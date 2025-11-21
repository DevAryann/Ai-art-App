from flask import Flask, request, jsonify
import requests
import time

app = Flask(__name__)

@app.route('/')
def home():
    return jsonify({"status": "Online", "message": "AI Server is ready!"})

# This is the new Magic Endpoint
@app.route('/generate', methods=['POST'])
def generate_image():
    # 1. Get the text prompt from the Android App
    data = request.json
    prompt = data.get('prompt', 'A cute robot painting a picture') # Default prompt if none provided
    
    print(f"Received prompt: {prompt}")

    # 2. Construct the URL for Pollinations AI
    # We replace spaces with "%20" because URLs can't have spaces
    clean_prompt = prompt.replace(" ", "%20")
    
    # Pollinations generates the image directly at this URL
    # We add a random seed number so every image is unique
    seed = int(time.time())
    image_url = f"https://image.pollinations.ai/prompt/{clean_prompt}?seed={seed}"

    # 3. Send the URL back to Android
    return jsonify({
        "status": "success",
        "image_url": image_url
    })

if __name__ == '__main__':
    # host='0.0.0.0' allows the emulator to see the server
    app.run(debug=True, port=5000, host='0.0.0.0')