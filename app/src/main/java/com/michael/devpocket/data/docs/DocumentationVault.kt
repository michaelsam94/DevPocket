package com.michael.devpocket.data.docs

data class DocBundle(
    val id: String,
    val title: String,
    val version: String,
    val pageCount: Int,
    val description: String,
    val iconSymbol: String // Emoji or visual symbol for the card
)

data class DocPage(
    val id: String,
    val bundleId: String,
    val title: String,
    val markdownContent: String,
    val tags: List<String>
)

object DocumentationVault {

    val bundles = listOf(
        DocBundle(
            id = "json_spec",
            title = "JSON Quick Reference",
            version = "RFC 8259",
            pageCount = 4,
            description = "JavaScript Object Notation syntax guidelines, types, structures, and common schemas.",
            iconSymbol = "📦"
        ),
        DocBundle(
            id = "regex_cheatsheet",
            title = "Regex Playground Cheat Sheet",
            version = "PCRE v2",
            pageCount = 5,
            description = "Regular expression anchors, quantifiers, lookarounds, characters, and capture group syntax.",
            iconSymbol = "🔍"
        ),
        DocBundle(
            id = "markdown_guide",
            title = "Markdown Syntax Guide",
            version = "CommonMark v1.0",
            pageCount = 4,
            description = "Lightweight styling elements, lists, image blocks, tabular records, and code backticks.",
            iconSymbol = "📝"
        ),
        DocBundle(
            id = "bash_commands",
            title = "Common Shell & Bash Guide",
            version = "POSIX Standard",
            pageCount = 5,
            description = "Crucial Unix commands, directory navigators, pipes redirection, and conditional bash scripts.",
            iconSymbol = "⚙️"
        ),
        DocBundle(
            id = "http_status",
            title = "HTTP Status Codes Vault",
            version = "HTTP/1.1 & HTTP/2",
            pageCount = 5,
            description = "Detailed references for Web responses: 1xx, 2xx, 3xx, 4xx client issues, and 5xx backend crashes.",
            iconSymbol = "🌐"
        )
    )

