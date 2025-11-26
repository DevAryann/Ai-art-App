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

   
    clean_prompt = prompt.replace(" ", "%20")
    
   
    seed = int(time.time())
    image_url = f"https://image.pollinations.ai/prompt/{clean_prompt}?seed={seed}"

    
    return jsonify({
        "status": "success",
        "image_url": image_url
    })

if __name__ == '__main__':
    
    app.run(debug=True, port=5000, host='0.0.0.0')