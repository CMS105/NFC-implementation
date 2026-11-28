# GasSeva — LPG Passbook Terminal (Android Application)

GasSeva is a native Android application designed for LPG distributors and delivery personnel to identify customer passbooks and verify customer records using 13.56 MHz NTAG213 NFC smart passbooks and an offline SQLite local database.

---

## 🌟 Main Features

- **Offline Local SQLite Database:** Complete offline functionality backed by `AppDatabaseHelper` extending `SQLiteOpenHelper`.
- **NTAG213 NFC Passbook Reading:** Parses NDEF text records over 13.56 MHz ISO 14443-A links without platform popup interferences.
- **NFC Security-Code Comparison:** Compares the code read from the NFC payload with the code stored for the matching Customer ID using an exact, case-sensitive string comparison. This is a data check, not cryptographic tag authentication.
- **Manual Customer Lookup:** Allows staff to search customer records by entering the Customer ID (e.g. `CUS001`). Manual lookup does not search by the 10-character security code.
- **Customer Registration & Persistent Photo Storage:** Full customer registration flow supporting Camera & Gallery photo capture stored locally at `context.filesDir/customer_photos/`.
- **Project Attribution:** The application includes creator and project attribution information in its native source code and About interface.

---

## 🛠️ Technology Stack & Architecture

- **Language:** Kotlin 2.2.10
- **UI Framework:** Jetpack Compose & Material 3 (Midnight Obsidian, Glassy Black & Brushed Champagne Gold Theme)
- **Architecture:** Model-View-ViewModel (MVVM) with Kotlin StateFlow & Coroutines
- **Database:** Local SQLite (`SQLiteOpenHelper` with parameterized queries)
- **NFC Engine:** Android NFC API (`NfcAdapter.enableReaderMode` & `enableForegroundDispatch`)
- **Min SDK:** 26 (Android 8.0) | **Target SDK:** 37
- **Gradle & AGP:** Gradle 9.6.0 & Android Gradle Plugin 9.5.1

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
│   ├── CreatorManifestVerifier.kt        # Creator metadata consistency checks
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

### 1. Manual Customer ID Lookup

The manual search screen accepts a Customer ID such as `CUS001`. It does not provide a security-code search.

```text
Manual Input (Customer ID: CUS001)
       |
       v
Database Lookup (customerId = 'CUS001')
       |
       +--[Found]----> Display Customer Profile
       |
       +--[Not Found]-> Display "Customer Not Found"
```

### 2. NFC Passbook Verification

The NFC workflow extracts the Customer ID and security code from the NDEF payload, retrieves the customer record by Customer ID, and compares the tag code with the stored code.

```text
Tap NTAG213 Tag
       |
       v
Parse NDEF Payload (Customer ID and CODE)
       |
       v
Retrieve Customer by Customer ID
       |
       v
Compare Tag CODE with stored uniqueNfcCode
       |
       +--[Exact, case-sensitive match]--> Display Customer Profile
       |
       +--[Missing or different code]----> Verification Failed
```

---

## 🚀 Build & Installation Instructions

### Build Debug APK
```bash
./gradlew app:assembleDebug
```
The output APK is generated at `app/build/outputs/apk/debug/app-debug.apk`. The repository does not currently define a build step that copies this APK to the project root.

### Run Unit Tests
```bash
./gradlew clean test
```

---

## 🔒 Security Architecture & Known Limitations

- **Local Data Storage:** Customer records are stored in an on-device SQLite database through `SQLiteOpenHelper`. Database queries use selection arguments for lookups.
- **NFC Code Comparison:** The app performs an exact, case-sensitive comparison between the security code parsed from the NFC payload and the code stored for the identified customer. This comparison does not provide cryptographic authentication.
- **Creator Metadata Check:** `CreatorManifestVerifier` computes a SHA-256 digest but only checks whether the computed value is non-empty; it does not compare that digest with the declared expected value. It is not a digital-signature or tamper-detection mechanism.
- **License Prototype:** `LicenseManager` is configured for development mode. Its activation-code check only tests for the `GASSEVA-` prefix and is not production-grade license validation.
- **Static NDEF Limitation:** NTAG213 tags store static NDEF data that can be copied. Matching a stored code helps detect mismatched records but does not establish that a physical tag is genuine. Stronger anti-cloning protection requires compatible tags and a cryptographic authentication design, such as NTAG 424 DNA.
- **Offline Operation:** Customer lookup and NFC comparison use the local database; the README does not describe a cloud synchronization service.

---

## Planned Enhancement

The current manual lookup workflow uses the Customer ID. A planned enhancement is to replace this input with the customer's **10-character security code**, allowing staff to retrieve the corresponding customer record using that code instead.

This is a planned change and is not part of the current implementation. The README will be updated again when the feature is implemented and verified.

## Project Attribution

**Concept and Development:** M S Chirag  
**Creator ID:** `CMS105`

[LinkedIn — M S Chirag](https://www.linkedin.com/in/m-s-chirag-3466a4383/)

## Team Members

- **Digshu** — [LinkedIn Profile](https://www.linkedin.com/in/digshu/)
- **Isiri**

## Connect
