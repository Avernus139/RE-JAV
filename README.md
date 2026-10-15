# Jav Library — Aniyomi catalogue extension (source)

This extension reads the public GitHub Pages site:

`https://avernus139.github.io/JavLibrary/`

It parses the existing HTML cards in `.movie-grid .movie` and uses:
- `.movie p` as the item title
- `.movie img` as the cover image URL

It is intentionally a catalogue only. It does **not** implement video playback, downloads, or episode lists.

## Included

- `src/all/javlibrary/build.gradle`
- `src/all/javlibrary/src/eu/kanade/tachiyomi/animeextension/all/javlibrary/JavLibrary.kt`

## Build into an APK

This is an extension module, not a standalone Android Studio project. To compile it, add the module to a compatible Aniyomi extensions source repository/fork.

1. Download/clone a compatible extensions source repository. The upstream-style layout uses modules under `src/<language>/<source-name>/`.
2. Copy this ZIP's `src/all/javlibrary` folder into that repository's `src/all/javlibrary`.
3. Register `:src:all:javlibrary` in the repository's `settings.gradle.kts` if its settings do not automatically discover modules.
4. Run the repository's Gradle wrapper, for example:
   `./gradlew :src:all:javlibrary:assembleDebug`
5. Install the resulting APK on the Android device and open it with Aniyomi if the app accepts the repository/fork's extension API.

Extension APIs and Gradle plugin conventions differ across Aniyomi forks and versions, so compilation must be verified against the exact source repository you use. This environment could not compile an APK because it does not have the Android/Gradle extension build environment or network access to fetch it.

## Notes

- The website is requested directly over HTTPS; no browser CORS configuration is needed for native extension HTTP requests.
- The source reads the current static page as one catalogue page. If you add pagination to your website later, the extension will need matching pagination logic.
- Your website's `data-actor` values are not used by this first version.
- Titles with identical text are deduplicated.
