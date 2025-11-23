package com.example.aigenerator

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.ImageLoader
import coil.compose.SubcomposeAsyncImage
import coil.request.ImageRequest
import com.example.aigenerator.data.FirestoreRepository
import com.example.aigenerator.ui.theme.AiGeneratorTheme
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import java.util.concurrent.TimeUnit

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AiGeneratorTheme {
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

// Initialize the repository (Database Connection)
val repository = FirestoreRepository()

@Composable
fun MainScreen() {
    // 1. FETCH DATA: Watch the "prompts" list from our database
    val promptList by repository.prompts.collectAsState()

    // 2. TRIGGER FETCH: When app opens, get data from Firestore immediately
    LaunchedEffect(Unit) {
        repository.fetchPrompts()
    }

    val context = LocalContext.current

    // Custom Image Loader with 30s Timeout for AI
    val imageLoader = remember {
        ImageLoader.Builder(context)
            .okHttpClient {
                OkHttpClient.Builder()
                    .connectTimeout(60, TimeUnit.SECONDS)
                    .readTimeout(60, TimeUnit.SECONDS)
                    .build()
            }
            .build()
    }

    // State variables
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
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(24.dp))

        // --- IMAGE DISPLAY AREA ---
        Card(
            modifier = Modifier
                .size(300.dp)
                .clip(RoundedCornerShape(16.dp)),
            elevation = CardDefaults.cardElevation(8.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                if (imageUrl.isNotEmpty()) {
                    SubcomposeAsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(imageUrl)
                            .crossfade(true)
                            .build(),
                        imageLoader = imageLoader,
                        modifier = Modifier.fillMaxSize(),
                        contentDescription = "Generated AI Image",
                        loading = { NeonLoadingAnimation() },
                        error = { Text("Failed to load", color = Color.Red) }
                    )
                } else {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Your art will appear here", color = Color.Gray)
                    }
                }

                if (isLoading && imageUrl.isEmpty()) {
                    NeonLoadingAnimation()
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // --- NEW FEATURE: VIRAL PROMPTS SCROLL BAR ---
        // Only show this if we actually have data from Firebase
        if (promptList.isNotEmpty()) {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "TRY THESE VIRAL PROMPTS:",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.Gray,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(start = 4.dp, bottom = 8.dp)
                )

                // The Horizontal Scroll List
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(promptList) { prompt ->
                        AssistChip(
                            onClick = {
                                // When clicked, AUTO-FILL the text box!
                                promptText = prompt.Text
                            },
                            label = { Text(prompt.category) },
                            leadingIcon = {
                                // Add a tiny icon for style
                                Icon(
                                    imageVector = androidx.compose.material.icons.Icons.Default.Star,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.secondary,
                                    modifier = Modifier.size(16.dp)
                                )
                            },
                            colors = AssistChipDefaults.assistChipColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant,
                                labelColor = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        )
                    }
                }
            }
        }
        // -------------------------------------------

        Spacer(modifier = Modifier.height(16.dp))

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
                            println("DEBUG_APP: Sending request to server...")
                            val request = GenerateRequest(prompt = promptText)
                            val response = RetrofitClient.api.generateImage(request)
                            println("DEBUG_APP: Success! URL: ${response.image_url}")
                            imageUrl = response.image_url
                        } catch (e: Exception) {
                            println("DEBUG_APP: ERROR - ${e.message}")
                            e.printStackTrace()
                        } finally {
                            isLoading = false
                        }
                    }
                }
            },
            modifier = Modifier.fillMaxWidth()
        )
    }
}