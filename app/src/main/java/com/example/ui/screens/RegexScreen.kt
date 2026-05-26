package com.example.ui.screens

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
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.ui.theme.*
import com.example.ui.viewmodel.DevPocketViewModel
import com.example.ui.viewmodel.MatchResultWrapper

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun RegexScreen(
    viewModel: DevPocketViewModel,
    modifier: Modifier = Modifier
) {
    val matches by viewModel.regexMatches.collectAsState()
    val savedSessions by viewModel.regexSessions.collectAsState()
    val focusManager = LocalFocusManager.current
    
    var showSaveSessionDialog by remember { mutableStateOf(false) }
    var showHistoryDialog by remember { mutableStateOf(false) }
    var showCheatsheet by remember { mutableStateOf(false) }

    // Color list to cycle matching backdrops elegantly
    val highlightColors = listOf(
        NeonTeal.copy(alpha = 0.3f),
        SoftPurple.copy(alpha = 0.35f),
        WarningOrange.copy(alpha = 0.3f),
        Color(0xFF22C55E).copy(alpha = 0.3f),
        Color(0xFF3B82F6).copy(alpha = 0.3f),
        Color(0xFFEAB308).copy(alpha = 0.3f)
    )

    // Build the annotated string displaying regex matches dynamically inside text area
    val annotatedTestInput = buildAnnotatedString {
        append(viewModel.regexInput)
        if (viewModel.regexError == null && matches.isNotEmpty()) {
            matches.forEachIndexed { index, match ->
                val safeStart = match.start.coerceIn(0, viewModel.regexInput.length)
                val safeEnd = match.end.coerceIn(0, viewModel.regexInput.length)
                if (safeStart < safeEnd) {
                    val color = highlightColors[index % highlightColors.size]
                    addStyle(
                        style = SpanStyle(
                            background = color,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        ),
                        start = safeStart,
                        end = safeEnd
                    )
                }
            }
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(12.dp)
            .verticalScroll(rememberScrollState())
    ) {
        // Pattern Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "REGEX PATTERN PLAYGROUND",
                style = MaterialTheme.typography.labelLarge,
                color = NeonTeal,
                fontWeight = FontWeight.Bold
            )
            Row {
                IconButton(onClick = { showHistoryDialog = true }) {
                    Icon(Icons.Default.History, "History", tint = NeonTeal)
                }
                IconButton(onClick = { showSaveSessionDialog = true }, modifier = Modifier.testTag("save_regex_session_button")) {
                    Icon(Icons.Default.BookmarkAdd, "Save Session", tint = NeonTeal)
                }
            }
        }

        // Custom Delimiter pattern input layout
        val patternBorderColor = if (viewModel.regexError != null) MaterialTheme.colorScheme.error else NeonTeal
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 6.dp)
                .border(1.dp, patternBorderColor, RoundedCornerShape(4.dp))
                .clip(RoundedCornerShape(4.dp))
                .background(TerminalSurface)
                .padding(horizontal = 10.dp, vertical = 2.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "/",
                fontFamily = FontFamily.Monospace,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = OnSurfaceMuted
            )
            TextField(
                value = viewModel.regexPattern,
                onValueChange = {
                    viewModel.regexPattern = it
                    viewModel.evaluateRegex()
                },
                textStyle = TextStyle(fontFamily = FontFamily.Monospace, fontSize = 15.sp, color = Color.White),
                placeholder = { Text("your_regex_here", fontFamily = FontFamily.Monospace, fontSize = 14.sp) },
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                ),
                modifier = Modifier
                    .weight(1f)
                    .testTag("pattern_input_field"),
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done)
            )
            Text(
                "/",
                fontFamily = FontFamily.Monospace,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = OnSurfaceMuted
            )
            
            // Flags visualization suffix
            val flagsSuffix = buildString {
                if (viewModel.regexFlagsGlobal) append("g")
                if (viewModel.regexFlagsIgnoreCase) append("i")
                if (viewModel.regexFlagsMultiline) append("m")
                if (viewModel.regexFlagsDotAll) append("s")
            }
            Text(
                flagsSuffix.ifEmpty { "no-flags" },
                fontFamily = FontFamily.Monospace,
                fontSize = 13.sp,
                color = NeonTeal,
                modifier = Modifier.padding(start = 4.dp)
            )
        }

        // Error log
        viewModel.regexError?.let { err ->
            Text(
                text = "❌ INVALID SCHEMA: $err",
                color = MaterialTheme.colorScheme.error,
                fontFamily = FontFamily.Monospace,
                fontSize = 11.sp,
                modifier = Modifier.padding(vertical = 4.dp)
            )
        }

        // Flags buttons container
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            val flags = listOf(
                "g" to "Global Search",
                "i" to "Ignore Case",
                "m" to "Multiline Match",
                "s" to "DotMatchesAll"
            )
            flags.forEach { (char, label) ->
                val active = when (char) {
                    "g" -> viewModel.regexFlagsGlobal
                    "i" -> viewModel.regexFlagsIgnoreCase
                    "m" -> viewModel.regexFlagsMultiline
                    "s" -> viewModel.regexFlagsDotAll
                    else -> false
                }
                FilterChip(
                    selected = active,
                    onClick = {
                        when (char) {
                            "g" -> viewModel.regexFlagsGlobal = !viewModel.regexFlagsGlobal
                            "i" -> viewModel.regexFlagsIgnoreCase = !viewModel.regexFlagsIgnoreCase
                            "m" -> viewModel.regexFlagsMultiline = !viewModel.regexFlagsMultiline
                            "s" -> viewModel.regexFlagsDotAll = !viewModel.regexFlagsDotAll
                        }
                        viewModel.evaluateRegex()
                    },
                    label = { Text(char, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = NeonTeal,
                        selectedLabelColor = Color.Black,
                        containerColor = TerminalSurface
                    ),
                    modifier = Modifier.testTag("flag_chip_$char")
                )
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("TEST INPUT STREAM", style = MaterialTheme.typography.labelSmall, color = NeonTeal)
            Text(
                "Highlighted consecutively below",
                style = MaterialTheme.typography.labelSmall,
                color = OnSurfaceMuted,
                fontSize = 11.sp
            )
        }

        // Multiline input displaying matches beautifully
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .height(130.dp)
                .padding(vertical = 6.dp)
                .border(1.dp, TerminalOutline, RoundedCornerShape(4.dp))
                .clip(RoundedCornerShape(4.dp))
                .background(TerminalSurface)
                .padding(8.dp)
        ) {
            TextField(
                value = viewModel.regexInput,
                onValueChange = {
                    viewModel.regexInput = it
                    viewModel.evaluateRegex()
                },
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
                    .testTag("regex_input_field")
            )
        }

        // Summary Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "MATCHES BOARD (${matches.size})",
                style = MaterialTheme.typography.labelLarge,
                color = NeonTeal,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Computed in ${viewModel.regexMatchTimeMs} ms",
                style = MaterialTheme.typography.labelSmall,
                color = OnSurfaceMuted
            )
        }

        // Display individual match records
        if (matches.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(80.dp)
                    .border(1.dp, TerminalOutline, RoundedCornerShape(4.dp))
                    .background(TerminalSurface),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    "No match strings detected in sample text.",
                    color = OnSurfaceMuted.copy(alpha = 0.6f),
                    fontFamily = FontFamily.Monospace,
                    fontSize = 12.sp
                )
            }
        } else {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                matches.forEach { match ->
                    val colorIndex = match.index % highlightColors.size
                    Card(
                        colors = CardDefaults.cardColors(containerColor = TerminalSurface),
                        border = BorderStroke(1.dp, TerminalOutline),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(8.dp)
                                            .clip(RoundedCornerShape(4.dp))
                                            .background(highlightColors[colorIndex].copy(alpha = 1.0f))
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        "MATCH ${match.index + 1}",
                                        fontFamily = FontFamily.Monospace,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = NeonTeal
                                    )
                                }
                                Text(
                                    "Index: [${match.start}..${match.end}]",
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 10.sp,
                                    color = OnSurfaceMuted
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                "\"${match.text}\"",
                                fontFamily = FontFamily.Monospace,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            if (match.groups.size > 1) {
                                Spacer(modifier = Modifier.height(8.dp))
                                Text("CAPTURE GROUPS:", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = SoftPurple)
                                match.groups.drop(1).forEach { groupInfo ->
                                    Text(
                                        "- $groupInfo",
                                        fontFamily = FontFamily.Monospace,
                                        fontSize = 10.sp,
                                        color = OnSurfaceMuted,
                                        modifier = Modifier.padding(start = 4.dp, top = 2.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Cheat sheet expandable trigger
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, TerminalOutline, RoundedCornerShape(4.dp))
                .clip(RoundedCornerShape(4.dp))
                .background(TerminalSurface)
                .clickable { showCheatsheet = !showCheatsheet }
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Help, "Help", tint = NeonTeal, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    "OFFLINE REGEX CHEAT SHEET",
                    style = MaterialTheme.typography.labelLarge,
                    color = OnBackgroundBright,
                    fontWeight = FontWeight.Bold
                )
            }
            Icon(
                imageVector = if (showCheatsheet) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                contentDescription = "Expand",
                tint = NeonTeal
            )
        }

        AnimatedVisibility(visible = showCheatsheet) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 6.dp)
                    .border(1.dp, TerminalOutline, RoundedCornerShape(4.dp))
                    .clip(RoundedCornerShape(4.dp))
                    .background(TerminalSurface)
                    .padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val rules = listOf(
                    "Anchors" to "^ (start of string), $ (end of string), \\b (word boundary)",
                    "Quantifiers" to "* (0+), + (1+), ? (0 or 1), {n} (exact), {n,m} (range)",
                    "Classes" to "\\d (digits), \\w (alphanumeric + _), \\s (whitespace), . (any)",
                    "Sets" to "[abc] (any in set), [^abc] (none in set), [a-z] (lowercase range)",
                    "Groups" to "(...) (capturing group), (?:...) (non-capturing), (?=...) (lookahead)"
                )
                rules.forEach { (cat, desc) ->
                    Column {
                        Text(cat.uppercase(), fontSize = 11.sp, color = NeonTeal, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
                        Text(desc, fontSize = 12.sp, color = OnBackgroundBright, modifier = Modifier.padding(start = 4.dp))
                    }
                }
            }
        }
    }

    // === SAVE SESSION DIALOG ===
    if (showSaveSessionDialog) {
        var label_txt by remember { mutableStateOf("My Email Filter") }
        Dialog(onDismissRequest = { showSaveSessionDialog = false }) {
            Surface(
                modifier = Modifier
                    .width(320.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .border(1.dp, TerminalOutline, RoundedCornerShape(8.dp)),
                color = TerminalSurface
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("SAVE SESSION", style = MaterialTheme.typography.headlineMedium, color = NeonTeal)
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(
                        value = label_txt,
                        onValueChange = { label_txt = it },
                        label = { Text("Session Name") },
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = NeonTeal, unfocusedBorderColor = TerminalOutline),
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(horizontalArrangement = Arrangement.End, modifier = Modifier.fillMaxWidth()) {
                        TextButton(onClick = { showSaveSessionDialog = false }) {
                            Text("CANCEL", color = OnSurfaceMuted)
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(
                            onClick = {
                                viewModel.saveRegexSession(label_txt)
                                showSaveSessionDialog = false
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

    // === HISTORIC LOAD DIALOG ===
    if (showHistoryDialog) {
        Dialog(onDismissRequest = { showHistoryDialog = false }) {
            Surface(
                modifier = Modifier
                    .width(340.dp)
                    .height(400.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .border(1.dp, TerminalOutline, RoundedCornerShape(8.dp)),
                color = TerminalSurface
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("HISTORY PLAYGROUNDS", style = MaterialTheme.typography.headlineMedium, color = NeonTeal)
                    Spacer(modifier = Modifier.height(12.dp))
                    Divider(color = TerminalOutline)
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    if (savedSessions.isEmpty()) {
                        Box(modifier = Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                            Text("No items stored offline.", color = OnSurfaceMuted)
                        }
                    } else {
                        LazyColumn(modifier = Modifier.weight(1f).fillMaxWidth()) {
                            items(savedSessions) { session ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp)
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(Color.Black.copy(alpha = 0.2f))
                                        .clickable {
                                            viewModel.loadRegexSession(session)
                                            showHistoryDialog = false
                                        }
                                        .padding(8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(session.label ?: "Unnamed session", color = NeonTeal, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                        Text("/${session.pattern}/", color = Color.White, fontFamily = FontFamily.Monospace, maxLines = 1, fontSize = 11.sp)
                                    }
                                    IconButton(onClick = { viewModel.deleteRegexSession(session) }, modifier = Modifier.size(24.dp)) {
                                        Icon(Icons.Default.Delete, "Delete", tint = MaterialTheme.colorScheme.error)
                                    }
                                }
                            }
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(12.dp))
                    Button(
                        onClick = { showHistoryDialog = false },
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
