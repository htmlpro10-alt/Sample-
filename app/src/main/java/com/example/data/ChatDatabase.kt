package com.example.data

import android.content.Context
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase
import kotlinx.coroutines.flow.Flow

@Entity(tableName = "messages")
data class Message(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val text: String,
    val timestamp: Long = System.currentTimeMillis(),
    val isSent: Boolean, // true if sent by user, false if received from Alex Rivera
    val status: String = "read" // "sent", "read"
)

@Dao
interface MessageDao {
    @Query("SELECT * FROM messages ORDER BY timestamp ASC")
    fun getAllMessages(): Flow<List<Message>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessage(message: Message): Long

    @Query("DELETE FROM messages WHERE id = :id")
    suspend fun deleteMessage(id: Long)

    @Query("DELETE FROM messages")
    suspend fun clearAllMessages()
    
    @Query("SELECT COUNT(*) FROM messages")
    suspend fun getMessageCount(): Int
}

@Database(entities = [Message::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun messageDao(): MessageDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "sleek_chat_database"
                )
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}

class ChatRepository(private val messageDao: MessageDao) {
    val allMessages: Flow<List<Message>> = messageDao.getAllMessages()

    suspend fun getMessageCount(): Int {
        return messageDao.getMessageCount()
    }

    suspend fun insert(message: Message): Long {
        return messageDao.insertMessage(message)
    }

    suspend fun deleteById(id: Long) {
        messageDao.deleteMessage(id)
    }

    suspend fun clearChat() {
        messageDao.clearAllMessages()
    }
}