    val pages = listOf(
        // === JSON SPEC PAGES ===
        DocPage(
            id = "json_basics",
            bundleId = "json_spec",
            title = "JSON Values & Syntax Rules",
            markdownContent = """
                # JSON Basic Rules
                JSON (JavaScript Object Notation) is a lightweight data-interchange text format. It is completely language-independent but uses familiar C-family programming conventions.
                
                ### Strict Syntax Mandates:
                * **Double Quotes**: Keys and string values *must* be enclosed in double quotes `"key" : "value"`. Single quotes are invalid.
                * **Trailing Commas**: Trailing commas are strictly prohibited (e.g. `{"a": 1,}` will fail parsing).
                * **No Comments**: Standard JSON does not support inline comments (`//` or `/* */`).
                
                ### Permissible Object Types:
                1. **String**: Enclosed in `"..."` with backslash escaping (`\"`, `\\`, `\n`).
                2. **Number**: Integer or floating-point (e.g., `42`, `-3.14`, `2.997e8`).
                3. **Object**: Unordered key-value parameters wrapped in `{...}`.
                4. **Array**: Ordered sequence value stacks wrapped in `[...]`.
                5. **Boolean**: Literal `true` or `false` (lowercase).
                6. **Null**: Literal `null` (lowercase).
            """.trimIndent(),
            tags = listOf("json", "syntax", "rules", "types", "quotes")
        ),
        DocPage(
            id = "json_objects_arrays",
            bundleId = "json_spec",
            title = "Objects vs Arrays structures",
            markdownContent = """
                # Objects and Arrays
                In JSON, elements nested are built on collection pairs:
                
                ### 1. JSON Objects `{}`
                An object structure is an unordered set of name/value pairs. An object begins with `{` (left curly bracket) and ends with `}` (right curly bracket). Each name is followed by `:` (colon) and the name/value pairs are separated by `,` (comma).
                
                ```json
                {
                  "developer": {
                    "name": "Jane",
                    "role": "Systems Architect",
                    "languages": ["Kotlin", "Rust", "Go"]
                  }
                }
                ```
                
                ### 2. JSON Arrays `[]`
                An array is an ordered collection of values. An array begins with `[` (left bracket) and ends with `]` (right bracket). Values are separated by `,` (comma).
                
                ```json
                [
                  "electric_teal",
                  "warning_orange",
                  "soft_violet"
                ]
                ```
            """.trimIndent(),
            tags = listOf("json", "objects", "arrays", "nested", "lists")
        ),
        DocPage(
            id = "json_escaping",
            bundleId = "json_spec",
            title = "String Escaping Reference",
            markdownContent = """
                # String Escaping in JSON
                Any character inside a JSON string may be escaped using a backslash `\`. 
                The following escaping sequences are standard:
                
                * `\"` - Quotation mark
                * `\\` - Backslash
                * `\/` - Slash
                * `\b` - Backspace
                * `\f` - Form feed
                * `\n` - New line
                * `\r` - Carriage return
                * `\t` - Tab
                * `\u` followed by 4 hexadecimal digits - Unicode character (e.g., `\u00E5` for `å`)
            """.trimIndent(),
            tags = listOf("json", "escaping", "slash", "unicode", "characters")
        ),
        DocPage(
            id = "json_validation",
            bundleId = "json_spec",
            title = "Validation & Schemas Overview",
            markdownContent = """
                # JSON Validation Check
                When evaluating user JSON, validating syntax is critical to avoid crashes.
                
                ### Common Validation Failures:
                1. **Unclosed Brackets**: e.g. `{"count": 4` (missing `}`).
                2. **Missing Quotes**: e.g. `{name: "Alex"}` (keys *must* have `"..."`).
                3. **Injected Hex Characters**: Putting unescaped binary breaks strings.
                
                DevPocket's **Formatting Suite** uses Android's built-in validating stream. It parses your input, highlights errors, and isolates exact lines that break parser specifications.
            """.trimIndent(),
            tags = listOf("json", "validation", "schemas", "errors", "parsers")
        ),

        // === REGEX CHEATSHEET ===
        DocPage(
            id = "regex_anchors",
            bundleId = "regex_cheatsheet",
            title = "Regex Anchors & Boundaries",
            markdownContent = """
                # Regex Anchors
                Anchors do not match any character on their own. Instead, they assert a position within the context stream.
                
                ### Common Anchors:
                * `^` - **Start of string**: Asserts position at the beginning of the entire query input. If `m` (multiline) flag is active, also matches after any newline `\n`.
                * `${'$'}` - **End of string**: Asserts position at the end of the query input. Under multiline `m` flag, also matches right before any newline.
                * `\b` - **Word boundary**: Asserts a boundary between a word character (`\w`) and a non-word character (`\W`), or vice-versa. Great for isolating complete words (e.g., `\bcat\b` meets `cat` but ignores `catalog`).
                * `\B` - **Non-word boundary**: Asserts position *inside* a word cluster (not adjacent to words spacing).
            """.trimIndent(),
            tags = listOf("regex", "anchors", "start", "end", "boundary", "word")
        ),
        DocPage(
            id = "regex_quantifiers",
            bundleId = "regex_cheatsheet",
            title = "Quantifiers & Greediness",
            markdownContent = """
                # Quantifiers
                Quantifiers specify how many times a character, group, or class should repeat.
                
                ### Core Quantifiers:
                * `*` - **0 or more times**: Matches `{0,}`.
                * `+` - **1 or more times**: Matches `{1,}`.
                * `?` - **0 or 1 time**: Matches `{0,1}`. Makes token optional.
                * `{n}` - **Exact count**: Matches exactly `n` occurrences.
                * `{min,}` - **Minimum count**: Matches at least `min` occurrences.
                * `{min,max}` - **Range**: Matches between `min` and `max` occurrences.
                
                ### Greedy vs Lazy matchers:
                By default, quantifiers are **greedy** — they consume the largest substring matching the pattern. 
                Append a `?` to make them **lazy** (consuming the smallest valid subset):
                * `*?` - Match 0 or more (lazy).
                * `+?` - Match 1 or more (lazy).
                * **Example**: Given `"<div>text</div>"`, matching `<.*>` returns `"<div>text</div>"` (greedy), while matching `<.*?>` returns `"<div>"` (lazy).
            """.trimIndent(),
            tags = listOf("regex", "quantifiers", "greedy", "lazy", "star", "plus")
        ),
        DocPage(
            id = "regex_character_classes",
            bundleId = "regex_cheatsheet",
            title = "Character Groups & Shorthands",
            markdownContent = """
                # Character Classes
                A character class matches any character from a specific bracket set.
                
                ### Basic Classes:
                * `[abc]` - Match any single character inside the set: `a`, `b`, or `c`.
                * `[^abc]` - **Negated Set**: Match any character *not* in bracket set.
                * `[a-z]` - Match characters in range `a` through `z`.
                * `[A-Z]` - Match capital letters.
                * `[0-9]` - Match any numerical digit.
                
                ### Shorthand Sets:
                * `.` - Matches **any character** except newline characters `\n` (unless `s` dotAll flag is active).
                * `\d` - Matches any digit. Equivalent to `[0-9]`.
                * `\D` - Non-digit. Matches anything except digit numerals.
                * `\w` - Word character. Matches alphanumeric strings plus underscore `_`.
                * `\W` - Non-word character.
                * `\s` - Whitespace character (space, tabs, carriage returns).
                * `\S` - Anything except whitespaces.
            """.trimIndent(),
            tags = listOf("regex", "characters", "letters", "digits", "shorthand", "sets")
        ),
        DocPage(
            id = "regex_groups",
            bundleId = "regex_cheatsheet",
            title = "Capture Groups & Assertions",
            markdownContent = """
                # Groups & Lookarounds
                Groups bundle multiple tokens to apply repetitions or extract matched subsets.
                
                ### Groups syntax:
                * `(...)` - **Capture Group**: Groups tokens and records matched string index. Matches are retrievable via group array.
                * `(?:...)` - **Non-capturing Group**: Groups tokens to apply quantifiers, but avoids allocating memory for result arrays.
                * `(?<name>...)` - **Named Capture Group**: Attaches a custom logical name to the output group.
                
                ### Lookarounds (Zero-width Assertions):
                * `(?=...)` - **Positive Lookahead**: Asserts that matching token *is followed* by lookahead expression.
                * `(?!...)` - **Negative Lookahead**: Asserts that matching token *is not followed*.
                * `(?<=...)` - **Positive Lookbehind**: Asserts preceding string match.
                * `(?<!...)` - **Negative Lookbehind**.
            """.trimIndent(),
            tags = listOf("regex", "groups", "capture", "lookahead", "lookbehind")
        ),
        DocPage(
            id = "regex_flags_page",
            bundleId = "regex_cheatsheet",
            title = "Active Engine Flags",
            markdownContent = """
                # Match Engine Flags
                Flags modify the behavior of regular expression calculations.
                
                ### Standard Regex Flags:
                * **`g` (Global)**: Matches all valid strings rather than stopping after finding the very first one.
                * **`i` (Case Insensitive)**: Ignores case differences. `[a-z]` will match caps `[A-Z]`.
                * **`m` (Multiline)**: Causes anchors `^` and `${'$'}` to align and validate against individual lines (`\n`) rather than the absolute start/end of the full multi-line block.
                * **`s` (DotAll)**: Causes dot `.` character to successfully match newlines `\n` too.
                * **`u` (Unicode)**: Evaluates characters as Unicode code points (allowing emojis and multi-byte text matching).
            """.trimIndent(),
            tags = listOf("regex", "flags", "global", "ignorecase", "multiline", "dotall")
        ),

        // === MARKDOWN GUIDE ===
        DocPage(
            id = "md_headers",
            bundleId = "markdown_guide",
            title = "Headings, Emphasis & Layouts",
            markdownContent = """
                # Markdown Headers & Typography
                Markdown is a popular plain-text syntax that converts straightforward text documents into formatted components.
                
                ### 1. Headers `#`
                Headers are created by prefixing your line with a hash symbol `#`. The number of hashes determines the size of the header:
                * `# Header 1` (Main Display Title)
                * `## Header 2` (Standard Section Title)
                * `### Header 3` (Sub-section Title)
                * `#### Header 4` (Paragraph Anchor)
                
                ### 2. Text Emphasis Style
                Add italics, bold, or strikethrough using asterisks:
                * *Italics*: Wrap text with single asterisks `*italics*` or `_italics_`.
                * **Bold**: Wrap text with dual asterisks `**bold**` or `__bold__`.
                * ***Bold-Italics***: Wrap text with three asterisks `***combined***`.
                * ~~Strikethrough~~: Wrap text in double wave tildes `~~strikethrough~~`.
            """.trimIndent(),
            tags = listOf("markdown", "headers", "bold", "italics", "styles", "text")
        ),
        DocPage(
            id = "md_lists",
            bundleId = "markdown_guide",
            title = "Lists & Blockquotes formatting",
            markdownContent = """
                # Lists & Quotes
                Markdown easily handles both ordered and unordered bullet listings.
                
                ### 1. Unordered Lists
                Use an asterisk `*`, minus sign `-`, or plus sign `+` followed by a space:
                * Element A
                * Element B
                  * Sub-element B2 (indent with 2 or 4 spaces)
                
                ### 2. Ordered Lists
                Use simple digits followed by a dot and a space:
                1. Initial step
                2. Second step
                3. Concluding action
                
                ### 3. Blockquotes `>`
                Represent nested callouts by prefixing text lines with `>`:
                > "Precision, not complexity. High contrast typography always beats generic AI slop styles."
            """.trimIndent(),
            tags = listOf("markdown", "lists", "ordered", "bullets", "quotes", "blockquote")
        ),
        DocPage(
            id = "md_links_images",
            bundleId = "markdown_guide",
            title = "Hyperlinks & Image cards",
            markdownContent = """
                # Links & Interactive References
                Align hyperlinks and images cleanly.
                
                ### 1. Hyperlinks
                Wrap clickable label words in square brackets, immediately followed by the target URL in parentheses:
                `[DevPocket Build](https://ai.studio/build)`
                
                Output: [DevPocket Build](https://ai.studio/build)
                
                ### 2. On-Device Images
                Prefix the hyperlink structure with an exclamation mark `!`:
                `![Logo Icon](https://example.com/logo.png)`
                
                The alt-text nested within `[...]` serves as an accessibility content description for screen readers.
            """.trimIndent(),
            tags = listOf("markdown", "links", "hyperlinks", "images", "urls")
        ),
        DocPage(
            id = "md_code_blocks",
            bundleId = "markdown_guide",
            title = "Code Backticks & Monospace",
            markdownContent = """
                # Monospace & Code Fields
                Represent computer programs or variable identifiers using surrounding backticks:
                
                ### 1. Inline Code
                Enclose variables or short commands with a single backtick `` ` ``. For instance, `` `val x = 10` `` renders as a highlighted monospace word: `val x = 10`.
                
                ### 2. Multi-line Code blocks
                Wrap multiple lines inside triple backticks `` ``` ``. You can appending the target language (e.g. `kotlin` or `json`) after the starting backticks to trigger syntactic highlights:
                
                ```kotlin
                fun main() {
                    println("DevPocket Sandbox")
                }
                ```
            """.trimIndent(),
            tags = listOf("markdown", "code", "monospace", "backticks", "syntax", "highlight")
        ),

        // === BASH / SHELL CHEATSHEET ===
        DocPage(
            id = "bash_files",
            bundleId = "bash_commands",
            title = "File & Navigation Commands",
            markdownContent = """
                # Unix Files & Directory Navigation
                These core commands manage Unix directory structures, folders, and simple files.
                
                ### Essential Navigators:
                * `pwd`: **Print Working Directory**: Prints the absolute path of the current terminal folder.
                * `ls`: **List Directory**: Lists all contents. Use `ls -la` to display permissions, file sizes, and hidden files (starting with `.`).
                * `cd <dir>`: **Change Directory**: Moves session to path folder.
                  * `cd ..` moves back up to the parent directory.
                  * `cd ~` returns home.
                
                ### File Modifiers:
                * `mkdir <name>`: Create empty folder directory.
                * `touch <file>`: Create simple empty file or update timestamps.
                * `cp <src> <dest>`: Copy file to a destination path. Use `cp -r` to copy folders recursively.
                * `mv <src> <dest>`: Move or rename files/folders.
                * `rm <file>`: Delete file. Use `rm -rf <dir>` to force erase entire folders recursively (be extremely careful!).
            """.trimIndent(),
            tags = listOf("shell", "bash", "files", "ls", "cd", "rm", "mkdir", "commands")
        ),
        DocPage(
            id = "bash_search",
            bundleId = "bash_commands",
            title = "Text Search, Pipes & Filtering",
            markdownContent = """
                # Streams Redirection, Grep & Pipelines
                Manage and route outputs between separate utilities.
                
                ### Stream Redirection Operators:
                * `>`: **Overwrite output**: Writes standard output of command to target file, erasing previous contents.
                  * `echo "hello" > log.txt`
                * `>>`: **Append output**: Appends stream to end of file without wiping it.
                * `|`: **The Unix Pipe**: Feeds stdout of left command as stdin to the right command.
                
                ### Search Filter with Grep:
                Search for specific text lines in file systems:
                * `grep "pattern" index.html`
                * `grep -rI "BuildConfig" ./app`
                  * `-r` does recursive directories search.
                  * `-I` skips matching inside binary blobs.
            """.trimIndent(),
            tags = listOf("shell", "bash", "grep", "pipes", "redirect", "stream", "filter")
        ),
        DocPage(
            id = "bash_diagnostics",
            bundleId = "bash_commands",
            title = "Diagnostics, Tasks & System logs",
            markdownContent = """
                # System Diagnostics & Process control
                Diagnose device memory, track running threads, and manage operations.
                
                ### Process Utilities:
                * `ps`: Standard process snapshot. Use `ps aux` to trace every running program in the system.
                * `top` / `htop`: Live interactive resources visual manager (CPU, RAM utilization).
                * `kill <PID>`: Terminate process thread ID. Use `kill -9 <PID>` to kill immediately.
                * `df -h`: Report total free disk storage space dynamically on terminal.
                * `free -m`: Displays RAM physical records in megabytes.
                
                ### File Permissions:
                * `chmod +x run.sh`: Makes custom script executable.
                * `chmod 755 file.csv`: Standard user rwx, global rx permissions.
            """.trimIndent(),
            tags = listOf("shell", "bash", "diagnostics", "permissions", "kill", "ps")
        ),
        DocPage(
            id = "bash_script_loop",
            bundleId = "bash_commands",
            title = "Conditionals, Loops & Scripts",
            markdownContent = """
                # Basic Shell Scripting
                Shell scripts let you chain Unix commands, validate outputs, and run programmatic logic directly.
                
                ### 1. Variables assignment:
                ```bash
                NAME="DevPocket"
                echo "App name is ${'$'}NAME"
                ```
                
                ### 2. If Condition:
                Ensure space borders brackets correctly:
                ```bash
                if [ "${'$'}NAME" = "DevPocket" ]; then
                    echo "Match approved"
                else
                    echo "Unknown application"
                fi
                ```
                
                ### 3. For loop iteration:
                ```bash
                for i in 1 2 3; do
                    echo "Index sequence is ${'$'}i"
                done
                ```
            """.trimIndent(),
            tags = listOf("shell", "bash", "script", "loop", "variables", "conditionals")
        ),
        DocPage(
            id = "bash_shortcuts",
            bundleId = "bash_commands",
            title = "Key Terminal Keyboard Shortcuts",
            markdownContent = """
                # Terminal Productivity Shortcuts
                Use these hotkeys inside any standard Unix shell to speed up workspace diagnostics:
                
                * `Ctrl + C`: Forcibly terminate the current running job.
                * `Ctrl + Z`: Suspend task to background. Enter `fg` to resume.
                * `Ctrl + L`: Clear console workspace screen (equivalent to `clear` command).
                * `Ctrl + R`: Search historic list of typed execution commands recursively.
                * `Tab`: Auto-complete file paths and utility names instantly.
                * `Ctrl + A`: Jump starting terminal cursor directly to beginning of line.
                * `Ctrl + E`: Jump cursor straight to end of line.
            """.trimIndent(),
            tags = listOf("shell", "bash", "shortcuts", "hotkeys", "terminal", "clear")
        ),

        // === HTTP STATUS CODES ===
        DocPage(
            id = "http_1xx_2xx",
            bundleId = "http_status",
            title = "1xx Info & 2xx Success Codes",
            markdownContent = """
                # Web Responses: Informational and Successful
                HTTP status codes are returned by any server in response to RESTful API requests.
                
                ### 1xx: Informational Codes (Status Pending)
                * `100 Continue`: Initial headers received, client may continue request body packages.
                * `101 Switching Protocols`: Server satisfies upgrade header instructions (e.g., swapping to WebSocket).
                
                ### 2xx: Success Codes (Actions Complete)
                * `200 OK`: Request succeeded. Returned assets or data payloads exist in response body.
                * `201 Created`: Request succeeded and a brand new on-server database entity card was successfully provisioned.
                * `202 Accepted`: Request queued for background execution (not completed yet).
                * `204 No Content`: Successful action, but response intentionally returns zero structural payload data.
            """.trimIndent(),
            tags = listOf("http", "status", "codes", "200", "success", "ok", "info")
        ),
        DocPage(
            id = "http_3xx",
            bundleId = "http_status",
            title = "3xx Redirections Codes",
            markdownContent = """
                # Redirection codes (Endpoint shifts)
                3xx status codes instruct client browsers or API libraries to request URL redirections.
                
                ### Crucial 3xx status codes:
                * `301 Moved Permanently`: The requested URL has been assigned a brand new static location. Future lookups *must* query new URL.
                * `302 Found` (Temporary redirection): Target URL changed temporarily. Keep checking main URL for future calls.
                * `304 Not Modified`: Cached file resources are still fresh. Client library may read cached local copies safely, saving massive network bandwidth!
            """.trimIndent(),
            tags = listOf("http", "status", "codes", "300", "redirect", "cache", "304")
        ),
        DocPage(
            id = "http_4xx",
            bundleId = "http_status",
            title = "4xx Client Errors References",
            markdownContent = """
                # Client Fault Status (Request issues)
                4xx codes indicate the incoming client request is malformed, lacks permissions, or contains invalid credentials.
                
                ### Core 4xx errors:
                * `400 Bad Request`: Server cannot parse input syntax (e.g., malformed JSON payload).
                * `401 Unauthorized`: Request requires user authentication.
                * `403 Forbidden`: Credentials validated, but user lacks logical permissions to fetch resource.
                * `404 Not Found`: Target url path does not exist on server.
                * `405 Method Not Allowed`: HTTP verb (e.g. `POST`) is not supported on this endpoint.
                * `409 Conflict`: Target update conflicts with server state (e.g., email registered).
                * `429 Too Many Requests`: Client has exceeded query rate limits. Backoff immediately!
            """.trimIndent(),
            tags = listOf("http", "status", "codes", "400", "error", "unauthorized", "notfound")
        ),
        DocPage(
            id = "http_5xx",
            bundleId = "http_status",
            title = "5xx Server Errors References",
            markdownContent = """
                # Server Fault Status (Backend crash)
                5xx codes indicate the server encountered an unexpected error rendering it unable to fulfill request.
                
                ### Core 5xx errors:
                * `500 Internal Server Error`: Standard unexpected crash inside database, runtime, or server thread.
                * `502 Bad Gateway`: Server acting as routing proxy received invalid upstream response.
                * `503 Service Unavailable`: Server is overloaded or offline for maintenance.
                * `504 Gateway Timeout`: upstream proxy timed out while retrieving database package.
            """.trimIndent(),
            tags = listOf("http", "status", "codes", "500", "server", "crash", "timeout")
        ),
        DocPage(
            id = "http_diagnostics_flow",
            bundleId = "http_status",
            title = "How to test HTTP queries offline",
            markdownContent = """
                # Offline API Diagnostic Guidelines
                When checking network codes offline:
                
                ### 1. Mock endpoints
                Construct on-device structures returning expected code scenarios (2xx vs 4xx) to test app logic before launching web services.
                
                ### 2. Cache queries:
                Validate headers like `Cache-Control` can store responses, ensuring database fallback capabilities when airplane mode is on.
            """.trimIndent(),
            tags = listOf("http", "status", "codes", "testing", "mocking", "api", "queries")
        )
    )
}
