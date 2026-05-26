package com.michael.devpocket.core.script

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.webkit.JavascriptInterface
import android.webkit.WebView
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.withTimeoutOrNull
import kotlin.math.pow

class ScriptExecutor(private val context: Context) {

    class JSEventBridge {
        var onLog: ((String) -> Unit)? = null
        var onError: ((String) -> Unit)? = null

        @JavascriptInterface
        fun log(msg: String) {
            onLog?.invoke(msg)
        }

        @JavascriptInterface
        fun error(msg: String) {
            onError?.invoke(msg)
        }
    }

    private var cachedWebView: WebView? = null
    private val bridge = JSEventBridge()

    private fun getWebView(): WebView {
        var wv = cachedWebView
        if (wv == null) {
            wv = WebView(context)
            wv.settings.javaScriptEnabled = true
            wv.addJavascriptInterface(bridge, "LogBridge")
            cachedWebView = wv
        }
        return wv
    }

    fun prewarm() {
        try {
            Handler(Looper.getMainLooper()).post {
                try {
                    getWebView()
                } catch (e: Throwable) {
                    // Prevent headless JVM test crashes if WebView is unavailable
                }
            }
        } catch (e: Throwable) {
            // Prevent handler/looper crashes in pure unit tests
        }
    }

    suspend fun executeJs(
        sourceCode: String,
        timeoutMs: Long = 5000L
    ): ExecutionResult {
        val startTime = System.nanoTime()
        val stdout = StringBuilder()
        val stderr = StringBuilder()
        
        val deferredResult = CompletableDeferred<ExecutionResult>()
        
        // Execute on Main Thread because WebViews must be initialized on the main looper.
        // It performs the actual JS evaluation asynchronously on the WebKit thread.
        Handler(Looper.getMainLooper()).post {
            try {
                val wv = getWebView()
                
                bridge.onLog = { msg -> stdout.append(msg).append("\n") }
                bridge.onError = { msg -> stderr.append(msg).append("\n") }
                
                // Polyfill console inside WebView to stream to LogBridge
                val bootstrapCode = """
                    var console = {};
                    console.log = function() {
                        var args = Array.prototype.slice.call(arguments);
                        LogBridge.log(args.map(v => typeof v === 'object' ? JSON.stringify(v) : String(v)).join(' '));
                    };
                    console.error = function() {
                        var args = Array.prototype.slice.call(arguments);
                        LogBridge.error(args.map(v => typeof v === 'object' ? JSON.stringify(v) : String(v)).join(' '));
                    };
                    console.info = console.log;
                    console.warn = console.log;
                """.trimIndent()

                // Safe wrapper execution block
                val userExpression = """
                    (function() {
                        $bootstrapCode
                        try {
                            val_result_raw = (function() {
                                $sourceCode
                            })();
                            if (typeof val_result_raw !== 'undefined') {
                                console.log('[Result] => ' + val_result_raw);
                            }
                        } catch(err) {
                            console.error(err.message || String(err));
                        }
                    })();
                """.trimIndent()
                
                val runUserCode = {
                    wv.evaluateJavascript(userExpression) { result ->
                        val elapsed = (System.nanoTime() - startTime) / 1_000_000L
                        val finalOut = stdout.toString().trim()
                        val finalErr = stderr.toString().trim()
                        
                        val exitCode = if (finalErr.isNotEmpty()) 1 else 0
                        val status = if (finalErr.isNotEmpty()) "runtimeError" else "success"
                        
                        bridge.onLog = null
                        bridge.onError = null
                        
                        deferredResult.complete(
                            ExecutionResult(
                                stdout = finalOut,
                                stderr = finalErr,
                                exitCode = exitCode,
                                wallTimeMs = elapsed,
                                status = status
                            )
                        )
                    }
                }

                wv.webViewClient = object : android.webkit.WebViewClient() {
                    override fun onPageFinished(view: WebView?, url: String?) {
                        wv.webViewClient = android.webkit.WebViewClient()
                        runUserCode()
                    }
                }
                wv.loadUrl("about:blank")
                
            } catch (e: Exception) {
                val elapsed = (System.nanoTime() - startTime) / 1_000_000L
                bridge.onLog = null
                bridge.onError = null
                deferredResult.complete(
                    ExecutionResult(
                        stdout = "",
                        stderr = e.message ?: "Failed to initialize standard WebView sandboxing",
                        exitCode = -1,
                        wallTimeMs = elapsed,
                        status = "runtimeError"
                    )
                )
            }
        }
        
        return withTimeoutOrNull(timeoutMs) {
            deferredResult.await()
        } ?: ExecutionResult(
            stdout = stdout.toString().trim(),
            stderr = "Execution timed out (Limit: ${timeoutMs}ms)",
            exitCode = -2,
            wallTimeMs = timeoutMs,
            status = "timeout"
        )
    }

