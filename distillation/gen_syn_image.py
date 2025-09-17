from diffusers import StableDiffusionLatents2ImgPipeline, DDPMScheduler, DDIMScheduler

import torch
import torchvision  
from torchvision import transforms
from diffusers import AutoencoderKL
import argparse
from dataset_utils import *
import json
import os
from tqdm import tqdm
import torch.nn.functional as F
from dataset_utils import *
from torchvision.utils import save_image
from classes import IMAGENET2012_CLASSES

import ipdb



def parse_args():
    parser = argparse.ArgumentParser()
    parser.add_argument('--batch_size', default=10, type=int, 
                        help='batch size')
    parser.add_argument('--diffusion_checkpoints_path', default="stablediffusion/checkpoints/stable-diffusion-v1-5", type=str, 
                        help='path to stable diffusion model from pretrained')
    parser.add_argument('--vae_checkpoints_path', default="pretrain_model/sd-vae-ft-mse", type=str, 
                        help='path to vae model from pretrained')
    parser.add_argument('--dataset', default='cifar10', type=str, 
                        help='data prepare to distillate')
    parser.add_argument('--guidance_scale', '-g', default=8, type=float, 
                        help='diffusers guidance_scale')
    parser.add_argument('--ipc', default=1, type=int, 
                        help='image per class')
    parser.add_argument('--km_expand', default=10, type=int, 
                        help='expand ration for minibatch k-means model')
    parser.add_argument('--label_file_path', default='data/imagenet_classes.txt', type=str, 
                        help='root dir')
    parser.add_argument('--image_prototype', default='prototypes/imagenet-ipc1-kmexpand1.json', type=str, 
                        help='image prototype')
    parser.add_argument('--image_prototype_path', default='prototypes/imagenet-ipc1-kmexpand1.json', type=str, 
                        help='image prototype path')
    parser.add_argument('--text_prototype', default='prototypes/imagenet-ipc1-kmexpand1.json', type=str, 
                        help='text prototype path')
    parser.add_argument('--save_init_image_path', default='data/init_data/random', type=str, 
                        help='where to save the generated prototype json files')
    parser.add_argument('--strength', '-s', default=0.75, type=float, 
                        help='diffusers strength')
    parser.add_argument('--seed', default=0, type=int, 
                        help='seed')   
    parser.add_argument('--method', default='ours', type=str, 
                        help='method') 
    parser.add_argument('--do_extral_guidance', default=0, type=int, 
                        help='method')
    args = parser.parse_args()
    return args

def load_image_prototype(args):
    prototype_file_path = args.image_prototype
    with open(prototype_file_path, 'r') as f:
        prototype = json.load(f)

    for prompt, data in prototype.items():
        prototype[prompt] = torch.tensor(data, dtype=torch.float16).to(args.device)
    print("image prototype loaded.")
    return prototype

def load_text_prototype(args):
    prototype_file_path = args.text_prototype
    with open(prototype_file_path, 'r') as f:
        prototype = json.load(f)

    for prompt, data in prototype.items():
        prototype[prompt] = data
    print("text prototype loaded.")
    return prototype

def get_pipeline_embeds(pipeline, prompt, negative_prompt, device):
    """ Get pipeline embeds for prompts bigger than the maxlength of the pipe
    :param pipeline:
    :param prompt:
    :param negative_prompt:
    :param device:
    :return:
    """
    max_length = pipeline.tokenizer.model_max_length

    # simple way to determine length of tokens
    count_prompt = len(prompt.split(" "))
    count_negative_prompt = len(negative_prompt.split(" "))

    # create the tensor based on which prompt is longer
    if count_prompt >= count_negative_prompt:
        input_ids = pipeline.tokenizer(prompt, return_tensors="pt", truncation=False).input_ids.to(device)
        shape_max_length = input_ids.shape[-1]
        negative_ids = pipeline.tokenizer(negative_prompt, truncation=False, padding="max_length",
                                          max_length=shape_max_length, return_tensors="pt").input_ids.to(device)

    else:
        negative_ids = pipeline.tokenizer(negative_prompt, return_tensors="pt", truncation=False).input_ids.to(device)
        shape_max_length = negative_ids.shape[-1]
        input_ids = pipeline.tokenizer(prompt, return_tensors="pt", truncation=False, padding="max_length",
                                       max_length=shape_max_length).input_ids.to(device)

    concat_embeds = []
    neg_embeds = []
    for i in range(0, shape_max_length, max_length):
        concat_embeds.append(pipeline.text_encoder(input_ids[:, i: i + max_length])[0])
        neg_embeds.append(pipeline.text_encoder(negative_ids[:, i: i + max_length])[0])

    return torch.cat(concat_embeds, dim=1), torch.cat(neg_embeds, dim=1)


def image_to_latent(image_path, vae_model, device):
    image = Image.open(image_path).convert('RGB')
    
    image = image.resize((512, 512), resample=Image.LANCZOS)
    image = np.array(image).astype(np.float32) / 255.0
    image = image[None].transpose(0, 3, 1, 2)
    image_tensor = torch.from_numpy(image)
    image_tensor = 2.0 * image_tensor - 1.0
    image_tensor = image_tensor.to(device)
    
    if vae_model.dtype == torch.float16:
        image_tensor = image_tensor.half()

    with torch.no_grad():
        latent_dist = vae_model.encode(image_tensor).latent_dist
        latent = latent_dist.mode()
        latent = latent * vae_model.config.scaling_factor
    return latent.cpu()  

