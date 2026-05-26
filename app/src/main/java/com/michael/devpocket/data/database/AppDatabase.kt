package com.michael.devpocket.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [CodeFile::class, RegexSession::class, ScriptSnippet::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun codeFileDao(): CodeFileDao
    abstract fun regexSessionDao(): RegexSessionDao
    abstract fun scriptSnippetDao(): ScriptSnippetDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        @androidx.annotation.VisibleForTesting
        internal var inMemoryForTests: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            inMemoryForTests?.let { return it }
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "devpocket_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }

        fun resetForTests() {
            inMemoryForTests?.close()
            inMemoryForTests = null
        }
    }
}
