🤖 AI-Powered Mobile Art Generator (Android / Python)

This project is a high-performance, full-stack application designed to showcase modern mobile development (Android/Kotlin/Compose) integrated with real-time backend API logic (Python/Flask) and external Generative AI services.

The primary goal was to create a fast, consumer-grade app that provides instant, dynamic content based on user prompts and trending concepts.

✨ Key Features & Technical Highlights

Multimodal Architecture (Mobile + Backend): Demonstrates full-stack mobile development expertise by separating the presentation layer (Android) from the heavy processing layer (Python).

Real-Time Data Integration: Integrates Firebase Firestore as a backend-as-a-service (BaaS) to instantly deliver viral prompts and categories to the user interface via Kotlin Flow.

Asynchronous Performance Handling: Uses Kotlin Coroutines (LaunchedEffect) and Retrofit to manage network I/O and prevent UI freezing during long-running AI operations.

Custom UI/UX: Features a custom Jetpack Compose UI with a Dark/Cyberpunk theme, custom neon loading animation, and a gradient button, optimized for modern Android devices.

External AI Service Integration: Currently uses the Pollinations.ai open API for image generation. Future plans include upgrading to a dedicated enterprise solution.

Deployment Pipeline: The Python backend is container-ready and deployed on a professional PaaS (Render/Cloud Run).

| Component | Technology | Role | 
 | :--- | :--- | :--- | 
| **Frontend** | **Kotlin / Jetpack Compose** | Responsive UI, State Management (Compose States), and lifecycle handling. | 
| **Backend API** | **Python / Flask / Gunicorn** | Lightweight RESTful API, process isolation, and production-ready WSGI server. | 
| **AI Generation** | **Pollinations.ai** | Primary Text-to-Image generation model (simple URL-based API). | 
| **Data & Storage** | **Firebase Firestore** | Real-time backend to store and deliver trending prompt categories. | 
| **Networking** | **Retrofit / OkHttp** | Type-safe HTTP communication and custom 30-second image download timeout. | 
| **Image Loading** | **Coil** / **SubcomposeAsyncImage** | High-performance asynchronous image loading and custom animation handling. |

🚀 How to Run Locally

1. Prerequisites

Android Studio (Latest version, supporting Kotlin/Compose).

Python 3.8+ (with venv activated).

Your local machine's Firewall must allow connections on Port 5000.

NOTE: For full functionality, the Python server needs access to the public internet to call the Pollinations API.


### 2. Backend Setup

1. Navigate to the `/backend` directory.

2. Install dependencies:

   ```bash
   pip install -r requirements.txt


3. Run the Flask server:

   ```bash
   python app.py

3. Frontend Setup

  1. Configure API URL: In app/src/main/java/com/example/aigenerator/data/RetrofitClient.kt, set the BASE_URL to point to your development machine:
     ```bash
     private const val BASE_URL = "[http://10.0.2.2:5000/](http://10.0.2.2:5000/)" 

📂 Project Structure (Backend)

├── backend/
│   ├── app.py                  # Main Flask application and /generate endpoint
│   ├── requirements.txt        # Python dependencies (Flask, Gunicorn, requests)
│   ├── gunicorn_config.py      # Production server config (sets timeout to 60s)
│   └── .env                    # (Local file, not committed) Stores sensitive keys (currently unused)
├── Frontend/
│   ├── ...                     # Android project files (MainActivity, UIComponents)
│   └── data/
│       ├── Prompt.kt           # Data class for Firestore data model
│       └── FirestoreRepository.kt # Logic to fetch and expose prompts as StateFlow
└── README.md

✅ Roadmap & Future Features

[ ] AI Model Upgrade: Migrate the generation endpoint from the Pollinations.ai URL to a dedicated SDK (e.g., Hugging Face or another high-quality, stable provider).

[ ] Image Editing (I2I): Implement file upload functionality in the Android app and build a dedicated /edit endpoint in Python to perform image-to-image transformations.

[ ] User Authentication: Integrate Firebase Authentication for user accounts and private galleries.

[ ] Favorite Images: Implement Cloud Firestore storage to let users save their favorite generated images


Project created by Aryan Jadhao

