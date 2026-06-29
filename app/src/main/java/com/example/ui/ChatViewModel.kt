package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.AppDatabase
import com.example.data.ChatRepository
import com.example.data.Message
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Locale

class ChatViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: ChatRepository

    val messages: StateFlow<List<Message>>
    
    private val _isTyping = MutableStateFlow(false)
    val isTyping: StateFlow<Boolean> = _isTyping.asStateFlow()

    init {
        val database = AppDatabase.getDatabase(application)
        repository = ChatRepository(database.messageDao())
        
        // Expose messages flow from database
        messages = repository.allMessages.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

        // Prepopulate database with "Sleek Interface" HTML messages if empty
        viewModelScope.launch {
            if (repository.getMessageCount() == 0) {
                prepopulateDefaultMessages()
            }
        }
    }

    private suspend fun prepopulateDefaultMessages() {
        val now = System.currentTimeMillis()
        // Subtract time to make them look recent
        repository.insert(
            Message(
                text = "Hey! How is the new chat interface coming along? Have you tried the MD3 colors?",
                timestamp = now - 360000, // 6 mins ago
                isSent = false
            )
        )
        repository.insert(
            Message(
                text = "It's looking great. I just finished the fluid layout for the viewport. Check this out!",
                timestamp = now - 240000, // 4 mins ago
                isSent = true
            )
        )
        repository.insert(
            Message(
                text = "The dynamic rounding on the bubbles feels very native. Good job on the Material You palette.",
                timestamp = now - 120000, // 2 mins ago
                isSent = false
            )
        )
        repository.insert(
            Message(
                text = "Exactly what I was aiming for! No dead space anywhere.",
                timestamp = now - 60000, // 1 min ago
                isSent = true
            )
        )
    }

    fun sendMessage(text: String) {
        if (text.isBlank()) return

        viewModelScope.launch {
            // 1. Save user's message
            val userMessage = Message(text = text, isSent = true)
            repository.insert(userMessage)

            // 2. Trigger typing reply
            _isTyping.value = true
            delay(1500) // Realistic reading and typing delay
            _isTyping.value = false

            // 3. Select responsive reply
            val replyText = selectReplyForMessage(text)
            val replyMessage = Message(text = replyText, isSent = false)
            repository.insert(replyMessage)
        }
    }

    private fun selectReplyForMessage(userText: String): String {
        val textLower = userText.lowercase(Locale.ROOT)
        return when {
            textLower.contains("ios") || textLower.contains("android") || textLower.contains("cross") -> {
                "Kotlin Multiplatform (KMP) is perfect for Android and iOS chat apps! You share the database and business logic, then build native Compose for Android and SwiftUI for iOS."
            }
            textLower.contains("design") || textLower.contains("theme") || textLower.contains("sleek") || textLower.contains("color") -> {
                "I absolutely love this 'Sleek Interface' style! The light background `#FDFBFF` paired with the vibrant royal blue accent `#005AC1` and dynamic bubble curves is incredibly modern."
            }
            textLower.contains("kotlin") || textLower.contains("compose") -> {
                "Jetpack Compose makes building slick UI so rewarding. Things like custom rounded corners (e.g., leaving one corner square) and Spring physics bring the chat UI to life!"
            }
            textLower.contains("help") || textLower.contains("how to") -> {
                "This project persists all conversation history in SQLite via Room, runs UI asynchronously using Coroutines and StateFlow, and strictly adheres to Material 3 design."
            }
            textLower.contains("hello") || textLower.contains("hi") || textLower.contains("hey") -> {
                "Hey there! Alex Rivera here. I'm testing our newly developed 'Sleek Chat' interface. How are you liking the bubble curves?"
            }
            textLower.contains("room") || textLower.contains("database") || textLower.contains("save") -> {
                "Yes, our entire chat history is safely stored in a local SQLite database using Room. You can close and reopen the app, and our history remains preserved!"
            }
            textLower.contains("clear") || textLower.contains("delete") || textLower.contains("reset") -> {
                "If you want to clear our chat history, you can tap the 'Delete' icon in the top header. It will clear all messages so we can start fresh!"
            }
            else -> {
                val genericReplies = listOf(
                    "That makes total sense! Have you checked out the gesture navigation integration?",
                    "Exactly! No dead space, high-fidelity micro-interactions, and beautiful negative space.",
                    "Let's keep testing the chat. Type something else, and I'll keep replying!",
                    "I'm extremely impressed with the speed and responsiveness of this setup. What feature should we build next?",
                    "Ensuring touch target sizes of 48dp and correct WindowInsets for edge-to-edge support makes the experience feel incredibly premium."
                )
                genericReplies.random()
            }
        }
    }

    fun clearChat() {
        viewModelScope.launch {
            repository.clearChat()
        }
    }

    fun resetToDefault() {
        viewModelScope.launch {
            repository.clearChat()
            prepopulateDefaultMessages()
        }
    }

    class Factory(private val application: Application) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(ChatViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return ChatViewModel(application) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
