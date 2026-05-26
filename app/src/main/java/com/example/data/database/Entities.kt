package com.example.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "code_files")
data class CodeFile(
    @PrimaryKey val id: String,
    val name: String,
    val language: String,
    val content: String,
    val lastModified: Long,
    val isPinned: Boolean = false
)

@Entity(tableName = "regex_sessions")
data class RegexSession(
    @PrimaryKey val id: String,
    val pattern: String,
    val flags: String, // Comma-separated list of active flags e.g. "g,i,m"
    val testInput: String,
    val createdAt: Long,
    val label: String? = null
)

@Entity(tableName = "script_snippets")
data class ScriptSnippet(
    @PrimaryKey val id: String,
    val title: String,
    val code: String,
    val language: String, // "javascript", "basic_math" etc.
    val createdAt: Long
)
