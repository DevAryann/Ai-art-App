package com.example.aigenerator // Keep your actual package name here!

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.* // Material Design 3 components
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import kotlinx.coroutines.launch
import coil.ImageLoader
import coil.request.ImageRequest
import androidx.compose.ui.platform.LocalContext
import coil.compose.SubcomposeAsyncImage
import com.example.aigenerator.ui.theme.AiGeneratorTheme
import okhttp3.OkHttpClient
import java.util.concurrent.TimeUnit

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            // USE YOUR CUSTOM THEME HERE
            AiGeneratorTheme {
                // A Surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MainScreen()
                }
            }
        }
    }
}

@Composable
fun MainScreen() {

    val context = LocalContext.current
    val imageLoader = remember {
        ImageLoader.Builder(context)
            .okHttpClient {
                OkHttpClient.Builder()
                    // Increase timeout to 30 seconds
                    .connectTimeout(30, TimeUnit.SECONDS)
                    .readTimeout(30, TimeUnit.SECONDS)
                    .build()
            }
            .build()
    }
    // State variables to control the UI
    var promptText by remember { mutableStateOf("") }
    var imageUrl by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "AI Art Generator",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(24.dp))

        // The Image Display Area
        Card(
            modifier = Modifier
                .size(300.dp)
                .clip(RoundedCornerShape(16.dp)),
            elevation = CardDefaults.cardElevation(8.dp)
        ) {
            Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                if (imageUrl.isNotEmpty()) {
                    // 🔴 SWITCH TO SUBCOMPOSE ASYNC IMAGE
                    SubcomposeAsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(imageUrl)
                            .crossfade(true)
                            .build(),
                        imageLoader = imageLoader, // Keep your 30s timeout loader!
                        modifier = Modifier.fillMaxSize(),
                        contentDescription = "Generated AI Image",

                        // 1. DEFINE THE LOADING STATE
                        loading = {
                            NeonLoadingAnimation() // <--- Your new cool animation
                        },

                        // 2. DEFINE THE ERROR STATE
                        error = {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Text("Failed to load", color = Color.Red)
                            }
                        }
                    )
                } else {
                    // This is the state BEFORE you click generate
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        // You can put an icon here if you want
                        Text(
                            "Your art will appear here",
                            color = Color.Gray
                        )
                    }
                }

                // OPTIONAL: If you want the animation to play while the SERVER is thinking
                // (Before Coil even gets the URL), you can overlay it here:
                if (isLoading && imageUrl.isEmpty()) {
                    NeonLoadingAnimation()
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // User Input
        OutlinedTextField(
            value = promptText,
            onValueChange = { promptText = it },
            label = { Text("Describe your image...") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Generate Button
        ModernGradientButton(
            text = "Generate Art",
            isLoading = isLoading,
            onClick = {
                if (promptText.isNotBlank()) {
                    isLoading = true
                    coroutineScope.launch {
                        try {
                            println("DEBUG_APP: Sending request to server...") // 1. Did we start?

                            val request = GenerateRequest(prompt = promptText)
                            val response = RetrofitClient.api.generateImage(request)

                            // 2. DID WE GET A URL?
                            println("DEBUG_APP: Success! Server sent this URL: ${response.image_url}")

                            imageUrl = response.image_url

                        } catch (e: Exception) {
                            // 3. OR DID WE CRASH?
                            println("DEBUG_APP: ERROR - ${e.message}")
                            e.printStackTrace()
                        } finally {
                            isLoading = false
                        }
                    }
                }
            },
            modifier = Modifier.fillMaxWidth(),

        )
    }
}

@Preview
@Composable
fun MainScreenPreview() {
    MainScreen()
}
