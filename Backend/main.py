from flask import Flask, request, jsonify
import time
import logging

# Set up professional logging
logging.basicConfig(level=logging.INFO)
logger = logging.getLogger(__name__)

app = Flask(__name__)

@app.route('/')
def home():
    """Health check endpoint to verify server status."""
    return jsonify({
        "status": "Online", 
        "service": "Baba Art API",
        "timestamp": time.ctime()
    })

@app.route('/generate', methods=['POST'])
def generate_image():
    """
    Endpoint to generate an AI image URL via Pollinations.ai.
    Expects JSON: {"prompt": "your description"}
    """
    start_time = time.time()

    # 1. Parse and Validate Data
    data = request.get_json(silent=True)
    if not data or 'prompt' not in data:
        return jsonify({"status": "error", "message": "Missing prompt in request body"}), 400

    prompt = data.get('prompt', '').strip()
    
    if not prompt:
        return jsonify({"status": "error", "message": "Prompt cannot be empty"}), 400

    logger.info(f"Generating art for: {prompt}")

    # 2. URL Encoding (Replace spaces with %20)
    clean_prompt = prompt.replace(" ", "%20")
    
    # Use a unique seed based on time to ensure fresh generation
    seed = int(time.time())
    image_url = f"https://image.pollinations.ai/prompt/{clean_prompt}?seed={seed}&width=1024&height=1024&nologo=true"

    # 3. Performance Metrics
    duration = round(time.time() - start_time, 2)
    logger.info(f"SUCCESS: Created URL in {duration}s")

    # 4. Return Structured Response
    return jsonify({
        "status": "success",
        "image_url": image_url,
        "metadata": {
            "duration_seconds": duration,
            "seed": seed,
            "engine": "Pollinations.ai"
        }
    })

@app.errorhandler(404)
def not_found(e):
    return jsonify({"status": "error", "message": "Endpoint not found"}), 404

if __name__ == '__main__':
    # host='0.0.0.0' allows external connections (like your Android device)
    app.run(debug=True, port=5000, host='0.0.0.0')
