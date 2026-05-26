package com.michael.devpocket.playstore

import com.michael.devpocket.data.database.AppDatabase
import com.michael.devpocket.data.database.CodeFile
import com.michael.devpocket.data.database.RegexSession
import com.michael.devpocket.data.database.ScriptSnippet
import com.michael.devpocket.ui.viewmodel.DevPocketViewModel
import kotlinx.coroutines.runBlocking

object PlayStoreTestFixtures {
    fun seed(viewModel: DevPocketViewModel, db: AppDatabase) = runBlocking {
        // 1. Seed workspace code files
        db.codeFileDao().saveFile(
            CodeFile(
                id = "1",
                name = "index.js",
                language = "javascript",
                content = """
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
                """.trimIndent(),
                lastModified = System.currentTimeMillis() - 1000,
                isPinned = true
            ),
            CodeFile(
                id = "2",
                name = "data.json",
                language = "json",
                content = """
                    {
                      "app": "DevPocket",
                      "version": "1.0.0",
                      "status": "active",
                      "features": [
                        "Workspace Code Editor",
                        "High performance JS/Math sandbox",
                        "Smart Regex Playground",
                        "Brutal Code Formatter"
                      ]
                    }
                """.trimIndent(),
                lastModified = System.currentTimeMillis() - 5000,
                isPinned = false
            ),
            CodeFile(
                id = "3",
                name = "style.css",
                language = "css",
                content = """
                    body {
                      background-color: #090a0e;
                      color: #00e5c3;
                      font-family: 'Outfit', monospace;
                    }
                """.trimIndent(),
                lastModified = System.currentTimeMillis() - 10000,
                isPinned = false
            )
        )

        // 2. Seed Regex sessions
        db.regexSessionDao().saveSession(
            RegexSession(
                id = "regex1",
                pattern = "[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}",
                flags = "i,g",
                testInput = "Support email: support@devpocket.io, admin queries: admin@devpocket.io",
                createdAt = System.currentTimeMillis(),
                label = "Email Match"
            )
        )

        // 3. Seed console snippets
        db.scriptSnippetDao().saveSnippet(
            ScriptSnippet(
                id = "snip1",
                title = "Fibonacci Calculator",
                code = """
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
                """.trimIndent(),
                language = "JS",
                createdAt = System.currentTimeMillis()
            )
        )
    }
}
