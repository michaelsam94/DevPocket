package com.michael.devpocket.playstore

import android.app.Application
import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.drawable.toBitmap
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.michael.devpocket.R
import com.michael.devpocket.data.database.AppDatabase
import com.michael.devpocket.ui.screens.*
import com.michael.devpocket.ui.theme.MyApplicationTheme
import com.michael.devpocket.ui.viewmodel.DevPocketViewModel
import org.junit.After
import org.junit.Test
import org.junit.experimental.categories.Category
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

@RunWith(RobolectricTestRunner::class)
@Category(PlayStoreScreenshotTests::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(sdk = [35])
class PlayStoreScreenshotTest {

    private val app: Application
        get() = ApplicationProvider.getApplicationContext()

    private fun createSeededPlayStoreViewModel(application: Application): DevPocketViewModel {
        AppDatabase.resetForTests()
        val db = Room.inMemoryDatabaseBuilder(
            application,
            AppDatabase::class.java
        )
        .allowMainThreadQueries()
        .build()
        AppDatabase.inMemoryForTests = db

        val viewModel = DevPocketViewModel(application)
        PlayStoreTestFixtures.seed(viewModel, db)
        return viewModel
    }

    @After
    fun tearDown() {
        AppDatabase.resetForTests()
    }

    // ==========================================
    // PHONE SCREENSHOTS (1080 x 1920)
    // ==========================================

    @Test
    @Config(qualifiers = "w360dp-h640dp-xxhdpi") // phone 1080×1920
    fun phone_01_dashboard() {
        val viewModel = createSeededPlayStoreViewModel(app)
        // Seed workspace file view state
        viewModel.editorFileName = "index.js"
        viewModel.editorLang = "javascript"
        viewModel.editorText = """
            // DevPocket Workspace
            console.log("Analyzing system properties...");
            const os = require('os');
            function systemInfo() {
              return {
                platform: os.platform(),
                arch: os.arch(),
                cpus: os.cpus().length,
                freeMemMB: Math.round(os.freemem() / 1024 / 1024)
              };
            }
            console.log(JSON.stringify(systemInfo(), null, 2));
        """.trimIndent()

        capturePlayStoreImage("phone/01_dashboard.png") {
            MyApplicationTheme {
                WorkspaceScreen(viewModel = viewModel)
            }
        }
    }

    @Test
    @Config(qualifiers = "w360dp-h640dp-xxhdpi")
    fun phone_02_formatter() {
        val viewModel = createSeededPlayStoreViewModel(app)
        viewModel.formatterLang = "JSON"
        viewModel.formatterInput = "{\"app\":\"DevPocket\",\"status\":\"active\",\"build\":36}"
        viewModel.formatterOutput = """
            {
              "app": "DevPocket",
              "status": "active",
              "build": 36
            }
        """.trimIndent()

        capturePlayStoreImage("phone/02_formatter.png") {
            MyApplicationTheme {
                FormatterScreen(viewModel = viewModel)
            }
        }
    }

    @Test
    @Config(qualifiers = "w360dp-h640dp-xxhdpi")
    fun phone_03_regex() {
        val viewModel = createSeededPlayStoreViewModel(app)
        viewModel.regexPattern = "[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}"
        viewModel.regexInput = "Send reports to support@devpocket.io or test admin@localhost.com."
        viewModel.evaluateRegex()

        capturePlayStoreImage("phone/03_regex.png") {
            MyApplicationTheme {
                RegexScreen(viewModel = viewModel)
            }
        }
    }

    @Test
    @Config(qualifiers = "w360dp-h640dp-xxhdpi")
    fun phone_04_console() {
        val viewModel = createSeededPlayStoreViewModel(app)
        viewModel.consoleMode = "JS"
        viewModel.consoleScript = """
            function fib(n) {
              if (n <= 1) return BigInt(n);
              let a = 0n, b = 1n;
              for (let i = 2; i <= n; i++) {
                let temp = a + b;
                a = b;
                b = temp;
              }
              return b;
            }
            console.log("Fib(200): " + fib(200).toString());
        """.trimIndent()
        viewModel.consoleStdout = "Starting execution engine...\nFib(200): 28057117299251014003761193241300755419705351"
        viewModel.consoleStderr = ""

        capturePlayStoreImage("phone/04_console.png") {
            MyApplicationTheme {
                ConsoleScreen(viewModel = viewModel)
            }
        }
    }

    // ==========================================
    // TABLET SCREENSHOTS (1600 x 2560)
    // ==========================================

    @Test
    @Config(qualifiers = "w800dp-h1280dp-xhdpi") // tablet 1600×2560
    fun tablet_01_dashboard() {
        val viewModel = createSeededPlayStoreViewModel(app)
        viewModel.editorFileName = "index.js"
        viewModel.editorLang = "javascript"
        viewModel.editorText = """
            // DevPocket Workspace
            console.log("Analyzing system properties...");
            const os = require('os');
            function systemInfo() {
              return {
                platform: os.platform(),
                arch: os.arch(),
                cpus: os.cpus().length,
                freeMemMB: Math.round(os.freemem() / 1024 / 1024)
              };
            }
            console.log(JSON.stringify(systemInfo(), null, 2));
        """.trimIndent()

        capturePlayStoreImage("tablet/01_dashboard.png") {
            MyApplicationTheme {
                WorkspaceScreen(viewModel = viewModel)
            }
        }
    }

    @Test
    @Config(qualifiers = "w800dp-h1280dp-xhdpi")
    fun tablet_02_formatter() {
        val viewModel = createSeededPlayStoreViewModel(app)
        viewModel.formatterLang = "JSON"
        viewModel.formatterInput = "{\"app\":\"DevPocket\",\"status\":\"active\",\"build\":36}"
        viewModel.formatterOutput = """
            {
              "app": "DevPocket",
              "status": "active",
              "build": 36
            }
        """.trimIndent()

        capturePlayStoreImage("tablet/02_formatter.png") {
            MyApplicationTheme {
                FormatterScreen(viewModel = viewModel)
            }
        }
    }

    @Test
    @Config(qualifiers = "w800dp-h1280dp-xhdpi")
    fun tablet_03_regex() {
        val viewModel = createSeededPlayStoreViewModel(app)
        viewModel.regexPattern = "[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}"
        viewModel.regexInput = "Send reports to support@devpocket.io or test admin@localhost.com."
        viewModel.evaluateRegex()

        capturePlayStoreImage("tablet/03_regex.png") {
            MyApplicationTheme {
                RegexScreen(viewModel = viewModel)
            }
        }
    }

    @Test
    @Config(qualifiers = "w800dp-h1280dp-xhdpi")
    fun tablet_04_console() {
        val viewModel = createSeededPlayStoreViewModel(app)
        viewModel.consoleMode = "JS"
        viewModel.consoleScript = """
            function fib(n) {
              if (n <= 1) return BigInt(n);
              let a = 0n, b = 1n;
              for (let i = 2; i <= n; i++) {
                let temp = a + b;
                a = b;
                b = temp;
              }
              return b;
            }
            console.log("Fib(200): " + fib(200).toString());
        """.trimIndent()
        viewModel.consoleStdout = "Starting execution engine...\nFib(200): 28057117299251014003761193241300755419705351"
        viewModel.consoleStderr = ""

        capturePlayStoreImage("tablet/04_console.png") {
            MyApplicationTheme {
                ConsoleScreen(viewModel = viewModel)
            }
        }
    }

    // ==========================================
    // PLAY STORE FEATURE GRAPHIC (1024 x 500)
    // ==========================================

    @Test
    @Config(qualifiers = "w1024dp-h500dp-mdpi") // feature 1024×500
    fun feature_graphic() {
        capturePlayStoreImage("feature-graphic.png") {
            FeatureGraphicContent()
        }
    }

    // ==========================================
    // PLAY STORE APP ICON (512 x 512)
    // ==========================================

    @Test
    @Config(qualifiers = "w512dp-h512dp-mdpi") // app icon 512×512
    fun app_icon_512() {
        val ctx = ApplicationProvider.getApplicationContext<Context>()
        capturePlayStoreImage("app-icon-512.png") {
            MyApplicationTheme {
                val iconBitmap = remember {
                    checkNotNull(ctx.getDrawable(R.mipmap.ic_launcher))
                        .toBitmap(512, 512)
                        .asImageBitmap()
                }
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color(0xFF090A0E)),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        bitmap = iconBitmap,
                        contentDescription = "App Icon",
                        modifier = Modifier.size(360.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun FeatureGraphicContent() {
    MyApplicationTheme {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.linearGradient(
                        colors = listOf(
                            Color(0xFF090A0E), // TerminalBackground
                            Color(0xFF13151A), // TerminalSurface
                            Color(0xFF1E212A)  // TerminalSurfaceVariant
                        )
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.padding(24.dp)
            ) {
                // Branded Box border
                Box(
                    modifier = Modifier
                        .border(2.dp, Color(0xFF00E5C3), RoundedCornerShape(8.dp))
                        .padding(horizontal = 24.dp, vertical = 12.dp)
                ) {
                    Text(
                        text = "DEVPOCKET",
                        style = TextStyle(
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            fontSize = 64.sp,
                            color = Color(0xFF00E5C3), // NeonTeal
                            letterSpacing = 4.sp
                        )
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Offline Developer Sandbox & Utility Suite",
                    style = TextStyle(
                        fontFamily = FontFamily.Monospace,
                        fontSize = 20.sp,
                        color = Color(0xFF9CA3AF) // OnSurfaceMuted
                    )
                )
                Spacer(modifier = Modifier.height(32.dp))
                // Visual indicators representing features
                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    FeatureBadge("CODE WORKSPACE")
                    FeatureBadge("BRUTAL FORMATTER")
                    FeatureBadge("SMART REGEX")
                    FeatureBadge("JS SANDBOX")
                }
            }
        }
    }
}

@Composable
fun FeatureBadge(text: String) {
    Box(
        modifier = Modifier
            .border(1.dp, Color(0xFF2C313D), RoundedCornerShape(4.dp))
            .background(Color(0xFF13151A))
            .padding(horizontal = 12.dp, vertical = 6.dp)
    ) {
        Text(
            text = text,
            style = TextStyle(
                fontFamily = FontFamily.Monospace,
                fontSize = 11.sp,
                color = Color(0xFF00E5C3),
                fontWeight = FontWeight.Bold
            )
        )
    }
}
