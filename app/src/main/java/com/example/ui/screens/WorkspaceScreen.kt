package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.database.CodeFile
import com.example.ui.theme.NeonTeal
import com.example.ui.theme.TerminalOutline
import com.example.ui.theme.TerminalSurface
import com.example.ui.viewmodel.DevPocketViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorkspaceScreen(
    viewModel: DevPocketViewModel,
    modifier: Modifier = Modifier
) {
    val files by viewModel.codeFiles.collectAsState()
    val activeFile = viewModel.activeFile
    
    var showPreferencesDialog by remember { mutableStateOf(false) }
    var showNewFilePrompt by remember { mutableStateOf(false) }
    var showDeleteConfirmFile by remember { mutableStateOf<CodeFile?>(null) }
    
    val languages = listOf("plainText", "json", "xml", "html", "markdown", "css", "sql", "javascript")
    val focusManager = LocalFocusManager.current
    
    Row(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Left Column: Active files drawer/sidebar
        Column(
            modifier = Modifier
                .width(180.dp)
                .fillMaxHeight()
                .border(width = 1.dp, color = TerminalOutline)
                .background(TerminalSurface)
                .padding(8.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "FILES",
                    style = MaterialTheme.typography.labelLarge,
                    color = NeonTeal,
                    fontWeight = FontWeight.Bold
                )
                IconButton(
                    onClick = { showNewFilePrompt = true },
                    modifier = Modifier
                        .size(28.dp)
                        .testTag("create_file_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "New File",
                        tint = NeonTeal,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
            
            Divider(color = TerminalOutline, modifier = Modifier.padding(bottom = 8.dp))
            
            if (files.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No files yet.\nPress + to build.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                        fontSize = 11.sp
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(1f)
                ) {
                    items(files) { file ->
                        val isSelected = activeFile?.id == file.id
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 2.dp)
                                .clip(RoundedCornerShape(4.dp))
                                .background(if (isSelected) MaterialTheme.colorScheme.surfaceVariant else Color.Transparent)
                                .clickable {
                                    focusManager.clearFocus()
                                    viewModel.selectFile(file)
                                }
                                .padding(paddingValues = PaddingValues(horizontal = 6.dp, vertical = 6.dp)),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = if (file.isPinned) Icons.Default.PushPin else Icons.Default.Description,
                                contentDescription = if (file.isPinned) "Pinned" else "File",
                                tint = if (file.isPinned) NeonTeal else MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier
                                    .size(14.dp)
                                    .padding(end = 4.dp)
                            )
                            Text(
                                text = file.name,
                                style = MaterialTheme.typography.bodyMedium,
                                color = if (isSelected) NeonTeal else MaterialTheme.colorScheme.onBackground,
                                maxLines = 1,
                                fontSize = 12.sp,
                                modifier = Modifier.weight(1f)
                            )
                            IconButton(
                                onClick = { viewModel.togglePinFile(file) },
                                modifier = Modifier.size(16.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.PushPin,
                                    contentDescription = "Pin File",
                                    tint = if (file.isPinned) NeonTeal else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f),
                                    modifier = Modifier.size(10.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(4.dp))
                            IconButton(
                                onClick = { showDeleteConfirmFile = file },
                                modifier = Modifier.size(16.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = "Delete File",
                                    tint = MaterialTheme.colorScheme.error.copy(alpha = 0.7f),
                                    modifier = Modifier.size(10.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
        
        // Right Column: Editor and controls
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .padding(horizontal = 12.dp, vertical = 8.dp)
        ) {
            // Filename input row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = viewModel.editorFileName,
                    onValueChange = { viewModel.editorFileName = it },
                    label = { Text("Filename", style = MaterialTheme.typography.labelSmall) },
                    textStyle = TextStyle(fontFamily = FontFamily.Monospace, fontSize = 13.sp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = NeonTeal,
                        unfocusedBorderColor = TerminalOutline,
                        focusedLabelColor = NeonTeal
                    ),
                    modifier = Modifier
                        .weight(1f)
                        .testTag("editor_filename_input"),
                    singleLine = true
                )
                
                // Language Dropdown
                var expandedLanguageMenu by remember { mutableStateOf(false) }
                Box {
                    Button(
                        onClick = { expandedLanguageMenu = true },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant,
                            contentColor = MaterialTheme.colorScheme.onBackground
                        ),
                        shape = RoundedCornerShape(4.dp),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                        modifier = Modifier.height(52.dp)
                    ) {
                        Text(
                            text = viewModel.editorLang,
                            style = MaterialTheme.typography.labelLarge,
                            color = NeonTeal
                        )
                        Icon(
                            imageVector = Icons.Default.ArrowDropDown,
                            contentDescription = "Languages",
                            tint = NeonTeal,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                    DropdownMenu(
                        expanded = expandedLanguageMenu,
                        onDismissRequest = { expandedLanguageMenu = false },
                        modifier = Modifier.background(TerminalSurface)
                    ) {
                        languages.forEach { lang ->
                            DropdownMenuItem(
                                text = { Text(lang, color = MaterialTheme.colorScheme.onBackground) },
                                onClick = {
                                    viewModel.editorLang = lang
                                    expandedLanguageMenu = false
                                }
                            )
                        }
                    }
                }
                
                // Settings button
                IconButton(
                    onClick = { showPreferencesDialog = true },
                    modifier = Modifier
                        .size(44.dp)
                        .border(1.dp, TerminalOutline, RoundedCornerShape(4.dp))
                ) {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = "Settings",
                        tint = MaterialTheme.colorScheme.onBackground
                    )
                }
                
                // Save button
                IconButton(
                    onClick = {
                        focusManager.clearFocus()
                        viewModel.saveFile()
                    },
                    modifier = Modifier
                        .size(44.dp)
                        .background(NeonTeal, RoundedCornerShape(4.dp))
                        .testTag("save_file_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Save,
                        contentDescription = "Save File",
                        tint = Color.Black
                    )
                }
            }
            
            // Code Editor Space
            Row(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .border(width = 1.dp, color = TerminalOutline, shape = RoundedCornerShape(4.dp))
                    .clip(RoundedCornerShape(4.dp))
                    .background(TerminalSurface)
            ) {
                // Gutters (Line numbers)
                val scrollState = rememberScrollState()
                
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .background(Color.Black.copy(alpha = 0.2f))
                        .padding(horizontal = 6.dp, vertical = 8.dp)
                        .verticalScroll(scrollState)
                ) {
                    val lineCount = viewModel.editorText.split("\n").size
                    val gutterText = (1..lineCount).joinToString("\n") { it.toString().padStart(3) }
                    Text(
                        text = gutterText,
                        fontFamily = FontFamily.Monospace,
                        fontSize = viewModel.fontSize.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f),
                        lineHeight = (viewModel.fontSize * 1.5).sp
                    )
                }
                
                // Vertical divider line between line numbers and editor content
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .width(1.dp)
                        .background(TerminalOutline)
                )
                
                // Interactive Text Edit Area
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .padding(horizontal = 8.dp, vertical = 8.dp)
                ) {
                    TextField(
                        value = viewModel.editorText,
                        onValueChange = { input ->
                            // Bracket pairing auto-closing helper
                            val updated = if (viewModel.bracketAutoClose && input.length == viewModel.editorText.length + 1) {
                                val addedChar = input[input.length - 1]
                                val closingMap = mapOf('{' to '}', '[' to ']', '(' to ')', '"' to '"', '\'' to '\'')
                                if (closingMap.containsKey(addedChar)) {
                                    input + closingMap[addedChar]
                                } else {
                                    input
                                }
                            } else {
                                input
                            }
                            viewModel.editorText = updated
                        },
                        textStyle = TextStyle(
                            fontFamily = FontFamily.Monospace,
                            fontSize = viewModel.fontSize.sp,
                            color = MaterialTheme.colorScheme.onBackground,
                            lineHeight = (viewModel.fontSize * 1.5).sp
                        ),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,
                            disabledContainerColor = Color.Transparent,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent
                        ),
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(scrollState)
                            .testTag("editor_text_input")
                    )
                    
                    if (viewModel.editorText.isEmpty()) {
                        Text(
                            text = "// Write code here...\n// Tap Save 💾 to store in offline Room DB.",
                            fontFamily = FontFamily.Monospace,
                            fontSize = viewModel.fontSize.sp,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f),
                            modifier = Modifier.padding(start = 12.dp, top = 12.dp)
                        )
                    }
                }
            }
        }
    }
    
    // === DIALOGS ===
    if (showPreferencesDialog) {
        Dialog(onDismissRequest = { showPreferencesDialog = false }) {
            Surface(
                modifier = Modifier
                    .width(320.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .border(1.dp, TerminalOutline, RoundedCornerShape(8.dp)),
                color = TerminalSurface
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "EDITOR SETTINGS",
                        style = MaterialTheme.typography.headlineMedium,
                        color = NeonTeal,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )
                    
                    Divider(color = TerminalOutline, modifier = Modifier.padding(bottom = 12.dp))
                    
                    // Font scale
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Font Size", color = MaterialTheme.colorScheme.onBackground)
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            IconButton(onClick = { viewModel.fontSize = (viewModel.fontSize - 1).coerceAtLeast(10) }) {
                                Icon(Icons.Default.Remove, "Smaller", tint = NeonTeal)
                            }
                            Text(viewModel.fontSize.toString(), color = NeonTeal, fontWeight = FontWeight.Bold)
                            IconButton(onClick = { viewModel.fontSize = (viewModel.fontSize + 1).coerceAtMost(28) }) {
                                Icon(Icons.Default.Add, "Bigger", tint = NeonTeal)
                            }
                        }
                    }
                    
                    // Bracket closing
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Auto-pair Brackets", color = MaterialTheme.colorScheme.onBackground)
                        Switch(
                            checked = viewModel.bracketAutoClose,
                            onCheckedChange = { viewModel.bracketAutoClose = it },
                            colors = SwitchDefaults.colors(checkedThumbColor = NeonTeal)
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Button(
                        onClick = { showPreferencesDialog = false },
                        colors = ButtonDefaults.buttonColors(containerColor = NeonTeal),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Text("APPLY", color = Color.Black, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
    
    // New file prompt dialog
    if (showNewFilePrompt) {
        var tempName by remember { mutableStateOf("new_script.js") }
        Dialog(onDismissRequest = { showNewFilePrompt = false }) {
            Surface(
                modifier = Modifier
                    .width(320.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .border(1.dp, TerminalOutline, RoundedCornerShape(8.dp)),
                color = TerminalSurface
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("NEW LOGICAL FILE", style = MaterialTheme.typography.headlineMedium, color = NeonTeal)
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(
                        value = tempName,
                        onValueChange = { tempName = it },
                        label = { Text("File Name") },
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = NeonTeal, unfocusedBorderColor = TerminalOutline),
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(horizontalArrangement = Arrangement.End, modifier = Modifier.fillMaxWidth()) {
                        TextButton(onClick = { showNewFilePrompt = false }) {
                            Text("CANCEL", color = MaterialTheme.colorScheme.onSurface)
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(
                            onClick = {
                                val ext = tempName.substringAfterLast(".", "")
                                val resolvedLang = when (ext) {
                                    "js" -> "javascript"
                                    "json" -> "json"
                                    "xml" -> "xml"
                                    "html" -> "html"
                                    "css" -> "css"
                                    "sql" -> "sql"
                                    "md" -> "markdown"
                                    else -> "plainText"
                                }
                                viewModel.createNewFile()
                                viewModel.editorFileName = tempName
                                viewModel.editorLang = resolvedLang
                                viewModel.saveFile()
                                showNewFilePrompt = false
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = NeonTeal),
                            shape = RoundedCornerShape(4.dp)
                        ) {
                            Text("CREATE", color = Color.Black, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
    
    // File delete confirmation
    showDeleteConfirmFile?.let { file ->
        Dialog(onDismissRequest = { showDeleteConfirmFile = null }) {
            Surface(
                modifier = Modifier
                    .width(320.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .border(1.dp, TerminalOutline, RoundedCornerShape(8.dp)),
                color = TerminalSurface
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("DELETE FILE?", style = MaterialTheme.typography.headlineMedium, color = MaterialTheme.colorScheme.error)
                    Spacer(modifier = Modifier.height(12.dp))
                    Text("Are you sure you want to permanently erase \"${file.name}\"? This action cannot be undone.", color = MaterialTheme.colorScheme.onBackground)
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(horizontalArrangement = Arrangement.End, modifier = Modifier.fillMaxWidth()) {
                        TextButton(onClick = { showDeleteConfirmFile = null }) {
                            Text("CANCEL", color = MaterialTheme.colorScheme.onSurface)
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(
                            onClick = {
                                viewModel.deleteFile(file)
                                showDeleteConfirmFile = null
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                            shape = RoundedCornerShape(4.dp)
                        ) {
                            Text("DELETE", color = Color.White, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}
