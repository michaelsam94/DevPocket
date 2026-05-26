package com.michael.devpocket.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import com.michael.devpocket.ui.theme.*
import com.michael.devpocket.ui.viewmodel.DevPocketViewModel

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ConsoleScreen(
    viewModel: DevPocketViewModel,
    modifier: Modifier = Modifier
) {
    val snippets by viewModel.scriptSnippets.collectAsState()
    val focusManager = LocalFocusManager.current
    val consoleScrollState = rememberScrollState()

    var showSaveSnippetDialog by remember { mutableStateOf(false) }
    var showSnippetsDialog by remember { mutableStateOf(false) }
    var showWorkspaceFilesDialog by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(12.dp)
            .verticalScroll(rememberScrollState())
    ) {
        // App header bar
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "OFFLINE SCRIPT SANDBOX ENGINE",
                style = MaterialTheme.typography.labelLarge,
                color = NeonTeal,
                fontWeight = FontWeight.Bold
            )
            Row {
                IconButton(onClick = { showWorkspaceFilesDialog = true }) {
                    Icon(Icons.Default.FolderOpen, "Workspace Files", tint = NeonTeal)
                }
                IconButton(onClick = { showSnippetsDialog = true }) {
                    Icon(Icons.Default.History, "Snippets", tint = NeonTeal)
                }
                IconButton(onClick = { showSaveSnippetDialog = true }, modifier = Modifier.testTag("save_script_snippet_button")) {
                    Icon(Icons.Default.Save, "Save Snippet", tint = NeonTeal)
                }
            }
        }

        // Subtitle row to pick Sandbox Mode & Timeout duration
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Environment Option Dropdown
            var expandedEnv by remember { mutableStateOf(false) }
            Box(modifier = Modifier.weight(1f)) {
                OutlinedCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { expandedEnv = true },
                    colors = CardDefaults.outlinedCardColors(containerColor = TerminalSurface),
                    border = BorderStroke(1.dp, TerminalOutline)
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (viewModel.consoleMode == "JS") "JavaScript Engine (V8)" else "Kotlin Math Core (AST)",
                            fontSize = 12.sp,
                            fontFamily = FontFamily.Monospace,
                            color = NeonTeal,
                            fontWeight = FontWeight.Bold
                        )
                        Icon(Icons.Default.ArrowDropDown, "Env Dropdown", tint = NeonTeal, modifier = Modifier.size(16.dp))
                    }
                }
                DropdownMenu(
                    expanded = expandedEnv,
                    onDismissRequest = { expandedEnv = false },
                    modifier = Modifier.background(TerminalSurface)
                ) {
                    DropdownMenuItem(
                        text = { Text("JavaScript Sandbox (Headless V8)", color = Color.White) },
                        onClick = {
                            viewModel.consoleMode = "JS"
                            viewModel.consoleScript = """
                                // Standard JavaScript Sandbox
                                var multiplier = 3.5;
                                console.log("Executing standard script...");
                                var value = 12 + 15;
                                console.log("Calculated internal: " + value);
                                "Result: " + (value * multiplier);
                            """.trimIndent()
                            expandedEnv = false
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("Kotlin Safe Math Expression Renderer", color = Color.White) },
                        onClick = {
                            viewModel.consoleMode = "MATH"
                            viewModel.consoleScript = "sqrt(144) + sin(90) * (5^2) - cos(0)"
                            expandedEnv = false
                        }
                    )
                }
            }

            // Timeout threshold
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    "COMPILE LIMIT: ${(viewModel.consoleTimeoutMs / 1000)}s",
                    fontSize = 10.sp,
                    fontFamily = FontFamily.Monospace,
                    color = OnSurfaceMuted
                )
                Slider(
                    value = (viewModel.consoleTimeoutMs / 1000).toFloat(),
                    onValueChange = { viewModel.consoleTimeoutMs = (it.toLong() * 1000L).coerceIn(1000L, 30000L) },
                    valueRange = 1f..30f,
                    steps = 29,
                    colors = SliderDefaults.colors(
                        thumbColor = NeonTeal,
                        activeTrackColor = NeonTeal,
                        inactiveTrackColor = TerminalOutline
                    )
                )
            }
        }

        // Script input editing card
        Text(
            "INPUT SOURCE CODE CODE",
            style = MaterialTheme.typography.labelSmall,
            color = OnSurfaceMuted,
            modifier = Modifier.padding(top = 8.dp, bottom = 4.dp)
        )
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .height(140.dp)
                .border(1.dp, TerminalOutline, RoundedCornerShape(4.dp))
                .clip(RoundedCornerShape(4.dp))
                .background(TerminalSurface)
                .padding(8.dp)
        ) {
            TextField(
                value = viewModel.consoleScript,
                onValueChange = { viewModel.consoleScript = it },
                textStyle = TextStyle(fontFamily = FontFamily.Monospace, fontSize = 13.sp),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                ),
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .testTag("console_script_input")
            )
        }

        // Execution control actions
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 10.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Button(
                onClick = {
                    focusManager.clearFocus()
                    viewModel.runScript()
                },
                colors = ButtonDefaults.buttonColors(containerColor = NeonTeal),
                shape = RoundedCornerShape(4.dp),
                enabled = !viewModel.isConsoleExecuting,
                modifier = Modifier
                    .weight(1f)
                    .testTag("run_script_button")
            ) {
                if (viewModel.isConsoleExecuting) {
                    CircularProgressIndicator(modifier = Modifier.size(16.dp), color = Color.Black, strokeWidth = 2.dp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("EXECUTING...", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                } else {
                    Icon(Icons.Default.PlayArrow, "Run", tint = Color.Black)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("RUN ▶", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }
            }

            OutlinedButton(
                onClick = {
                    viewModel.isConsoleExecuting = false
                    viewModel.consoleStdout = "Sandbox session terminated by user."
                    viewModel.consoleStderr = ""
                    viewModel.consoleExitCode = -3
                },
                colors = ButtonDefaults.outlinedButtonColors(contentColor = WarningOrange),
                border = BorderStroke(1.dp, WarningOrange.copy(alpha = 0.5f)),
                shape = RoundedCornerShape(4.dp),
                modifier = Modifier.weight(1f).testTag("kill_script_button")
            ) {
                Icon(Icons.Default.Stop, "Kill", tint = WarningOrange)
                Spacer(modifier = Modifier.width(6.dp))
                Text("KILL ■", fontWeight = FontWeight.Bold, fontSize = 12.sp)
            }

            OutlinedButton(
                onClick = {
                    viewModel.consoleStdout = ""
                    viewModel.consoleStderr = ""
                    viewModel.consoleExitCode = null
                    viewModel.consoleWallTimeMs = 0
                },
                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
                border = BorderStroke(1.dp, TerminalOutline),
                shape = RoundedCornerShape(4.dp)
            ) {
                Icon(Icons.Default.DeleteSweep, "Clear Console")
                Spacer(modifier = Modifier.width(4.dp))
                Text("CLEAR", fontSize = 11.sp)
            }
        }

        // Terminal Board Output Card
        Row(
            modifier = Modifier.fillMaxWidth().padding(top = 10.dp, bottom = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("DEVPOCKET TERMINAL BOARD", style = MaterialTheme.typography.labelSmall, color = SoftPurple)
            
            // Console status chips
            viewModel.consoleExitCode?.let { code ->
                val codeBg = if (code == 0) TerminalSuccess else TerminalError
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(2.dp))
                        .background(codeBg)
                        .padding(horizontal = 4.dp, vertical = 2.dp)
                ) {
                    Text(
                        "EXIT: $code",
                        fontSize = 9.sp,
                        fontFamily = FontFamily.Monospace,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        // Output logger layout box
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
                .border(2.dp, SoftPurple.copy(alpha = 0.4f), RoundedCornerShape(4.dp))
                .clip(RoundedCornerShape(4.dp))
                .background(Color.Black)
                .padding(8.dp)
                .verticalScroll(consoleScrollState)
        ) {
            // Stdout stream
            if (viewModel.consoleStdout.isNotEmpty()) {
                Text(
                    text = viewModel.consoleStdout,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 11.sp,
                    color = Color.White,
                    lineHeight = 15.sp,
                    modifier = Modifier.padding(bottom = 6.dp)
                )
            }

            // Stderr stream
            if (viewModel.consoleStderr.isNotEmpty()) {
                Text(
                    text = viewModel.consoleStderr,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 11.sp,
                    color = WarningOrange,
                    fontWeight = FontWeight.Bold,
                    lineHeight = 15.sp,
                    modifier = Modifier.padding(bottom = 6.dp)
                )
            }
            
            // Wall execution time logs
            if (viewModel.consoleWallTimeMs > 0) {
                Text(
                    text = "[System Log] Process finished inside ${viewModel.consoleWallTimeMs}ms.",
                    fontFamily = FontFamily.Monospace,
                    fontSize = 10.sp,
                    color = NeonTeal,
                    lineHeight = 14.sp
                )
            }
        }
    }

    // === SAVE SNIPPET DIALOG ===
    if (showSaveSnippetDialog) {
        var snipTitle by remember { mutableStateOf("calc_primes.js") }
        Dialog(onDismissRequest = { showSaveSnippetDialog = false }) {
            Surface(
                modifier = Modifier
                    .width(320.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .border(1.dp, TerminalOutline, RoundedCornerShape(8.dp)),
                color = TerminalSurface
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("SAVE SNIPPET SNIP", style = MaterialTheme.typography.headlineMedium, color = NeonTeal)
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(
                        value = snipTitle,
                        onValueChange = { snipTitle = it },
                        label = { Text("Snippet Title") },
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = NeonTeal, unfocusedBorderColor = TerminalOutline),
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(horizontalArrangement = Arrangement.End, modifier = Modifier.fillMaxWidth()) {
                        TextButton(onClick = { showSaveSnippetDialog = false }) {
                            Text("CANCEL", color = OnSurfaceMuted)
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(
                            onClick = {
                                viewModel.saveScriptSnippet(snipTitle)
                                showSaveSnippetDialog = false
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = NeonTeal),
                            shape = RoundedCornerShape(4.dp)
                        ) {
                            Text("SAVE", color = Color.Black, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }

    // === SNIPPET LIBRARIES HISTORIC LOADING DIALOG ===
    if (showSnippetsDialog) {
        Dialog(onDismissRequest = { showSnippetsDialog = false }) {
            Surface(
                modifier = Modifier
                    .width(340.dp)
                    .height(400.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .border(1.dp, TerminalOutline, RoundedCornerShape(8.dp)),
                color = TerminalSurface
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("SNIPPETS REPOSITORY", style = MaterialTheme.typography.headlineMedium, color = NeonTeal)
                    Spacer(modifier = Modifier.height(12.dp))
                    Divider(color = TerminalOutline)
                    Spacer(modifier = Modifier.height(8.dp))

                    if (snippets.isEmpty()) {
                        Box(modifier = Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                            Text("No snippets saved.", color = OnSurfaceMuted)
                        }
                    } else {
                        LazyColumn(modifier = Modifier.weight(1f).fillMaxWidth()) {
                            items(snippets) { snip ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp)
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(Color.Black.copy(alpha = 0.2f))
                                        .clickable {
                                            viewModel.loadScriptSnippet(snip)
                                            showSnippetsDialog = false
                                        }
                                        .padding(8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(snip.title, color = NeonTeal, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                        Text(
                                            text = "Core: ${snip.language} · Length: ${snip.code.length}",
                                            fontFamily = FontFamily.Monospace,
                                            fontSize = 11.sp,
                                            color = Color.White
                                        )
                                    }
                                    IconButton(onClick = { viewModel.deleteScriptSnippet(snip) }, modifier = Modifier.size(24.dp)) {
                                        Icon(Icons.Default.Delete, "Delete", tint = MaterialTheme.colorScheme.error)
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    Button(
                        onClick = { showSnippetsDialog = false },
                        colors = ButtonDefaults.buttonColors(containerColor = NeonTeal),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Text("CLOSE", color = Color.Black, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }

    // === WORKSPACE FILES LOADING DIALOG ===
    if (showWorkspaceFilesDialog) {
        val workspaceFiles by viewModel.codeFiles.collectAsState()
        Dialog(onDismissRequest = { showWorkspaceFilesDialog = false }) {
            Surface(
                modifier = Modifier
                    .width(340.dp)
                    .height(400.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .border(1.dp, TerminalOutline, RoundedCornerShape(8.dp)),
                color = TerminalSurface
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("WORKSPACE FILES", style = MaterialTheme.typography.headlineMedium, color = NeonTeal)
                    Spacer(modifier = Modifier.height(12.dp))
                    Divider(color = TerminalOutline)
                    Spacer(modifier = Modifier.height(8.dp))

                    if (workspaceFiles.isEmpty()) {
                        Box(modifier = Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                            Text("No workspace files saved.", color = OnSurfaceMuted)
                        }
                    } else {
                        LazyColumn(modifier = Modifier.weight(1f).fillMaxWidth()) {
                            items(workspaceFiles) { file ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp)
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(Color.Black.copy(alpha = 0.2f))
                                        .clickable {
                                            viewModel.consoleScript = file.content
                                            if (file.language.lowercase() == "javascript") {
                                                viewModel.consoleMode = "JS"
                                            }
                                            showWorkspaceFilesDialog = false
                                        }
                                        .padding(8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Description,
                                        contentDescription = "File",
                                        tint = NeonTeal,
                                        modifier = Modifier.padding(end = 8.dp).size(20.dp)
                                    )
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(file.name, color = NeonTeal, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                        Text(
                                            text = "Lang: ${file.language} · Size: ${file.content.length} chars",
                                            fontFamily = FontFamily.Monospace,
                                            fontSize = 11.sp,
                                            color = Color.White
                                        )
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    Button(
                        onClick = { showWorkspaceFilesDialog = false },
                        colors = ButtonDefaults.buttonColors(containerColor = NeonTeal),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Text("CLOSE", color = Color.Black, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
