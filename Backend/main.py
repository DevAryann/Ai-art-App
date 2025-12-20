from flask import Flask, request, jsonify
from urllib.parse import quote
import time

app = Flask(__name__)

@app.route('/')
def home():
    return jsonify({
        "status": "Online",
        "message": "AI Server is ready!"
    })


@app.route('/health')
def health():
    return jsonify({
        "status": "ok",
        "timestamp": int(time.time())
    })


@app.route('/generate', methods=['POST'])
def generate_image():
    if not request.is_json:
        return jsonify({
            "status": "error",
            "message": "Request must be JSON"
        }), 400

    data = request.get_json()
    prompt = data.get('prompt', 'A cute robot painting a picture')

    print(f"Received prompt: {prompt}")

    encoded_prompt = quote(prompt)
    seed = int(time.time())

    image_url = (
        f"https://image.pollinations.ai/prompt/"
        f"{encoded_prompt}?seed={seed}"
    )

    return jsonify({
        "status": "success",
        "prompt": prompt,
        "image_url": image_url,
        "seed": seed
    })


if __name__ == '__main__':
    app.run(
        debug=True,
        port=5000,
        host='0.0.0.0'
    )
    