    fun evaluateBasicMath(expression: String): ExecutionResult {
        val startTime = System.nanoTime()
        val cleanExpr = expression.replace(" ", "")
        
        return try {
            val result = MathExpressionEvaluator.eval(cleanExpr)
            val elapsed = (System.nanoTime() - startTime) / 1_000_000L
            ExecutionResult(
                stdout = "[Math Result] => $result",
                stderr = "",
                exitCode = 0,
                wallTimeMs = elapsed,
                status = "success"
            )
        } catch (e: Exception) {
            val elapsed = (System.nanoTime() - startTime) / 1_000_000L
            ExecutionResult(
                stdout = "",
                stderr = e.message ?: "Calculation error",
                exitCode = 1,
                wallTimeMs = elapsed,
                status = "runtimeError"
            )
        }
    }
}

data class ExecutionResult(
    val stdout: String,
    val stderr: String,
    val exitCode: Int,
    val wallTimeMs: Long,
    val status: String // "success", "runtimeError", "timeout"
)

/**
 * Simple double evaluation for pure offline basic arithmetic expressions without any WebView.
 */
object MathExpressionEvaluator {
    fun eval(str: String): Double {
        return object : Any() {
            var pos = -1
            var ch = 0

            fun nextChar() {
                ch = if (++pos < str.length) str[pos].code else -1
            }

            fun eat(charToEat: Int): Boolean {
                while (ch == ' '.code) nextChar()
                if (ch == charToEat) {
                    nextChar()
                    return true
                }
                return false
            }

            fun parse(): Double {
                nextChar()
                val x = parseExpression()
                if (pos < str.length) throw RuntimeException("Unexpected character: " + ch.toChar())
                return x
            }

            fun parseExpression(): Double {
                var x = parseTerm()
                while (true) {
                    if (eat('+'.code)) x += parseTerm() // addition
                    else if (eat('-'.code)) x -= parseTerm() // subtraction
                    else return x
                }
            }

            fun parseTerm(): Double {
                var x = parseFactor()
                while (true) {
                    if (eat('*'.code)) x *= parseFactor() // multiplication
                    else if (eat('/'.code)) x /= parseFactor() // division
                    else return x
                }
            }

            fun parseFactor(): Double {
                if (eat('+'.code)) return +parseFactor() // unary plus
                if (eat('-'.code)) return -parseFactor() // unary minus

                var x: Double
                val startPos = pos
                if (eat('('.code)) { // parentheses
                    x = parseExpression()
                    if (!eat(')'.code)) throw RuntimeException("Missing closing parenthesis")
                } else if (ch >= '0'.code && ch <= '9'.code || ch == '.'.code) { // numbers
                    while (ch >= '0'.code && ch <= '9'.code || ch == '.'.code) nextChar()
                    x = str.substring(startPos, pos).toDouble()
                } else if (ch >= 'a'.code && ch <= 'z'.code) { // functions
                    while (ch >= 'a'.code && ch <= 'z'.code) nextChar()
                    val func = str.substring(startPos, pos)
                    if (eat('('.code)) {
                        x = parseExpression()
                        if (!eat(')'.code)) throw RuntimeException("Missing closing parenthesis after $func")
                        x = when (func) {
                            "sqrt" -> kotlin.math.sqrt(x)
                            "sin" -> kotlin.math.sin(Math.toRadians(x))
                            "cos" -> kotlin.math.cos(Math.toRadians(x))
                            "tan" -> kotlin.math.tan(Math.toRadians(x))
                            "log" -> kotlin.math.log10(x)
                            "ln" -> kotlin.math.ln(x)
                            else -> throw RuntimeException("Unknown function: $func")
                        }
                    } else {
                        throw RuntimeException("Unknown character after variable name: " + ch.toChar())
                    }
                } else {
                    throw RuntimeException("Unexpected token: " + ch.toChar())
                }

                if (eat('^'.code)) x = x.pow(parseFactor()) // exponentiation

                return x
            }
        }.parse()
    }
}
