package com.michael.devpocket.ui.screens

import android.content.Intent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.michael.devpocket.ui.theme.NeonTeal
import com.michael.devpocket.ui.theme.TerminalOutline
import com.michael.devpocket.ui.theme.TerminalSurface
import com.michael.devpocket.ui.theme.WarningOrange
import com.michael.devpocket.ui.viewmodel.DevPocketViewModel

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun FormatterScreen(
    viewModel: DevPocketViewModel,
    modifier: Modifier = Modifier
) {
    val languages = listOf("JSON", "XML", "HTML", "CSS", "SQL")
    val clipboardManager = LocalClipboardManager.current
    val context = LocalContext.current
    val scrollStateInput = rememberScrollState()
    val scrollStateOutput = rememberScrollState()
    
    val inputBytes = viewModel.formatterInput.toByteArray().size
    val outputBytes = viewModel.formatterOutput.toByteArray().size
    
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(12.dp)
    ) {
        // Horizontal Language Selection Chips
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp)
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            languages.forEach { lang ->
                val isSelected = viewModel.formatterLang == lang
                FilterChip(
                    selected = isSelected,
                    onClick = { viewModel.formatterLang = lang },
                    label = { Text(lang, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = NeonTeal,
                        selectedLabelColor = Color.Black,
                        containerColor = TerminalSurface,
                        labelColor = MaterialTheme.colorScheme.onSurface
                    ),
                    modifier = Modifier.testTag("format_chip_$lang")
                )
            }
        }
        
        // Split Panes: Input & Output
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Panel 1: Input text block
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .border(1.dp, TerminalOutline, RoundedCornerShape(4.dp))
                    .clip(RoundedCornerShape(4.dp))
                    .background(TerminalSurface)
                    .padding(8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "INPUT (${viewModel.formatterLang})",
                        style = MaterialTheme.typography.labelSmall,
                        color = NeonTeal
                    )
                    Text(
                        text = "${viewModel.formatterInput.length} chars · $inputBytes B",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                    )
                }
                Divider(color = TerminalOutline, modifier = Modifier.padding(vertical = 4.dp))
                TextField(
                    value = viewModel.formatterInput,
                    onValueChange = { viewModel.formatterInput = it },
                    textStyle = TextStyle(fontFamily = FontFamily.Monospace, fontSize = 13.sp),
                    placeholder = {
                        Text(
                            "Paste raw ${viewModel.formatterLang} here to prettify or minify...",
                            fontFamily = FontFamily.Monospace,
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
                        )
                    },
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    ),
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .verticalScroll(scrollStateInput)
                        .testTag("formatter_input_field")
                )
            }
            
            // Error banner details
            viewModel.formatterError?.let { err ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(4.dp))
                        .background(MaterialTheme.colorScheme.errorContainer)
                        .border(1.dp, MaterialTheme.colorScheme.error, RoundedCornerShape(4.dp))
                        .padding(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Error,
                        contentDescription = "Error",
                        tint = MaterialTheme.colorScheme.onErrorContainer,
                        modifier = Modifier.padding(end = 8.dp)
                    )
                    Text(
                        text = "PARSE EXCEPTION: $err",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onErrorContainer,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 11.sp
                    )
                }
            }
            
            // Panel 2: Output text block
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .border(1.dp, TerminalOutline, RoundedCornerShape(4.dp))
                    .clip(RoundedCornerShape(4.dp))
                    .background(TerminalSurface)
                    .padding(8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "OUTPUT",
                        style = MaterialTheme.typography.labelSmall,
                        color = NeonTeal
                    )
                    Text(
                        text = "${viewModel.formatterOutput.length} chars · $outputBytes B",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                    )
                }
                Divider(color = TerminalOutline, modifier = Modifier.padding(vertical = 4.dp))
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .verticalScroll(scrollStateOutput)
                        .padding(8.dp)
                ) {
                    Text(
                        text = viewModel.formatterOutput.ifEmpty { "// Formatted output code will render here..." },
                        fontFamily = FontFamily.Monospace,
                        fontSize = 13.sp,
                        color = if (viewModel.formatterOutput.isEmpty()) MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f) else MaterialTheme.colorScheme.onBackground
                    )
                }
            }
        }
        
        Spacer(modifier = Modifier.height(12.dp))
        
        // Toolbar controls
        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                onClick = { viewModel.executeFormat(prettify = true) },
                colors = ButtonDefaults.buttonColors(containerColor = NeonTeal),
                shape = RoundedCornerShape(4.dp),
                modifier = Modifier.testTag("prettify_button")
            ) {
                Icon(Icons.Default.FormatAlignLeft, "Prettify", tint = Color.Black, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("PRETTIFY", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 12.sp)
            }
            
            // Minify is only valid for JSON/XML/HTML/CSS and SQL
            Button(
                onClick = { viewModel.executeFormat(prettify = false) },
                colors = ButtonDefaults.buttonColors(containerColor = WarningOrange),
                shape = RoundedCornerShape(4.dp),
                modifier = Modifier.testTag("minify_button")
            ) {
                Icon(Icons.Default.Compress, "Minify", tint = Color.Black, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("MINIFY", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 12.sp)
            }
            
            OutlinedButton(
                onClick = {
                    clipboardManager.setText(AnnotatedString(viewModel.formatterOutput))
                },
                shape = RoundedCornerShape(4.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
                border = BorderStroke(1.dp, TerminalOutline)
            ) {
                Icon(Icons.Default.ContentCopy, "Copy", modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("COPY", fontSize = 11.sp)
            }
            
            OutlinedButton(
                onClick = { viewModel.swapFormatterInOut() },
                shape = RoundedCornerShape(4.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
                border = BorderStroke(1.dp, TerminalOutline)
            ) {
                Icon(Icons.Default.SwapVert, "Swap", modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("SWAP", fontSize = 11.sp)
            }
            
            OutlinedButton(
                onClick = {
                    val shareIntent = Intent().apply {
                        action = Intent.ACTION_SEND
                        putExtra(Intent.EXTRA_TEXT, viewModel.formatterOutput)
                        type = "text/plain"
                    }
                    context.startActivity(Intent.createChooser(shareIntent, "Share output bytes"))
                },
                shape = RoundedCornerShape(4.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
                border = BorderStroke(1.dp, TerminalOutline)
            ) {
                Icon(Icons.Default.Share, "Share", modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("SHARE", fontSize = 11.sp)
            }
            
            OutlinedButton(
                onClick = { viewModel.clearFormatter() },
                shape = RoundedCornerShape(4.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.error.copy(alpha = 0.5f))
            ) {
                Icon(Icons.Default.Clear, "Clear", modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("CLEAR", fontSize = 11.sp)
            }
        }
    }
}
