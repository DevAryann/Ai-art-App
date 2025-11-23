package com.example.aigenerator.data

import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.tasks.await // Add dependency for .await()

class FirestoreRepository {
    private val db = FirebaseFirestore.getInstance()
    private val _prompts = MutableStateFlow<List<Prompt>>(emptyList())
    val prompts: StateFlow<List<Prompt>> = _prompts

    // Public function to fetch data
    suspend fun fetchPrompts() {
        try {
            // Get all documents from the 'prompts' collection
            val snapshot = db.collection("prompts")
                .get()
                .await() // Wait for the network request to finish

            // Map the documents to our Kotlin data class
            val promptList = snapshot.documents.mapNotNull { document ->
                // Firestore automatically converts Map to Data Class using .toObject<T>()
                document.toObject(Prompt::class.java)
            }

            // Update the StateFlow to notify the UI
            _prompts.value = promptList

        } catch (e: Exception) {
            println("FIREBASE ERROR: Failed to fetch prompts: $e")
            // Optionally, set state to indicate error
        }
    }
}