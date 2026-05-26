package com.michael.devpocket.data.database

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface CodeFileDao {
    @Query("SELECT * FROM code_files ORDER BY isPinned DESC, lastModified DESC")
    fun getAllFiles(): Flow<List<CodeFile>>

    @Query("SELECT * FROM code_files WHERE id = :id LIMIT 1")
    suspend fun getFileById(id: String): CodeFile?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveFile(vararg file: CodeFile)

    @Delete
    suspend fun deleteFile(file: CodeFile)

    @Query("DELETE FROM code_files WHERE id = :id")
    suspend fun deleteFileById(id: String)
}

@Dao
interface RegexSessionDao {
    @Query("SELECT * FROM regex_sessions ORDER BY createdAt DESC")
    fun getAllSessions(): Flow<List<RegexSession>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveSession(session: RegexSession)

    @Query("DELETE FROM regex_sessions WHERE id = :id")
    suspend fun deleteSessionById(id: String)
}

@Dao
interface ScriptSnippetDao {
    @Query("SELECT * FROM script_snippets ORDER BY createdAt DESC")
    fun getAllSnippets(): Flow<List<ScriptSnippet>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveSnippet(snippet: ScriptSnippet)

    @Query("DELETE FROM script_snippets WHERE id = :id")
    suspend fun deleteSnippetById(id: String)
}