def load_image_prototype_ours(json_file_path, vae_model, device):
    with open(json_file_path, 'r') as f:
        image_dict = json.load(f)
    
    latent_dict = {}
    
    for key, image_paths in image_dict.items():
        latent_list = []
        
        for img_paths_group in image_paths:
            try:
                # latent = image_to_latent(img_paths_group, vae_model, device)
                # latent_list.append(latent.numpy().tolist())  
                group_latents = []
                for img_path in img_paths_group:
                    latent = image_to_latent(img_path, vae_model, device)
                    group_latents.append(latent)
            
                avg_latent = torch.stack(group_latents).mean(dim=0)
                latent_list.append(avg_latent.numpy().tolist())
            except Exception as e:
                print(f"Error processing {img_path}: {e}")
                latent_list.append(None) 
        
        latent_dict[key] = latent_list
    for prompt, data in latent_dict.items():
        latent_dict[prompt] = torch.tensor(data, dtype=torch.float16).squeeze(1).to(device)
    return latent_dict


def gen_syn_images(pipe, image_prototype, label_list,text_prototype, args):
    for prompt, pros in tqdm(image_prototype.items(), total=len(image_prototype), position=0):

        assert  args.ipc % pros.size(0) == 0
        # text_prompt = IMAGENET2012_CLASSES[prompt]
        for j in range(int(args.ipc/(pros.size(0)))):
            for i in range(pros.size(0)):
                sub_pro = pros[i:i+1]
                text_prompt = text_prototype[prompt][i:i+1][0]
                sub_pro_random = torch.randn((1, 4, 64, 64), device='cuda',dtype=torch.half)
            
                negative_prompt = 'cartoon, anime, painting'
                
                print("Our inputs ", text_prompt, negative_prompt, len(prompt.split(" ")), len(negative_prompt.split(" ")))

                if args.method == 'ours':
                    images = pipe(prompt=text_prompt, negative_prompt=negative_prompt, latents=sub_pro, is_init=True, strength=args.strength, guidance_scale=args.guidance_scale, do_extral_guidance=args.do_extral_guidance).images
                else: 
                    prompt_embeds, negative_prompt_embeds = get_pipeline_embeds(pipe, text_prompt, negative_prompt, "cuda")
                    images = pipe(prompt_embeds=prompt_embeds, negative_prompt_embeds=negative_prompt_embeds, latents=sub_pro, is_init=True, strength=args.strength, guidance_scale=args.guidance_scale, do_extral_guidance=args.do_extral_guidance).images
                
                index = label_list.index(prompt)
                save_path = os.path.join(args.save_init_image_path, "train")
                os.makedirs(os.path.join(save_path, "{}/".format(prompt)), exist_ok=True)
                
                if 'cifar' in args.dataset:
                    images[0].resize((32, 32)).save(os.path.join(save_path, "{}/{}-image{}{}.png".format(prompt,prompt, i, j)))
                elif 'Image1K' in args.diffusion_checkpoints_path:
                    images[0].resize((224, 224)).save(os.path.join(save_path, "{}/{}-image{}{}.png".format(prompt,prompt, i, j)))
                elif 'tiny' in args.dataset:
                    images[0].resize((64, 64)).save(os.path.join(save_path, "{}/{}-image{}{}.png".format(prompt,prompt, i, j)))
                else:
                    images[0].resize((256, 256)).save(os.path.join(save_path, "{}/{}-image{}{}.png".format(prompt,prompt, i, j)))

def save_images(images, place_to_store):
    if not os.path.exists(os.path.dirname(place_to_store)):
        print(f"path is not exist: {os.path.dirname(place_to_store)}")
        os.makedirs(os.path.dirname(place_to_store))
    for clip_val in [2.5]:
        std = torch.std(images)
        mean = torch.mean(images)
        images = torch.clip(images, min=mean-clip_val*std, max=mean+clip_val*std)
    # image_np = images.data.cpu().numpy().transpose((1, 2, 0))
    # pil_image = Image.fromarray((image_np * 255).astype(np.uint8))
    # pil_image.save(place_to_store)
    save_image(images[0].unsqueeze(0), place_to_store, normalize=True)

def main():
    args = parse_args()
    set_random_seed(args.seed)
    args.device = 'cuda' if torch.cuda.is_available() else 'cpu'

    # 1.obtain label-prompt list
    label_dic = gen_label_list(args)

    # 2.define the diffusers pipeline
    pipe = StableDiffusionLatents2ImgPipeline.from_pretrained(args.diffusion_checkpoints_path, torch_dtype=torch.float16, safety_checker = None, requires_safety_checker = False)
    print(type(pipe.scheduler).__name__)
    
    # 3.load prototypes from json file
    print('method:', args.method)
    if args.method == 'ours':
        pipe.scheduler = DDIMScheduler.from_config(pipe.scheduler.config)   
        vae = AutoencoderKL.from_pretrained(args.vae_checkpoints_path).to(args.device)
        vae.eval()
        image_prototype = load_image_prototype_ours(args.image_prototype_path, vae, args.device)
    else:
        image_prototype = load_image_prototype(args)
    pipe = pipe.to(args.device)
    
    text_prototype = load_text_prototype(args)
    
    # 4.generate initialized synthetic images and save them for refine
    gen_syn_images(pipe=pipe, image_prototype=image_prototype, label_list=label_dic,text_prototype=text_prototype, args=args)


if __name__ == "__main__" : 
    main()
