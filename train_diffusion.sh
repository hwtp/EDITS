#### train diffusion model 
export ACCELERATE_USE_DEEPSPEED="false"
export CUDA_VISIBLE_DEVICES=2
export MODEL_NAME="pretrain_model/stable-diffusion-v1-5"
export TRAIN_DIR="/nfs4/share_dir/ImageWoof/train"

export OUTPUT_DIR="diffusers/imagewoof_seed0"
accelerate launch --num_processes=1 --main_process_port=29505 train_text_to_image.py   --pretrained_model_name_or_path=$MODEL_NAME   --train_data_dir=$TRAIN_DIR   --use_ema   --resolution=512 --center_crop --random_flip   --train_batch_size=8   --gradient_accumulation_steps=4   --gradient_checkpointing   --mixed_precision="fp16"      --learning_rate=1e-05   --max_grad_norm=1   --lr_scheduler="constant" --lr_warmup_steps=0   --output_dir=${OUTPUT_DIR} --num_train_epochs 8 --validation_epochs 2 --seed 0 --checkpoints_total_limit 2 --checkpointing_steps 500

export OUTPUT_DIR="diffusers/imagewoof_seed1"
accelerate launch --num_processes=1 --main_process_port=29506 train_text_to_image.py   --pretrained_model_name_or_path=$MODEL_NAME   --train_data_dir=$TRAIN_DIR   --use_ema   --resolution=512 --center_crop --random_flip   --train_batch_size=32   --gradient_accumulation_steps=4   --gradient_checkpointing   --mixed_precision="fp16"      --learning_rate=1e-05   --max_grad_norm=1   --lr_scheduler="constant" --lr_warmup_steps=0   --output_dir=${OUTPUT_DIR} --num_train_epochs 8 --validation_epochs 2 --seed 1 --checkpoints_total_limit 2 --checkpointing_steps 500