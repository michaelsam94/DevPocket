# DevPocket

Offline-first developer utility for Android: code workspace, formatters, regex playground, JavaScript sandbox, and built-in reference docs. Everything runs on-device with no cloud sync or telemetry.

**Package:** `com.michael.devpocket` · **Min SDK:** 24 · **Target SDK:** 36

## Features

- **Code workspace** — Syntax highlighting for JavaScript, JSON, CSS, HTML, SQL, and Markdown; bracket auto-pairing; adjustable font size; file drawer.
- **Brutal formatter** — Beautify, prettify, or minify JSON, XML, HTML, CSS, and SQL offline.
- **Regex playground** — Test patterns with global, case-insensitive, multiline, and dotAll flags; save match sessions locally.
- **JS & math sandbox** — Run JavaScript and math expressions in a local engine with stdout/stderr; load workspace files into the console.
- **Documentation vault** — Offline reference for JSON, regex, shell/bash, HTML/Markdown, and HTTP status codes.

Data (files, regex sessions, scripts) is stored in a local Room database. No internet connection is required for core features.

## Screenshots

Play Store graphics and listing copy live in [`play-store/`](play-store/). See [`play-store/README.md`](play-store/README.md) for how to regenerate assets.

## Privacy

Privacy policy (Netlify-hosted): [DevPocket-pv](https://github.com/michaelsam94/DevPocket-pv)

## Getting started

**Prerequisites:** [Android Studio](https://developer.android.com/studio) (or JDK 11+ and the Gradle wrapper in this repo)

1. Clone the repository and open the project root in Android Studio.
2. Let Gradle sync finish.
3. Run the **app** configuration on an emulator or device (`./gradlew installDebug` from the command line).

Debug builds use the included `debug.keystore`. For release builds, add `key.properties` at the project root (see your signing setup) or set `KEYSTORE_PATH`, `STORE_PASSWORD`, and `KEY_PASSWORD` environment variables.

## Play Store assets

From the project root:

```bash
bash ~/.cursor/skills/generate-app-assets/scripts/generate-app-icon.sh .
./gradlew generatePlayStoreAssets
bash ~/.cursor/skills/generate-app-assets/scripts/verify-play-store-assets.sh .
```

Release bundle:

```bash
./gradlew bundleRelease
```

## Tech stack

- Kotlin, Jetpack Compose, Material 3
- Room (local persistence)
- Navigation Compose, ViewModel
- Roborazzi + Robolectric (Play Store screenshot tests)

## License

Copyright © Michael Sam. All rights reserved unless otherwise noted in the repository.
