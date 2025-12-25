from flask import Flask, request, jsonify
from flask_cors import CORS
import time
import logging
import os

# Set up professional logging
logging.basicConfig(
    level=logging.INFO,
    format='%(asctime)s - %(name)s - %(levelname)s - %(message)s'
)
logger = logging.getLogger(__name__)

app = Flask(__name__)

# Enable CORS - Essential if you ever want to test your API from a web browser
CORS(app)

# Configuration from Environment Variables (Production Best Practice)
DEBUG_MODE = os.environ.get('FLASK_DEBUG', 'True') == 'True'
DEFAULT_WIDTH = int(os.environ.get('DEFAULT_WIDTH', 1024))
DEFAULT_HEIGHT = int(os.environ.get('DEFAULT_HEIGHT', 1024))

@app.route('/')
def home():
    """Health check endpoint to verify server status."""
    return jsonify({
        "status": "Online", 
        "service": "AI Art API ",
        "version": "1.2.0",
        "timestamp": time.ctime()
    })

@app.route('/generate', methods=['POST'])
def generate_image():
    """
    Advanced endpoint to generate an AI image URL.
    Supports optional parameters: width, height, and model.
    """
    start_time = time.time()

    # 1. Parse and Validate Data
    data = request.get_json(silent=True)
    if not data or 'prompt' not in data:
        return jsonify({
            "status": "error", 
            "message": "Missing required field: 'prompt'"
        }), 400

    prompt = data.get('prompt', '').strip()
    if not prompt:
        return jsonify({
            "status": "error", 
            "message": "Prompt content cannot be empty"
        }), 400

    # 2. Extract Optional Parameters
    width = data.get('width', DEFAULT_WIDTH)
    height = data.get('height', DEFAULT_HEIGHT)
    model = data.get('model', 'flux') # Defaulting to the high-quality Flux model

    logger.info(f"Processing request - Model: {model}, Prompt: {prompt[:30]}...")

    # 3. URL Construction and Encoding
    # Pollinations supports model selection: 'flux', 'turbo', etc.
    clean_prompt = prompt.replace(" ", "%20")
    seed = int(time.time() * 1000) # Higher precision seed
    
    image_url = (
        f"https://image.pollinations.ai/prompt/{clean_prompt}"
        f"?seed={seed}"
        f"&width={width}"
        f"&height={height}"
        f"&model={model}"
        f"&nologo=true"
    )

    # 4. Finalizing Response
    duration = round(time.time() - start_time, 3)
    logger.info(f"SUCCESS: Generated {model} image URL in {duration}s")

    return jsonify({
        "status": "success",
        "image_url": image_url,
        "request_details": {
            "prompt": prompt,
            "dimensions": f"{width}x{height}",
            "model_used": model,
            "seed": seed
        },
        "performance": {
            "latency_ms": int(duration * 1000),
            "engine": "Pollinations.ai"
        }
    })

@app.errorhandler(400)
def bad_request(e):
    return jsonify({"status": "error", "message": "Bad request - check your JSON format"}), 400

@app.errorhandler(404)
def not_found(e):
    return jsonify({"status": "error", "message": "Endpoint not found"}), 404

@app.errorhandler(500)
def server_error(e):
    return jsonify({"status": "error", "message": "Internal server error"}), 500

if __name__ == '__main__':
    # Using environment variable for Port (Required for Render/Heroku deployment)
    port = int(os.environ.get('PORT', 5000))
    app.run(debug=DEBUG_MODE, port=port, host='0.0.0.0')
