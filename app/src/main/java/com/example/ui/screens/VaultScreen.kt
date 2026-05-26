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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.docs.DocBundle
import com.example.data.docs.DocPage
import com.example.data.docs.DocumentationVault
import com.example.ui.theme.*
import com.example.ui.viewmodel.DevPocketViewModel

@Composable
fun VaultScreen(
    viewModel: DevPocketViewModel,
    modifier: Modifier = Modifier
) {
    val activePage = viewModel.selectedPage
    val activeBundle = viewModel.selectedBundle

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        if (activePage != null) {
            // View Page Detail Reader Screen
            PageReaderView(
                page = activePage,
                isBookmarked = viewModel.bookmarkedPages.contains(activePage.id),
                onToggleBookmark = { viewModel.toggleBookmarkPage(activePage.id) },
                onBack = { viewModel.selectedPage = null }
            )
        } else if (activeBundle != null) {
            // View Bundle Pages List Screen
            BundlePagesListView(
                bundle = activeBundle,
                viewModel = viewModel,
                onBack = { viewModel.selectedBundle = null }
            )
        } else {
            // Main Library View Screen
            MainLibraryView(viewModel = viewModel)
        }
    }
}

@Composable
fun MainLibraryView(
    viewModel: DevPocketViewModel
) {
    val bundles = DocumentationVault.bundles
    val filteredPages = viewModel.getFilteredPages()
    val bookmarkedIds = viewModel.bookmarkedPages

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(12.dp)
    ) {
        Text(
            "OFFLINE DOCUMENTATION VAULT",
            style = MaterialTheme.typography.labelLarge,
            color = NeonTeal,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        // Reactive search bar
        OutlinedTextField(
            value = viewModel.docSearchQuery,
            onValueChange = { viewModel.docSearchQuery = it },
            placeholder = { Text("Search docs (e.g. 404, anchors, lists, regex)...", fontSize = 13.sp) },
            leadingIcon = { Icon(Icons.Default.Search, "Search", tint = NeonTeal) },
            trailingIcon = {
                if (viewModel.docSearchQuery.isNotEmpty()) {
                    IconButton(onClick = { viewModel.docSearchQuery = "" }) {
                        Icon(Icons.Default.Clear, "Clear", tint = OnSurfaceMuted)
                    }
                }
            },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = NeonTeal,
                unfocusedBorderColor = TerminalOutline,
                focusedContainerColor = TerminalSurface,
                unfocusedContainerColor = TerminalSurface
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp)
                .testTag("doc_search_bar"),
            singleLine = true
        )

        if (viewModel.docSearchQuery.isNotEmpty()) {
            // Display Global Search Results matching terms
            Text(
                "SEARCH RESULTS (${filteredPages.size})",
                fontSize = 11.sp,
                fontFamily = FontFamily.Monospace,
                color = SoftPurple,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            if (filteredPages.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .border(1.dp, TerminalOutline, RoundedCornerShape(4.dp))
                        .background(TerminalSurface),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "No pages matching search parameters.",
                        color = OnSurfaceMuted,
                        fontFamily = FontFamily.Monospace
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(filteredPages) { page ->
                        SearchPageResultCard(
                            page = page,
                            query = viewModel.docSearchQuery,
                            onClick = { viewModel.selectedPage = page }
                        )
                    }
                }
            }
        } else {
            // Default Library of bundles view
            Text(
                "BUNDLED SYSTEMS LIBRARY",
                fontSize = 11.sp,
                fontFamily = FontFamily.Monospace,
                color = OnSurfaceMuted,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(bundles) { bundle ->
                    Card(
                        colors = CardDefaults.cardColors(containerColor = TerminalSurface),
                        border = BorderStroke(1.dp, TerminalOutline),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { viewModel.selectedBundle = bundle }
                            .testTag("bundle_card_${bundle.id}")
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = bundle.iconSymbol,
                                fontSize = 28.sp,
                                modifier = Modifier.padding(end = 12.dp)
                            )
                            Column(modifier = Modifier.weight(1f)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = bundle.title,
                                        style = MaterialTheme.typography.titleLarge,
                                        color = NeonTeal,
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = bundle.version,
                                        fontFamily = FontFamily.Monospace,
                                        fontSize = 10.sp,
                                        color = SoftPurple
                                    )
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = bundle.description,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = OnBackgroundBright,
                                    lineHeight = 16.sp,
                                    maxLines = 2,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Row {
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(2.dp))
                                            .background(TerminalOutline)
                                            .padding(horizontal = 4.dp, vertical = 2.dp)
                                    ) {
                                        Text(
                                            "${bundle.pageCount} PAGES",
                                            fontFamily = FontFamily.Monospace,
                                            fontSize = 9.sp,
                                            color = Color.White
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(2.dp))
                                            .background(NeonTeal.copy(alpha = 0.15f))
                                            .padding(horizontal = 4.dp, vertical = 2.dp)
                                    ) {
                                        Text(
                                            "OFFLINE READY",
                                            fontFamily = FontFamily.Monospace,
                                            fontSize = 9.sp,
                                            color = NeonTeal
                                        )
                                    }
                                }
                            }
                            Icon(
                                imageVector = Icons.Default.ChevronRight,
                                contentDescription = "Open Bundle",
                                tint = NeonTeal,
                                modifier = Modifier.padding(start = 4.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun BundlePagesListView(
    bundle: DocBundle,
    viewModel: DevPocketViewModel,
    onBack: () -> Unit
) {
    val pages = viewModel.getFilteredPages() // Filtered pages will narrow elements automatically

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(12.dp)
    ) {
        // Nav Header
        Row(
            modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.Default.ArrowBack, "Back", tint = NeonTeal)
            }
            Text(
                text = "${bundle.iconSymbol} ${bundle.title.uppercase()}",
                style = MaterialTheme.typography.headlineMedium,
                color = NeonTeal,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(1f)
            )
        }

        OutlinedTextField(
            value = viewModel.docSearchQuery,
            onValueChange = { viewModel.docSearchQuery = it },
            placeholder = { Text("Filter this directory structure...", fontSize = 13.sp) },
            leadingIcon = { Icon(Icons.Default.FilterList, "Filter", tint = NeonTeal) },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = NeonTeal,
                unfocusedBorderColor = TerminalOutline,
                focusedContainerColor = TerminalSurface,
                unfocusedContainerColor = TerminalSurface
            ),
            modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
            singleLine = true
        )

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.weight(1f)
        ) {
            items(pages) { page ->
                Card(
                    colors = CardDefaults.cardColors(containerColor = TerminalSurface),
                    border = BorderStroke(1.dp, TerminalOutline),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { viewModel.selectedPage = page }
                        .testTag("page_item_${page.id}")
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = page.title,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                fontSize = 14.sp
                            )
                            Icon(Icons.Default.OpenInNew, "Read", tint = NeonTeal, modifier = Modifier.size(16.dp))
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            modifier = Modifier.padding(top = 4.dp)
                        ) {
                            page.tags.forEach { tag ->
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(2.dp))
                                        .background(TerminalOutline)
                                        .padding(horizontal = 4.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        "#$tag",
                                        fontSize = 9.sp,
                                        fontFamily = FontFamily.Monospace,
                                        color = OnSurfaceMuted
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SearchPageResultCard(
    page: DocPage,
    query: String,
    onClick: () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = TerminalSurface),
        border = BorderStroke(1.dp, TerminalOutline),
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            Text(
                text = page.title,
                fontWeight = FontWeight.Bold,
                color = NeonTeal,
                fontSize = 13.sp
            )
            Spacer(modifier = Modifier.height(4.dp))
            
            // Build simple highlighted snippet snippet
            val fullContent = page.markdownContent.replace("\n", " ").take(120) + "..."
            val highlightedSnippet = buildAnnotatedString {
                var currentIndex = 0
                val qLower = query.lowercase()
                val fullLower = fullContent.lowercase()
                
                while (currentIndex < fullContent.length) {
                    val index = if (qLower.isNotEmpty()) fullLower.indexOf(qLower, currentIndex) else -1
                    if (index == -1) {
                        append(fullContent.substring(currentIndex))
                        break
                    } else {
                        append(fullContent.substring(currentIndex, index))
                        withStyle(style = SpanStyle(background = NeonTeal.copy(alpha = 0.4f), color = Color.White)) {
                            append(fullContent.substring(index, index + query.length))
                        }
                        currentIndex = index + query.length
                    }
                }
            }
            Text(
                text = highlightedSnippet,
                style = MaterialTheme.typography.bodyMedium,
                color = OnSurfaceMuted,
                fontSize = 11.sp,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
fun PageReaderView(
    page: DocPage,
    isBookmarked: Boolean,
    onToggleBookmark: () -> Unit,
    onBack: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(12.dp)
    ) {
        // Reader Header controls
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.Default.ArrowBack, "Back", tint = NeonTeal)
            }
            Row {
                IconButton(onClick = onToggleBookmark, modifier = Modifier.testTag("bookmark_toggle")) {
                    Icon(
                        imageVector = if (isBookmarked) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                        contentDescription = "Bookmark",
                        tint = if (isBookmarked) SoftPurple else NeonTeal
                    )
                }
            }
        }

        // Article Title
        Text(
            text = page.title,
            style = MaterialTheme.typography.displayLarge,
            color = Color.White,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 10.dp)
        )

        Divider(color = TerminalOutline, modifier = Modifier.padding(bottom = 12.dp))

        // Native custom Markdown AST parser renderer (beautifully aligned to Refined Brutalism theme)
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            val lines = page.markdownContent.split("\n")
            var inCodeBlock = false
            val codeLines = mutableListOf<String>()

            lines.forEach { line ->
                val trimmed = line.trim()
                
                when {
                    trimmed.startsWith("```") -> {
                        if (inCodeBlock) {
                            // Render code block card
                            CodeBlockCard(code = codeLines.joinToString("\n"))
                            codeLines.clear()
                            inCodeBlock = false
                        } else {
                            inCodeBlock = true
                        }
                    }
                    inCodeBlock -> {
                        codeLines.add(line)
                    }
                    trimmed.startsWith("# ") -> {
                        Text(
                            text = trimmed.substring(2),
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = NeonTeal,
                            fontFamily = FontFamily.Monospace,
                            modifier = Modifier.padding(top = 6.dp)
                        )
                    }
                    trimmed.startsWith("## ") -> {
                        Text(
                            text = trimmed.substring(3),
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = SoftPurple,
                            fontFamily = FontFamily.Monospace,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                    trimmed.startsWith("### ") -> {
                        Text(
                            text = trimmed.substring(4),
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = WarningOrange,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                    trimmed.startsWith("> ") -> {
                        // Render Quote block
                        QuoteBlockCard(text = trimmed.substring(2))
                    }
                    trimmed.startsWith("* ") || trimmed.startsWith("- ") -> {
                        Row(modifier = Modifier.padding(start = 6.dp)) {
                            Text("• ", color = NeonTeal, fontWeight = FontWeight.Bold)
                            Text(parseTextStyles(trimmed.substring(2)), color = OnBackgroundBright, fontSize = 13.sp)
                        }
                    }
                    trimmed.startsWith("1. ") || trimmed.startsWith("2. ") || trimmed.startsWith("3. ") -> {
                        val num = trimmed.substringBefore(" ")
                        val text = trimmed.substringAfter(" ")
                        Row(modifier = Modifier.padding(start = 6.dp)) {
                            Text("$num ", color = SoftPurple, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold)
                            Text(parseTextStyles(text), color = OnBackgroundBright, fontSize = 13.sp)
                        }
                    }
                    trimmed.isEmpty() -> {
                        // Empty line space
                    }
                    else -> {
                        // Standard paragraph
                        Text(
                            text = parseTextStyles(line),
                            style = MaterialTheme.typography.bodyLarge,
                            color = OnBackgroundBright,
                            fontSize = 13.sp,
                            lineHeight = 18.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun CodeBlockCard(code: String) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color.Black.copy(alpha = 0.5f)),
        border = BorderStroke(1.dp, TerminalOutline),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Column(modifier = Modifier.padding(8.dp)) {
            Text(
                text = code,
                fontFamily = FontFamily.Monospace,
                fontSize = 11.sp,
                color = NeonTeal,
                lineHeight = 15.sp
            )
        }
    }
}

@Composable
fun QuoteBlockCard(text: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .height(IntrinsicSize.Min)
            .background(TerminalSurface.copy(alpha = 0.5f))
    ) {
        Box(
            modifier = Modifier
                .width(3.dp)
                .fillMaxHeight()
                .background(SoftPurple)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = text,
            fontFamily = FontFamily.SansSerif,
            fontSize = 12.sp,
            fontStyle = FontStyle.Italic,
            color = OnBackgroundBright,
            lineHeight = 16.sp,
            modifier = Modifier.padding(vertical = 6.dp, horizontal = 4.dp)
        )
    }
}

// Simple custom inline Markdown style tags parser (e.g. converting `**bold**` or `inline monospace code`)
fun parseTextStyles(rawText: String): AnnotatedString {
    return buildAnnotatedString {
        // 1. Parse inline monospace code (enclosed in single ` backticks)
        // 2. Parse bold blocks (enclosed in **)
        var i = 0
        while (i < rawText.length) {
            when {
                rawText.startsWith("`", i) -> {
                    val endIdx = rawText.indexOf("`", i + 1)
                    if (endIdx != -1) {
                        withStyle(style = SpanStyle(fontFamily = FontFamily.Monospace, color = NeonTeal, background = Color.Black.copy(alpha = 0.25f))) {
                            append(rawText.substring(i + 1, endIdx))
                        }
                        i = endIdx + 1
                    } else {
                        append("`")
                        i++
                    }
                }
                rawText.startsWith("**", i) -> {
                    val endIdx = rawText.indexOf("**", i + 2)
                    if (endIdx != -1) {
                        withStyle(style = SpanStyle(fontWeight = FontWeight.Bold, color = Color.White)) {
                            append(rawText.substring(i + 2, endIdx))
                        }
                        i = endIdx + 2
                    } else {
                        append("**")
                        i += 2
                    }
                }
                else -> {
                    append(rawText[i].toString())
                    i++
                }
            }
        }
    }
}
