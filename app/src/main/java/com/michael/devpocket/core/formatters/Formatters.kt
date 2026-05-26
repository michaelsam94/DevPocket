package com.michael.devpocket.core.formatters

import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

object Formatters {

    fun formatJson(input: String, prettify: Boolean): FormatResult {
        if (input.trim().isEmpty()) {
            return FormatResult.Error("Input is empty")
        }
        return try {
            val trimmed = input.trim()
            val formatted = if (trimmed.startsWith("[")) {
                val array = JSONArray(trimmed)
                if (prettify) array.toString(4) else array.toString()
            } else {
                val obj = JSONObject(trimmed)
                if (prettify) obj.toString(4) else obj.toString()
            }
            FormatResult.Success(formatted)
        } catch (e: JSONException) {
            FormatResult.Error(e.message ?: "Invalid JSON syntax")
        }
    }

    fun formatXml(input: String, prettify: Boolean): FormatResult {
        val trimmed = input.trim()
        if (trimmed.isEmpty()) return FormatResult.Error("Input is empty")
        return try {
            if (prettify) {
                val result = StringBuilder()
                var indentLevel = 0
                val lines = trimmed
                    .replace("><", ">\n<")
                    .split("\n")
                
                for (line in lines) {
                    val currentLine = line.trim()
                    if (currentLine.isEmpty()) continue
                    
                    if (currentLine.startsWith("</")) {
                        indentLevel = (indentLevel - 1).coerceAtLeast(0)
                    }
                    
                    result.append("    ".repeat(indentLevel)).append(currentLine).append("\n")
                    
                    if (currentLine.startsWith("<") && !currentLine.startsWith("</") && !currentLine.endsWith("/>") && !currentLine.startsWith("<?")) {
                        // Check if it's a simple element with text, e.g., <tag>text</tag>
                        val isSimpleElement = currentLine.contains("</")
                        if (!isSimpleElement) {
                            indentLevel++
                        }
                    }
                }
                FormatResult.Success(result.toString().trimEnd())
            } else {
                // Minify XML
                val minified = trimmed
                    .replace(Regex(">\\s+<"), "><")
                    .replace(Regex("(?s)<!--.*?-->"), "") // Remove comments
                FormatResult.Success(minified)
            }
        } catch (e: Exception) {
            FormatResult.Error(e.message ?: "Failed to process XML")
        }
    }

    fun formatHtml(input: String, prettify: Boolean): FormatResult = formatXml(input, prettify) // Similar tag-basing logic

    fun formatCss(input: String, prettify: Boolean): FormatResult {
        val trimmed = input.trim()
        if (trimmed.isEmpty()) return FormatResult.Error("Input is empty")
        return try {
            if (prettify) {
                val result = StringBuilder()
                var indentLevel = 0
                val tokens = trimmed
                    .replace("{", " {\n")
                    .replace("}", "\n}\n")
                    .replace(";", ";\n")
                    .split("\n")

                for (token in tokens) {
                    val line = token.trim()
                    if (line.isEmpty()) continue

                    if (line.startsWith("}")) {
                        indentLevel = (indentLevel - 1).coerceAtLeast(0)
                    }

                    result.append("    ".repeat(indentLevel)).append(line).append("\n")

                    if (line.endsWith("{")) {
                        indentLevel++
                    }
                }
                FormatResult.Success(result.toString().trim().replace(Regex("\n\\s*\n"), "\n"))
            } else {
                // Minify CSS
                val minified = trimmed
                    .replace(Regex("\\s*\\{\\s*"), "{")
                    .replace(Regex("\\s*\\}\\s*"), "}")
                    .replace(Regex("\\s*;\\s*"), ";")
                    .replace(Regex("\\s*:\\s*"), ":")
                    .replace(Regex("/\\*.*?\\*/"), "") // Remove CSS comments
                FormatResult.Success(minified)
            }
        } catch (e: Exception) {
            FormatResult.Error(e.message ?: "Failed to process CSS")
        }
    }

    fun minifySql(input: String): FormatResult {
        val trimmed = input.trim()
        if (trimmed.isEmpty()) return FormatResult.Error("Input is empty")
        return try {
            // Remove single line comments
            var cleaned = trimmed.replace(Regex("--.*"), "")
            // Remove multi line comments
            cleaned = cleaned.replace(Regex("(?s)/\\*.*?\\*/"), "")
            // Coalesce whitespaces
            val minified = cleaned.replace(Regex("\\s+"), " ")
            FormatResult.Success(minified)
        } catch (e: Exception) {
            FormatResult.Error(e.message ?: "Failed to minify SQL")
        }
    }
}

sealed class FormatResult {
    data class Success(val formatted: String) : FormatResult()
    data class Error(val message: String) : FormatResult()
}
