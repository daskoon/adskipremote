# Fuckups - Attempts to resolve issues that failed

## Attempt 1: Missing `android.useAndroidX=true` and `android.enableJetifier=true`
- **Error:** `Execution failed for task ':app:checkDebugAarMetadata'. > Configuration ':app:debugRuntimeClasspath' contains AndroidX dependencies, but the android.useAndroidX property is not enabled`
- **Reason for failure:** The `gradle.properties` file was missing these crucial flags for AndroidX compatibility.

## Attempt 2: Running Gradle from the wrong directory
- **Error:** `Project directory 'C:\Users\me\Documents\Downloads\skip' is not part of the build defined by settings file 'C:\Users\me\settings.gradle'.`
- **Reason for failure:** I was executing the `gradlew.bat` command from the parent directory (`skip`) instead of the `AdSkipRemote` directory, causing Gradle to look for the `settings.gradle` file in the wrong place.

## Attempt 3: Missing `ic_launcher` resource in `app` module
- **Error:** `AAPT: error: resource mipmap/ic_launcher (aka com.whatdoyouwantwired.adskipremote:mipmap/ic_launcher) not found.`
- **Reason for failure:** The `AndroidManifest.xml` in the `app` module referenced a launcher icon that did not exist in the project's resources.

## Attempt 4: Missing `ic_launcher` resource in `wear` module
- **Error:** `AAPT: error: resource mipmap/ic_launcher (aka com.whatdoyouwantwired.adskipremote:mipmap/ic_launcher) not found.`
- **Reason for failure:** Similar to Attempt 3, the `AndroidManifest.xml` in the `wear` module referenced a launcher icon that did not exist.

## Attempt 5: Kotlin/Compose Compiler version mismatch
- **Error:** `This version (1.5.3) of the Compose Compiler requires Kotlin version 1.9.10 but you appear to be using Kotlin version 1.9.20 which is not known to be compatible.`
- **Reason for failure:** The `composeCompiler` version in `libs.versions.toml` was not compatible with the `kotlin` version.

## Attempt 6: Incorrect `wear-protolayout` dependency version
- **Error:** `Could not find androidx.wear:wear-protolayout:1.0.0.`
- **Reason for failure:** The version `1.0.0` for `wear-protolayout` was not available in the Maven repository.

## Attempt 7: Incorrect `wear-protolayout` dependency version (again)
- **Error:** `Could not find androidx.wear:wear-protolayout:1.3.0.`
- **Reason for failure:** Even after updating to `1.3.0`, the library was still not found. This indicates a deeper issue with how the dependencies are being resolved or that the version is still incorrect.

## Attempt 8: `SkipAdTileService.kt` compilation errors (first rewrite)
- **Error:** Numerous `Unresolved reference` and `overrides nothing` errors in `SkipAdTileService.kt`.
- **Reason for failure:** My initial rewrite of `SkipAdTileService.kt` mixed imports from `androidx.wear.tiles` and `androidx.wear.protolayout` incorrectly, leading to API mismatches and compilation failures.

## Attempt 9: `MainActivity.kt` (Wear) `MaterialTheme` properties
- **Error:** My attempt to replace `MaterialTheme.colors.background` with `MaterialTheme.colorScheme.background` failed because the file was already in the desired state.
- **Reason for failure:** I made an assumption about the file's content without re-reading it, leading to an unnecessary and failed `replace` operation.

## Attempt 10: `SkipAdTileService.kt` compilation errors (second rewrite)
- **Error:** Still numerous `Unresolved reference` and `overrides nothing` errors in `SkipAdTileService.kt`.
- **Reason for failure:** Despite trying to use a working example, I still failed to correctly integrate the `androidx.wear.tiles` and `androidx.wear.protolayout` APIs, leading to persistent compilation errors.

## Attempt 11: `SkipAdTileService.kt` compilation errors (third rewrite)
- **Error:** Still numerous `Unresolved reference` and `overrides nothing` errors in `SkipAdTileService.kt`.
- **Reason for failure:** I am still struggling to correctly integrate the `androidx.wear.tiles` and `androidx.wear.protolayout` APIs, leading to persistent compilation errors. I am clearly missing something fundamental about the correct usage of these APIs together.

## Attempt 12: Incorrect `androidx.wear.tiles` and `androidx.wear.protolayout` versions
- **Error:** `Could not find androidx.wear.tiles:tiles-service:1.3.0.`
- **Reason for failure:** My previous research incorrectly identified `1.3.0` as the latest stable version for `androidx.wear.tiles` and `androidx.wear.protolayout`. The actual latest stable version is `1.2.0`.

## Attempt 13: `web_fetch` failure for `mvnrepository.com`
- **Error:** `Error during fallback fetch for https://mvnrepository.com/artifact/androidx.wear.tiles/tiles: Request failed with status code 403 Forbidden`
- **Reason for failure:** Direct access to `mvnrepository.com` is being blocked, preventing me from verifying dependency versions through this method. This indicates a need to rely solely on official AndroidX release notes and local Gradle configuration for version and repository information.
