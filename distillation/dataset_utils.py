import os
import sys
import time
import random
import argparse
import numpy as np
import json
from PIL import Image

import torch
import torch.nn as nn
import torchvision
import torchvision.datasets as datasets
import torchvision.transforms as transforms
from torchvision.utils import save_image, make_grid
from torch.utils.data import Dataset, DataLoader

class IndexedImageFolder(datasets.ImageFolder):
    def __getitem__(self, index):
        path, target = self.samples[index]
        sample = self.loader(path)
        if self.transform is not None:
            sample = self.transform(sample)
        return sample, target, index
      
class ImageTextDataset(Dataset):
    def __init__(self, json_file, image_root_dir, transform=None):
        self.image_root_dir = image_root_dir
        self.transform = transform

        self.data = []
        with open(json_file, 'r') as f:
            for line in f:
                item = json.loads(line.strip())
                self.data.append(item)
        
        self.label_to_idx = {}
        self._create_label_mapping()
    
    def _create_label_mapping(self):
        labels = set()
        for item in self.data:
            label = item['file_name'].split('/')[0]
            labels.add(label)
        
        labels = sorted(list(labels))
        self.label_to_idx = {label: idx for idx, label in enumerate(labels)}
        self.idx_to_label = {idx: label for label, idx in self.label_to_idx.items()}
    
    def __len__(self):
        return len(self.data)
    
    def __getitem__(self, idx):
        item = self.data[idx]
        
        img_path = os.path.join(self.image_root_dir, item['file_name'])
        
        try:
            image = Image.open(img_path).convert('RGB')
        except:
            image = Image.new('RGB', (224, 224), color='black')
            
        if self.transform:
            image = self.transform(image)
        label_str = item['file_name'].split('/')[0]
        label = self.label_to_idx[label_str]
        
        text = item['text']
        
        full_img_path = img_path
        
        return {
            'image': image,
            'image_path': full_img_path,
            'text': text,
            'label': label,
            'label_str': label_str, 
            'index': idx
        }

def collate_fn(batch):
    """
    Custom batch functions
    """
    images = torch.stack([item['image'] for item in batch])
    image_paths = [item['image_path'] for item in batch]
    texts = [item['text'] for item in batch]
    labels = torch.tensor([item['label'] for item in batch])
    label_strs = [item['label_str'] for item in batch]
    idxs = [item['index'] for item in batch]
    
    return {
        'images': images,
        'image_paths': image_paths,
        'texts': texts,
        'labels': labels,
        'label_strs': label_strs,
        'indexs': idxs
    }


def load_dataset_ours(args):
    transform_train = transforms.Compose([
        transforms.Resize((args.size, args.size)),
        transforms.ToTensor(),
    ])
    dataset = ImageTextDataset(args.metajson_file, os.path.join(args.data_dir, 'train'), transform=transform_train)
    dataloader = DataLoader(
        dataset, 
        batch_size=args.batch_size, 
        shuffle=True, 
        num_workers=4,
        collate_fn=collate_fn,
        pin_memory=True
    )
    return dataloader

def load_dataset(args):
    # Obtain dataloader
    transform_train = transforms.Compose([
        transforms.Resize((args.size, args.size)),
        transforms.ToTensor(),
    ])
    if args.dataset == 'cifar10':
        transform_test = transforms.Compose([
            transforms.ToTensor(),
            transforms.Normalize((0.491, 0.482, 0.447), (0.202, 0.199, 0.201))
        ])
        trainset = IndexedImageFolder(root=args.data_dir + "/train", 
                                        transform=transform_train)

    elif args.dataset == 'cifar100':
        transform_test = transforms.Compose([
            transforms.ToTensor(),
            transforms.Normalize((0.507, 0.486, 0.441), (0.267, 0.256, 0.276))
        ])
        trainset = datasets.CIFAR100(root=args.data_dir, train=True, download=False,
                                    transform=transform_train)

    elif args.dataset in ['imagenet', 'imagewoof', 'imagenette', 'imageidc']:
        transform_test = transforms.Compose([
            transforms.Resize(256),
            transforms.CenterCrop(224),
            transforms.ToTensor(),
            transforms.Normalize([0.485, 0.456, 0.406], [0.229, 0.224, 0.225])
        ])
        trainset = IndexedImageFolder(root=args.data_dir + "/train", 
                                        transform=transform_train)
                       
    elif args.dataset == 'tiny_imagenet':
        transform_test = transforms.Compose([
            transforms.ToTensor(),
            transforms.Normalize([0.485, 0.456, 0.406], [0.229, 0.224, 0.225])
        ])
        trainset = IndexedImageFolder(root=args.data_dir + "/train", 
                                        transform=transform_train)



    trainloader = torch.utils.data.DataLoader(
        trainset, batch_size=args.batch_size, shuffle=True,
        num_workers=args.num_workers, drop_last=False
    )

    path_all = [path for path, _ in trainset.samples]

    return trainloader, path_all


def gen_label_list(args):
    # obtain label-prompt list
    with open(args.label_file_path, "r") as f:
        lines = f.readlines()

    labels = []
    for line in lines:
        line = line.strip()
        label = line.split('\t')[0]
        labels.append(label)
    
    return labels

def set_random_seed(seed):
    random.seed(seed)
    np.random.seed(seed)
    torch.manual_seed(seed)
    torch.cuda.manual_seed(seed)
    torch.cuda.manual_seed_all(seed)
    torch.backends.cudnn.deterministic = True
    torch.backends.cudnn.benchmark = False
