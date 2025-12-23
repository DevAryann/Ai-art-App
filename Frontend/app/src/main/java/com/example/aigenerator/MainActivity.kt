package com.example.aigenerator

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.ImageLoader
import coil.compose.SubcomposeAsyncImage
import coil.request.ImageRequest
import com.example.aigenerator.data.FirestoreRepository
import com.example.aigenerator.data.Prompt
import com.example.aigenerator.ui.theme.AiGeneratorTheme
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import java.util.concurrent.TimeUnit

// --- DATA MODELS ---
data class MainUiState(
    val promptText: String = "",
    val imageUrl: String = "",
    val isLoading: Boolean = false,
    val error: String? = null
)

data class GenerateRequest(val prompt: String)
data class GenerateResponse(val image_url: String)

// --- VIEWMODEL: The Brain of the App ---
class MainViewModel : ViewModel() {
    private val repository = FirestoreRepository()
    
    private val _uiState = MutableStateFlow(MainUiState())
    val uiState: StateFlow<MainUiState> = _uiState

    val promptList = repository.prompts

    init {
        viewModelScope.launch {
            repository.fetchPrompts()
        }
    }

    fun onPromptChange(newPrompt: String) {
        _uiState.value = _uiState.value.copy(promptText = newPrompt)
    }

    fun generateArt() {
        val currentPrompt = _uiState.value.promptText
        if (currentPrompt.isBlank()) return

        _uiState.value = _uiState.value.copy(isLoading = true, error = null)
        
        viewModelScope.launch {
            try {
                // Assuming RetrofitClient is defined in your project
                val request = GenerateRequest(prompt = currentPrompt)
                val response = RetrofitClient.api.generateImage(request)
                _uiState.value = _uiState.value.copy(
                    imageUrl = response.image_url,
                    isLoading = false
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "Connection failed. Please check your server."
                )
            }
        }
    }
}

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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(viewModel: MainViewModel = viewModel()) {
    val state by viewModel.uiState.collectAsState()
    val promptList by viewModel.promptList.collectAsState()
    val context = LocalContext.current

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

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                        MaterialTheme.colorScheme.background
                    )
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp)
                .statusBarsPadding(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "Baba Art Studio",
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight.ExtraBold,
                            letterSpacing = (-1).sp
                        ),
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "Powered by Pollinations AI",
                        style = MaterialTheme.typography.labelMedium,
                        color = Color.Gray
                    )
                }
                
                IconButton(
                    onClick = { /* Settings Action */ },
                    modifier = Modifier.background(MaterialTheme.colorScheme.surfaceVariant, CircleShape)
                ) {
                    Icon(Icons.Default.Person, contentDescription = "Profile")
                }
            }

            Spacer(modifier = Modifier.height(30.dp))

            // The Canvas Card
            Box(contentAlignment = Alignment.BottomEnd) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(350.dp)
                        .clip(RoundedCornerShape(28.dp)),
                    elevation = CardDefaults.cardElevation(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                        if (state.imageUrl.isNotEmpty()) {
                            SubcomposeAsyncImage(
                                model = ImageRequest.Builder(context)
                                    .data(state.imageUrl)
                                    .crossfade(true)
                                    .build(),
                                imageLoader = imageLoader,
                                modifier = Modifier.fillMaxSize(),
                                contentDescription = "AI Output",
                                loading = { NeonLoadingAnimation() },
                                error = { 
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Icon(Icons.Default.Warning, contentDescription = null, tint = Color.Red)
                                        Text("Failed to load", color = Color.Red)
                                    }
                                }
                            )
                        } else {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(
                                    Icons.Default.Brush, 
                                    contentDescription = null, 
                                    modifier = Modifier.size(48.dp),
                                    tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text("Awaiting your prompt...", color = Color.Gray)
                            }
                        }

                        if (state.isLoading) {
                            NeonLoadingAnimation()
                        }
                    }
                }

                // Share FAB
                AnimatedVisibility(
                    visible = state.imageUrl.isNotEmpty() && !state.isLoading,
                    enter = scaleIn() + fadeIn(),
                    exit = fadeOut()
                ) {
                    FloatingActionButton(
                        onClick = {
                            val intent = Intent(Intent.ACTION_SEND).apply {
                                type = "text/plain"
                                putExtra(Intent.EXTRA_TEXT, "Check out this AI art: ${state.imageUrl}")
                            }
                            context.startActivity(Intent.createChooser(intent, "Share via"))
                        },
                        modifier = Modifier.padding(16.dp),
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        shape = CircleShape
                    ) {
                        Icon(Icons.Default.Share, contentDescription = "Share")
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Style Chips
            if (promptList.isNotEmpty()) {
                Text(
                    text = "Popular Styles",
                    style = MaterialTheme.typography.titleSmall,
                    modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
                )
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(promptList) { prompt ->
                        FilterChip(
                            selected = state.promptText == prompt.text,
                            onClick = { viewModel.onPromptChange(prompt.text) },
                            label = { Text(prompt.category) },
                            leadingIcon = {
                                Icon(
                                    if (state.promptText == prompt.text) Icons.Default.Check else Icons.Default.AutoAwesome,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            // Error Display
            state.error?.let {
                Text(it, color = Color.Red, style = MaterialTheme.typography.bodySmall)
                Spacer(modifier = Modifier.height(8.dp))
            }

            // Prompt Input
            OutlinedTextField(
                value = state.promptText,
                onValueChange = { viewModel.onPromptChange(it) },
                placeholder = { Text("A neon city in the clouds...") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                trailingIcon = {
                    if (state.promptText.isNotEmpty()) {
                        IconButton(onClick = { viewModel.onPromptChange("") }) {
                            Icon(Icons.Default.Clear, contentDescription = "Clear")
                        }
                    }
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Main Action Button
            ModernGradientButton(
                text = if (state.isLoading) "Processing..." else "Generate Art",
                isLoading = state.isLoading,
                onClick = { viewModel.generateArt() },
                modifier = Modifier.fillMaxWidth().height(56.dp)
            )
            
            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}
