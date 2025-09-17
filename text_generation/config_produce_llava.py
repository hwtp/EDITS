import os
import json
# from classes import IMAGENET2012_CLASSES
import json

from collections import OrderedDict


current_working_dir = os.getcwd()

print(f"current dir is: {current_working_dir}")

current_dir = os.path.dirname(os.path.abspath(__file__))
json_file_path = os.path.join(current_dir, "IMAGENET2012_CLASSES.json")
with open(json_file_path, 'r', encoding='utf-8') as f:
    IMAGENET2012_CLASSES = json.load(f)

# Base directory where the subfolders are located
base_dir = '../../../share_dir/ImageWoof/train'
# base_dir = '../imagenet/train'
# Text and category which is the same for all

category = "detail"

# Initialize an empty list to hold the JSON objects
json_objects = []

# Iterate through each subfolder in the base directory
question_id = 0  # Initialize the question_id
save_dir = 'data/imageidc'
os.makedirs(save_dir, exist_ok=True)
with open(os.path.join(save_dir, 'output_vlcp.json'), 'w') as json_file:
    # Iterate through each subfolder in the base directory
    for subfolder in os.listdir(base_dir): 
        # ours
        text =  f"""Generate an extremely detailed and vivid caption for this image with sufficient tokens as much as possible. But the caption must be under 77 tokens. The image is of the "{IMAGENET2012_CLASSES[subfolder]}" and it must be mentioned in the caption. In other words, It/they cannot be mentioned in general terms (for example, there is a specific animal species, "Dingo" cannot be just called "dog")
                    If the label "{IMAGENET2012_CLASSES[subfolder]}" contains multiple descriptions with the same meaning, use only one of them.
                    
                    Follow this structure:
                    1. **Main Subject:** Describe the specific type, color, material, texture, size, and condition of the "{IMAGENET2012_CLASSES[subfolder]}".
                    2. **Setting & Background:** Where is it? Describe the environment, lighting, time of day, and surrounding objects.
                    3. **Action & Pose:** What is it doing? Describe its posture, movement, or expression.
    
                    Weave these elements into a dense, and fluent sentence. Use rich adjectives and precise nouns.
                    
                    Now, generate a detailed caption for this image of the lable "{IMAGENET2012_CLASSES[subfolder]}". Your caption must be under 77 tokens and must mention accurately the label "{IMAGENET2012_CLASSES[subfolder]}". OUTPUT ONLY THE CAPTION TEXT.
                   """
        # # vlcp 
        # text =  f"Describe the physical appearance of the {IMAGENET2012_CLASSES[subfolder]} in the image. Include details about its shape, posture, color, and any distinct features."                      
                
        subfolder_path = os.path.join(base_dir, subfolder)
        
        if os.path.isdir(subfolder_path):
            # Iterate through each image in the subfolder
            for image_file in os.listdir(subfolder_path):
                image_path = os.path.join(subfolder, image_file)
                # Create a JSON object for each image
                json_object = {
                    "question_id": question_id,  # Unique ID for each image
                    "image": image_path,
                    "text": text,
                    "category": category
                }
                # Write the JSON object to the file, each on a new line
                json_file.write(json.dumps(json_object) + '\n')
                question_id += 1  # Increment the question_id for the next image

print(f"JSON file generated with {question_id} entries.")