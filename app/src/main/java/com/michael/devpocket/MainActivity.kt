package com.michael.devpocket

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.michael.devpocket.ui.screens.*
import com.michael.devpocket.ui.theme.MyApplicationTheme
import com.michael.devpocket.ui.theme.NeonTeal
import com.michael.devpocket.ui.theme.TerminalOutline
import com.michael.devpocket.ui.theme.TerminalSurface
import com.michael.devpocket.ui.viewmodel.DevPocketViewModel

class MainActivity : ComponentActivity() {
    private val viewModel: DevPocketViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        setContent {
            MyApplicationTheme {
                var selectedTab by rememberSaveable { mutableIntStateOf(0) }
                
                val tabs = listOf(
                    TabData(0, "Editor", Icons.Filled.Edit, Icons.Outlined.Edit, "editor_tab"),
                    TabData(1, "Formatter", Icons.Filled.FormatPaint, Icons.Outlined.FormatPaint, "formatter_tab"),
                    TabData(2, "Regex", Icons.Filled.Search, Icons.Outlined.Search, "regex_tab"),
                    TabData(3, "Docs", Icons.Filled.Topic, Icons.Outlined.Topic, "docs_tab"),
                    TabData(4, "Console", Icons.Filled.SettingsInputHdmi, Icons.Outlined.SettingsInputComposite, "console_tab")
                )

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    bottomBar = {
                        NavigationBar(
                            containerColor = TerminalSurface,
                            tonalElevation = 0.dp,
                            modifier = Modifier
                                .windowInsetsPadding(WindowInsets.navigationBars)
                                .height(64.dp)
                                .border(width = 1.dp, color = TerminalOutline)
                        ) {
                            tabs.forEach { tab ->
                                val active = selectedTab == tab.index
                                NavigationBarItem(
                                    selected = active,
                                    onClick = { selectedTab = tab.index },
                                    icon = {
                                        Icon(
                                            imageVector = if (active) tab.activeIcon else tab.inactiveIcon,
                                            contentDescription = tab.label,
                                            tint = if (active) NeonTeal else MaterialTheme.colorScheme.onSurface
                                        )
                                    },
                                    colors = NavigationBarItemDefaults.colors(
                                        indicatorColor = TerminalOutline
                                    ),
                                    modifier = Modifier.testTag(tab.testTag)
                                )
                            }
                        }
                    },
                    contentWindowInsets = WindowInsets.statusBars
                ) { innerPadding ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(MaterialTheme.colorScheme.background)
                            .padding(innerPadding)
                    ) {
                        // Switch content container reactively
                        when (selectedTab) {
                            0 -> WorkspaceScreen(viewModel = viewModel)
                            1 -> FormatterScreen(viewModel = viewModel)
                            2 -> RegexScreen(viewModel = viewModel)
                            3 -> VaultScreen(viewModel = viewModel)
                            4 -> ConsoleScreen(viewModel = viewModel)
                        }
                    }
                }
            }
        }
    }
}

data class TabData(
    val index: Int,
    val label: String,
    val activeIcon: androidx.compose.ui.graphics.vector.ImageVector,
    val inactiveIcon: androidx.compose.ui.graphics.vector.ImageVector,
    val testTag: String
)
