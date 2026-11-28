# GasSeva — NFC Passbook Reader

GasSeva is an Android application for reading and verifying customer LPG passbooks using NFC. It is built with Kotlin and Jetpack Compose and currently uses an on-device SQLite database for customer lookup.

> **Project status:** This repository contains the Android application and local customer-data implementation. A Supabase-backed cloud database and offline synchronization layer are roadmap items, not features documented as implemented in the current source.

## Features

- NFC tag discovery and NDEF/tag data reading through Android NFC APIs.
- Parsing of the app's text-based LPG passbook payload.
- Customer lookup and security-code comparison against the local customer record.
- Local customer and NFC-tag storage using SQLiteOpenHelper.
- Manual customer search screen.
- Customer profile with name, customer ID and available photograph.
- Neutral avatar fallback when a customer photo is unavailable.
- Customer registration screen and local tag-binding path.
- Scan states, success/error feedback, animations and sound resources.

## NFC payload format

The current parser expects a text payload in this general format:

```text
LPGPASSBOOK
ID:CUS001
NAME:Customer Name
CODE:XXXXXXXXXX
```

| Field | Meaning |
| --- | --- |
| `LPGPASSBOOK` | Payload marker |
| `ID` | Customer ID used for local lookup |
| `NAME` | Name carried in the tag payload |
| `CODE` | Code checked against the local customer record |

Use the same field labels and line structure when preparing test tags. The Android app reads tags; writing/provisioning tags is performed separately with a compatible NFC-writing tool.

### Security note

A static NDEF text payload can be copied to another compatible NFC tag. Checking an ID and a static code is an application-level verification step, not cryptographic proof that a physical tag is genuine. Do not treat this flow as clone-proof authentication.

## Current data storage

The active database helper uses a local SQLite database named `gasseva_nfc.db`, with these tables:

- `customers`: customer ID, customer name, photo path and unique NFC/security code.
- `nfc_tags`: tag value mapped to a customer ID.

The database helper currently seeds four sample customer records (CUS001–CUS004). Customer photo resources for sample profiles are also bundled in the Android resources.

**Important:** Customer-specific seed data, security codes and bundled photographs are currently part of the app implementation/resources. Do not distribute a production APK with real customer information embedded in it. For production, move authoritative customer records and photographs to a secured backend and keep only the minimum necessary, access-controlled local cache for offline operation.

The active customer database helper is `SQLiteOpenHelper`. Room runtime dependencies and Room annotations are also present, but this README does not imply that the active customer database has been migrated to Room.

## Offline operation and planned cloud synchronization

The local database allows lookups for records already stored on the device without a network request. That is not the same as a completed cloud synchronization solution.

A planned offline-first architecture may use:

1. **Supabase PostgreSQL** as the central source of customer records.
2. **Supabase Storage** for customer photographs.
3. **Local SQLite** as the on-device cache for assigned customers and offline work.
4. **A sync queue** for records created while disconnected.
5. **Android WorkManager** to retry synchronization when network connectivity returns.

These are roadmap items, not claims that Supabase integration or background synchronization is already implemented.

## Technology stack

- Kotlin and Android SDK
- Jetpack Compose and Material 3
- Android NFC APIs
- SQLite / `SQLiteOpenHelper`
- Coil for image loading
- AndroidX Lifecycle and ViewModel
- Gradle Kotlin DSL
- JUnit and AndroidX instrumentation/UI test dependencies

The Android module currently declares `minSdk = 26`, `targetSdk = 37`, and application ID `com.example.nfclpgreader`.

## Project structure

```text
app/src/main/
├── AndroidManifest.xml
├── java/com/example/nfclpgreader/
│   ├── MainActivity.kt
│   ├── data/
│   │   ├── local/          # Local database and entities
│   │   ├── model/          # Customer models
│   │   └── repository/     # Customer repository implementations
│   ├── identity/           # Creator identity/licensing-related code
│   ├── nfc/                # NFC handling, parsing and code utilities
│   ├── ui/
│   │   ├── components/     # Reusable UI and scan feedback
│   │   ├── screens/        # Home, scan, search and profile screens
│   │   ├── theme/          # Compose theme
│   │   └── viewmodel/      # App state and navigation logic
│   └── util/               # Utilities
└── res/
    ├── drawable/           # App artwork and current sample photos
    ├── raw/                # Scan sound effects
    └── values/             # Strings, colors and themes
```

Tests are under `app/src/test` and `app/src/androidTest`.

## Requirements

- Android Studio with a compatible Android Gradle Plugin, Gradle and JDK setup.
- Android device with NFC for physical tag testing. The manifest marks NFC hardware as not required, so installation is possible on devices without NFC, but scanning will not be available.
- Compatible NFC tags containing the expected NDEF text payload.

## Build and run

1. Clone the repository:

   ```bash
   git clone https://github.com/CMS105/NFC-implementation.git
   cd NFC-implementation
   ```

2. Open the project in Android Studio and allow Gradle sync to complete.
3. Connect an Android device or start an emulator.
4. For physical NFC testing, use an NFC-enabled Android device and enable NFC in system settings.
5. Select the `app` run configuration and run the application.

Build a debug APK from the project root:

**Windows**
```powershell
.\gradlew.bat assembleDebug
```

**macOS / Linux**
```bash
./gradlew assembleDebug
```

The debug APK is normally generated at `app/build/outputs/apk/debug/app-debug.apk`.

Run unit tests:

**Windows**
```powershell
.\gradlew.bat test
```

**macOS / Linux**
```bash
./gradlew test
```

Run Android instrumented tests on a connected device/emulator:

```bash
./gradlew connectedAndroidTest
```

## Testing checklist

- NFC enabled and disabled states.
- Reading a supported NDEF text tag.
- Valid customer ID and matching code.
- Unknown customer ID.
- Missing, malformed or incorrect code.
- Unsupported or unreadable tags.
- Manual customer search.
- Missing customer photo fallback.
- Registration and tag binding.
- Physical NFC behaviour on target Android devices.

An emulator simulation is not a substitute for testing with the actual tag and phone hardware.

## Roadmap

- Move real customer records and customer photographs out of the APK.
- Create secured Supabase tables and private photo storage.
- Add server-side verification for online operation.
- Implement an offline-first local customer cache and safe offline verification strategy.
- Add a durable sync queue and retry/conflict handling for offline-created records.
- Add role-based access and audit logging before production deployment.
- Remove demo seed data from production builds.

## Repository

Source code: https://github.com/CMS105/NFC-implementation

## Maintainer

**M S Chirag**  
GitHub: [@CMS105](https://github.com/CMS105)

---

This README describes the repository structure and implementation observed in the source. Cloud integration, production security hardening and physical-device validation should be considered complete only after implementation and testing.