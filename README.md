# GasSeva — LPG Passbook Terminal (Android Application)

GasSeva is a native Android application designed for LPG distributors and delivery personnel to identify customer passbooks and verify customer records using 13.56 MHz NTAG213 NFC smart passbooks and an offline SQLite local database.

---

## 🌟 Main Features

- **Offline Local SQLite Database:** Complete offline functionality backed by `AppDatabaseHelper` extending `SQLiteOpenHelper`.
- **NTAG213 NFC Passbook Reading:** Parses NDEF text records over 13.56 MHz ISO 14443-A links without platform popup interferences.
- **Strict Cryptographic Security Code Matching:** Enforces exact, case-sensitive matching between the NFC tag security code and the database `uniqueNfcCode`.
- **Manual Security Code Lookup:** Allows authorized staff to search customer records manually by entering their assigned 10-character Security Code (e.g. `YHWP84PENQ`).
- **Customer Registration & Persistent Photo Storage:** Full customer registration flow supporting Camera & Gallery photo capture stored locally at `context.filesDir/customer_photos/`.
- **Permanent Creator Attribution & Integrity:** Native embedded read-only creator identity (`M S Chirag`, `CMS105`, `© 2026 M S Chirag`) with Ed25519 digital signature verification.

---

## 🛠️ Technology Stack & Architecture

- **Language:** Kotlin 2.2.10
- **UI Framework:** Jetpack Compose & Material 3 (Midnight Obsidian, Glassy Black & Brushed Champagne Gold Theme)
- **Architecture:** Model-View-ViewModel (MVVM) with Kotlin StateFlow & Coroutines
- **Database:** Local SQLite (`SQLiteOpenHelper` with parameterized queries)
- **NFC Engine:** Android NFC API (`NfcAdapter.enableReaderMode` & `enableForegroundDispatch`)
- **Min SDK:** 26 (Android 8.0) | **Target SDK:** 37
- **Gradle & AGP:** Gradle 9.6.0 & AGP 9.5.3

---

## 📁 Project Structure

```
app/src/main/java/com/example/nfclpgreader/
├── data/
│   ├── local/
│   │   ├── AppDatabaseHelper.kt          # SQLite Database Helper & Prepopulated Data
│   │   └── entity/CustomerEntity.kt      # Customer Table Schema
│   ├── model/Customer.kt                 # Customer Data Model
│   └── repository/CustomerRepository.kt  # Repository Abstraction & Implementation
├── identity/
│   ├── CreatorIdentity.kt                # Read-Only Native Project Attribution
│   ├── CreatorManifestVerifier.kt        # Ed25519 Cryptographic Manifest Verifier
│   └── LicenseManager.kt                 # Device License Activation Architecture
├── nfc/
│   ├── NfcManager.kt                     # NFC Hardware ReaderMode Lifecycle
│   ├── NfcParser.kt                      # NDEF Payload Text & URI Parser
│   └── UniqueCodeGenerator.kt            # Secure 10-Char Code Generator
└── ui/
    ├── components/                       # CyberRadar, 3D Checkmark Orb, Header, About Dialog
    ├── screens/                          # Home, Scan, Search, Profile, Not Found, Register
    ├── theme/                            # Color, Type, Theme Specifications
    └── viewmodel/MainViewModel.kt        # App Navigation & Verification Logic
```

---

## 📄 NTAG213 Passbook Payload Structure

GasSeva NTAG213 smart passbooks use the following NDEF Text Record payload structure:

```text
LPGPASSBOOK
ID:CUS001
NAME:M S CHIRAG
CODE:YHWP84PENQ
```

- **`LPGPASSBOOK`**: Application identification marker.
- **`ID`**: Customer ID (e.g., `CUS001`).
- **`NAME`**: Customer Name (used for pre-filling registration when unregistered).
- **`CODE`**: 10-character unique Security Code assigned to the customer.

---

## 🔄 Verification & Lookup Workflows

### 1. Manual Security Code Lookup Flow
```
Manual Lookup Input (Security Code: YHWP84PENQ)
       │
       ▼
Database Query (uniqueNfcCode = 'YHWP84PENQ')
       │
       ├──[Found]──► Display Customer Profile
       │
       └──[Not Found]──► Display "Customer Not Found"
```

### 2. NFC Passbook Verification Flow
```
Tap NTAG213 Tag ──► Parse NDEF Payload (Extract ID & CODE)
                          │
                          ▼
             Database Lookup by Customer ID
                          │
                          ▼
            Compare Tag CODE vs. DB uniqueNfcCode
                          │
                          ├──[Exact Match]──► Verified -> Open Profile
                          │
                          └──[Code Mismatch / Missing]──► Verification Failed Error
```

---

## 🚀 Build & Installation Instructions

### Build Debug APK
```bash
./gradlew app:assembleDebug
```
The output APK will be generated at `app/build/outputs/apk/debug/app-debug.apk` and copied to `GAS_NFC_CMS.apk` in the project root directory.

### Run Unit Tests
```bash
./gradlew clean test
```

---

## 🔒 Security Architecture & Known Limitations

- **Parameterized SQLite Queries:** All database lookups use `?` parameter bindings to eliminate SQL injection risks.
- **Privacy Protection:** Customer security codes are strictly hidden from customer-facing profile screens and error logs.
- **Static NDEF Payload Limitation Notice:** NTAG213 tags contain static NDEF payloads. Verification confirms that the code written on the tag matches the database record for that customer ID. True hardware anti-cloning or cryptographic chip authentication requires hardware-level 3DES/AES dynamic counter tags (e.g. NTAG 424 DNA).
