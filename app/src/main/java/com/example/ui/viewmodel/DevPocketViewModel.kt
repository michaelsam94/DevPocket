package com.example.ui.viewmodel

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.core.formatters.FormatResult
import com.example.core.formatters.Formatters
import com.example.core.script.ScriptExecutor
import com.example.data.database.AppDatabase
import com.example.data.database.CodeFile
import com.example.data.database.RegexSession
import com.example.data.database.ScriptSnippet
import com.example.data.docs.DocBundle
import com.example.data.docs.DocPage
import com.example.data.docs.DocumentationVault
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.UUID

class DevPocketViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getDatabase(application)
    private val fileDao = db.codeFileDao()
    private val regexDao = db.regexSessionDao()
    private val snippetDao = db.scriptSnippetDao()
    private val scriptExecutor = ScriptExecutor(application)

    // === CODE WORKSPACE STATE ===
    private val _codeFiles = MutableStateFlow<List<CodeFile>>(emptyList())
    val codeFiles: StateFlow<List<CodeFile>> = _codeFiles.asStateFlow()

    var activeFile by mutableStateOf<CodeFile?>(null)
        private set

    var editorText by mutableStateOf("")
    var editorLang by mutableStateOf("plainText") // "json", "xml", "html", "markdown", "css", "sql", "javascript", "plainText"
    var editorFileName by mutableStateOf("")
    
    // Editor preferences
    var fontSize by mutableStateOf(14)
    var wordWrap by mutableStateOf(true)
    var bracketAutoClose by mutableStateOf(true)
    
    // === FORMATTING SUITE STATE ===
    var formatterInput by mutableStateOf("")
    var formatterOutput by mutableStateOf("")
    var formatterLang by mutableStateOf("JSON") // "JSON", "XML", "HTML", "CSS", "SQL"
    var formatterError by mutableStateOf<String?>(null)
    var isFormatting by mutableStateOf(false)

    // === REGEX PLAYGROUND STATE ===
    var regexPattern by mutableStateOf("[a-zA-Z0-0._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}")
    var regexInput by mutableStateOf("Welcome to DevPocket! Contact support at support@devpocket.io or test admin@localhost.com.")
    var regexFlagsGlobal by mutableStateOf(true)
    var regexFlagsIgnoreCase by mutableStateOf(true)
    var regexFlagsMultiline by mutableStateOf(false)
    var regexFlagsDotAll by mutableStateOf(false)
    
    private val _regexMatches = MutableStateFlow<List<MatchResultWrapper>>(emptyList())
    val regexMatches: StateFlow<List<MatchResultWrapper>> = _regexMatches.asStateFlow()
    
    var regexMatchTimeMs by mutableStateOf(0L)
    var regexError by mutableStateOf<String?>(null)
    
    private val _regexSessions = MutableStateFlow<List<RegexSession>>(emptyList())
    val regexSessions: StateFlow<List<RegexSession>> = _regexSessions.asStateFlow()

    // === DOCUMENTATION VAULT STATE ===
    var docSearchQuery by mutableStateOf("")
    var selectedBundle by mutableStateOf<DocBundle?>(null)
    var selectedPage by mutableStateOf<DocPage?>(null)
    var bookmarkedPages by mutableStateOf<Set<String>>(emptySet()) // set of pageIds

    // === SCRIPT EXECUTION ENGINE STATE ===
    var consoleScript by mutableStateOf("""
        // DevPocket JavaScript Sandbox
        // Calculate Fibonacci sequence
        function fib(n) {
            if (n <= 1) return n;
            return fib(n - 1) + fib(n - 2);
        }
        
        console.log("Starting calculation...");
        var result = fib(12);
        console.log("Fibonacci(12) results:");
        result; // The final expression val is output to results
    """.trimIndent())
    var consoleMode by mutableStateOf("JS") // "JS" or "MATH"
    var consoleStdout by mutableStateOf("Ready to execute.\nPress RUN to initiate sandbox.")
    var consoleStderr by mutableStateOf("")
    var consoleTimeoutMs by mutableStateOf(5000L)
    var isConsoleExecuting by mutableStateOf(false)
    var consoleExitCode by mutableStateOf<Int?>(null)
    var consoleWallTimeMs by mutableStateOf(0L)
    
    private val _scriptSnippets = MutableStateFlow<List<ScriptSnippet>>(emptyList())
    val scriptSnippets: StateFlow<List<ScriptSnippet>> = _scriptSnippets.asStateFlow()

    init {
        // Observe Room changes in coroutine scopes
        viewModelScope.launch {
            fileDao.getAllFiles().collectLatest {
                _codeFiles.value = it
            }
        }
        viewModelScope.launch {
            regexDao.getAllSessions().collectLatest {
                _regexSessions.value = it
            }
        }
        viewModelScope.launch {
            snippetDao.getAllSnippets().collectLatest {
                _scriptSnippets.value = it
            }
        }
        
        // Initial dry-run Regex update
        evaluateRegex()
    }

    // === EDITOR ACTIONS ===
    fun selectFile(file: CodeFile?) {
        activeFile = file
        if (file != null) {
            editorText = file.content
            editorLang = file.language
            editorFileName = file.name
        } else {
            editorText = ""
            editorLang = "plainText"
            editorFileName = ""
        }
    }

    fun createNewFile() {
        activeFile = null
        editorText = ""
        editorLang = "plainText"
        editorFileName = "untitled.txt"
    }

    fun saveFile() {
        val name = editorFileName.trim().ifEmpty { "unnamed.txt" }
        viewModelScope.launch(Dispatchers.IO) {
            val fileId = activeFile?.id ?: UUID.randomUUID().toString()
            val newFile = CodeFile(
                id = fileId,
                name = name,
                language = editorLang,
                content = editorText,
                lastModified = System.currentTimeMillis(),
                isPinned = activeFile?.isPinned ?: false
            )
            fileDao.saveFile(newFile)
            withContext(Dispatchers.Main) {
                activeFile = newFile
            }
        }
    }

    fun togglePinFile(file: CodeFile) {
        viewModelScope.launch(Dispatchers.IO) {
            val updated = file.copy(isPinned = !file.isPinned)
            fileDao.saveFile(updated)
            if (activeFile?.id == file.id) {
                withContext(Dispatchers.Main) {
                    activeFile = updated
                }
            }
        }
    }

    fun deleteFile(file: CodeFile) {
        viewModelScope.launch(Dispatchers.IO) {
            fileDao.deleteFile(file)
            withContext(Dispatchers.Main) {
                if (activeFile?.id == file.id) {
                    selectFile(null)
                }
            }
        }
    }

    // === FORMATTING ACTIONS ===
    fun executeFormat(prettify: Boolean) {
        if (isFormatting) return
        isFormatting = true
        formatterError = null
        
        viewModelScope.launch(Dispatchers.Default) {
            val result = when (formatterLang) {
                "JSON" -> Formatters.formatJson(formatterInput, prettify)
                "XML", "HTML" -> Formatters.formatXml(formatterInput, prettify)
                "CSS" -> Formatters.formatCss(formatterInput, prettify)
                "SQL" -> if (prettify) Formatters.formatCss(formatterInput, true) else Formatters.minifySql(formatterInput)
                else -> FormatResult.Error("Unsupported language $formatterLang")
            }
            withContext(Dispatchers.Main) {
                when (result) {
                    is FormatResult.Success -> {
                        formatterOutput = result.formatted
                        formatterError = null
                    }
                    is FormatResult.Error -> {
                        formatterError = result.message
                    }
                }
                isFormatting = false
            }
        }
    }

    fun swapFormatterInOut() {
        val oldOut = formatterOutput
        if (oldOut.isNotEmpty()) {
            formatterInput = oldOut
            formatterOutput = ""
        }
    }

    fun clearFormatter() {
        formatterInput = ""
        formatterOutput = ""
        formatterError = null
    }

    // === REGEX ACTIONS ===
    fun evaluateRegex() {
        viewModelScope.launch(Dispatchers.Default) {
            val options = mutableListOf<RegexOption>()
            if (regexFlagsIgnoreCase) options.add(RegexOption.IGNORE_CASE)
            if (regexFlagsMultiline) options.add(RegexOption.MULTILINE)
            if (regexFlagsDotAll) options.add(RegexOption.DOT_MATCHES_ALL)

            val startTime = System.nanoTime()
            try {
                if (regexPattern.isEmpty()) {
                    withContext(Dispatchers.Main) {
                        _regexMatches.value = emptyList()
                        regexError = "Pattern is empty"
                    }
                    return@launch
                }
                
                val regex = Regex(regexPattern, options.toSet())
                val matches = regex.findAll(regexInput).toList()
                val mappedMatches = matches.mapIndexed { idx, match ->
                    val groups = match.groups.mapIndexedNotNull { gidx, group ->
                        if (group != null) "Group $gidx: \"${group.value}\" [${group.range.first}..${group.range.last}]" else null
                    }
                    MatchResultWrapper(
                        index = idx,
                        start = match.range.first,
                        end = match.range.last + 1,
                        text = match.value,
                        groups = groups
                    )
                }
                
                val elapsed = (System.nanoTime() - startTime) / 1_000_000L
                withContext(Dispatchers.Main) {
                    _regexMatches.value = mappedMatches
                    regexMatchTimeMs = elapsed
                    regexError = null
                }
            } catch (e: Exception) {
                val elapsed = (System.nanoTime() - startTime) / 1_000_000L
                withContext(Dispatchers.Main) {
                    _regexMatches.value = emptyList()
                    regexMatchTimeMs = elapsed
                    regexError = e.message ?: "Invalid regex schema"
                }
            }
        }
    }

    fun saveRegexSession(label: String) {
        val textLabel = label.trim().ifEmpty { "Regex session ${System.currentTimeMillis()}" }
        val activeFlags = mutableListOf<String>()
        if (regexFlagsGlobal) activeFlags.add("g")
        if (regexFlagsIgnoreCase) activeFlags.add("i")
        if (regexFlagsMultiline) activeFlags.add("m")
        if (regexFlagsDotAll) activeFlags.add("s")
        
        viewModelScope.launch(Dispatchers.IO) {
            val session = RegexSession(
                id = UUID.randomUUID().toString(),
                pattern = regexPattern,
                flags = activeFlags.joinToString(","),
                testInput = regexInput,
                createdAt = System.currentTimeMillis(),
                label = textLabel
            )
            regexDao.saveSession(session)
        }
    }

    fun deleteRegexSession(session: RegexSession) {
        viewModelScope.launch(Dispatchers.IO) {
            regexDao.deleteSessionById(session.id)
        }
    }

    fun loadRegexSession(session: RegexSession) {
        regexPattern = session.pattern
        regexInput = session.testInput
        val flagsList = session.flags.split(",")
        regexFlagsGlobal = flagsList.contains("g")
        regexFlagsIgnoreCase = flagsList.contains("i")
        regexFlagsMultiline = flagsList.contains("m")
        regexFlagsDotAll = flagsList.contains("s")
        evaluateRegex()
    }

    // === DOCUMENTATION ACTIONS ===
    fun toggleBookmarkPage(pageId: String) {
        val updated = bookmarkedPages.toMutableSet()
        if (updated.contains(pageId)) {
            updated.remove(pageId)
        } else {
            updated.add(pageId)
        }
        bookmarkedPages = updated
    }

    fun getFilteredPages(): List<DocPage> {
        val allPages = DocumentationVault.pages
        val query = docSearchQuery.trim()
        val bundle = selectedBundle
        
        var list = if (bundle != null) {
            allPages.filter { it.bundleId == bundle.id }
        } else {
            allPages
        }
        
        if (query.isNotEmpty()) {
            list = list.filter { p ->
                p.title.contains(query, ignoreCase = true) ||
                p.markdownContent.contains(query, ignoreCase = true) ||
                p.tags.any { it.contains(query, ignoreCase = true) }
            }
        }
        return list
    }

    // === CONSOLE SCENARIO ACTIONS ===
    fun runScript() {
        if (isConsoleExecuting) return
        isConsoleExecuting = true
        consoleStdout = "Starting execution engine...\n"
        consoleStderr = ""
        consoleExitCode = null
        
        viewModelScope.launch {
            if (consoleMode == "JS") {
                val outcome = scriptExecutor.executeJs(consoleScript, consoleTimeoutMs)
                consoleStdout = outcome.stdout.ifEmpty { "Script complete (Empty stdout)." }
                consoleStderr = outcome.stderr
                consoleExitCode = outcome.exitCode
                consoleWallTimeMs = outcome.wallTimeMs
            } else {
                val outcome = scriptExecutor.evaluateBasicMath(consoleScript)
                consoleStdout = outcome.stdout
                consoleStderr = outcome.stderr
                consoleExitCode = outcome.exitCode
                consoleWallTimeMs = outcome.wallTimeMs
            }
            isConsoleExecuting = false
        }
    }

    fun saveScriptSnippet(title: String) {
        val trimTitle = title.trim().ifEmpty { "Script ${System.currentTimeMillis()}" }
        viewModelScope.launch(Dispatchers.IO) {
            val snip = ScriptSnippet(
                id = UUID.randomUUID().toString(),
                title = trimTitle,
                code = consoleScript,
                language = consoleMode,
                createdAt = System.currentTimeMillis()
            )
            snippetDao.saveSnippet(snip)
        }
    }

    fun deleteScriptSnippet(snippet: ScriptSnippet) {
        viewModelScope.launch(Dispatchers.IO) {
            snippetDao.deleteSnippetById(snippet.id)
        }
    }

    fun loadScriptSnippet(snippet: ScriptSnippet) {
        consoleScript = snippet.code
        consoleMode = snippet.language
    }
}

// Custom wrapper to keep compiler and test tags accurate
data class MatchResultWrapper(
    val index: Int,
    val start: Int,
    val end: Int,
    val text: String,
    val groups: List<String>
)
