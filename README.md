# Pratham_chikitse

<div align="center">
  <img src="https://img.icons8.com/color/120/000000/first-aid-kit.png" alt="Pratham Chikitse Logo">
  <h1>Pratham Chikitse (First Aid)</h1>
  <p><strong>A Production-Grade, Offline-First Emergency First-Aid & Triage Application</strong></p>

  [![Kotlin](https://img.shields.io/badge/Kotlin-2.0.0-blue.svg?logo=kotlin)](https://kotlinlang.org)
  [![Android](https://img.shields.io/badge/Android-14.0-3DDC84.svg?logo=android)](https://developer.android.com)
  [![Jetpack Compose](https://img.shields.io/badge/Compose-Material%203-4285F4.svg?logo=android)](https://developer.android.com/jetpack/compose)
  [![License](https://img.shields.io/badge/License-MIT-green.svg)](LICENSE)
  [![Build](https://img.shields.io/badge/Build-Passing-brightgreen.svg)]()
</div>

<br/>

## 📖 Table of Contents
- [Project Overview](#-project-overview)
- [Key Features](#-key-features)
- [Technology Stack](#-technology-stack)
- [Project Architecture](#-project-architecture)
- [Prerequisites](#-prerequisites)
- [Installation & Setup](#-installation--setup)
- [Running the Application](#-running-the-application)
- [Data & Localization](#-data--localization)
- [Troubleshooting](#-troubleshooting)
- [Contributing](#-contributing)
- [License & Support](#-license--support)

---

## 🎯 Project Overview
**Pratham Chikitse** is a comprehensive, life-saving Android application engineered to provide immediate, actionable first-aid guidance. Built entirely offline-first, the app acts as a pocket medical triage assistant, designed to work reliably in remote areas or low-connectivity zones.

By focusing on accessibility, the application includes a Voice-First mode, multi-language support, and an AI-powered triage chatbot to help users quickly identify the appropriate emergency response without needing internet connectivity.

---

## ✨ Key Features
- **🚨 Interactive AI Triage Chatbot**: Parses user input locally and maps it to immediate, high-priority emergency guidelines and hospital recommendations.
- **🌐 Robust Multi-Language Support**: Seamlessly switch between English, Kannada, Hindi, Gujarati, Marathi, and Tamil. The UI, backend data, and AI responses instantly adapt without requiring app restarts.
- **🔊 Voice-First & Text-to-Speech (TTS)**: Built-in TTS engine reads out emergency instructions in the selected regional language, ensuring high accessibility for visually impaired users or chaotic emergency environments.
- **📴 Offline-First Architecture**: All guides, myths & facts, and triage logic are driven by localized JSON assets. No internet connection is ever required to fetch life-saving steps.
- **🏥 Geo-Spacial Hospital Locator**: Calculates nearest hospitals using offline boundary data when location permissions are granted.
- **🆘 SOS Mode**: Floating action button that provides immediate shortcuts to standard emergency numbers (108, 112, 100, 101) and custom emergency contacts.

---

## 🛠 Technology Stack
| Category | Technology / Library |
| :--- | :--- |
| **Language** | Kotlin `2.0.0` |
| **UI Toolkit** | Jetpack Compose (Material 3) |
| **Architecture** | MVVM (Model-View-ViewModel) |
| **Dependency Injection** | Dagger Hilt |
| **Local Persistence** | Preferences DataStore |
| **Asynchronous Programming** | Kotlin Coroutines & StateFlow |
| **Navigation** | Jetpack Navigation Compose |
| **JSON Parsing** | Gson |

---

## 🏗 Project Architecture
The application strictly follows Clean Architecture principles, ensuring a complete separation of concerns between UI components, state management, and data sources.

```text
app/src/main/
├── assets/                  # Offline JSON Datasets (emergencies, hospitals, etc.)
├── java/com/example/health/
│   ├── data/                # Repositories & Data Models
│   │   ├── local/           # DataStore and JsonDataSource logic
│   │   ├── model/           # Kotlin Data Classes (DTOs)
│   │   └── repository/      # Abstraction layers for data handling
│   ├── navigation/          # Compose NavGraph and Routes
│   ├── ui/                  # UI Layer (Screens & ViewModels)
│   │   ├── assistant/       # Triage Chatbot UI & Logic
│   │   ├── emergency/       # Emergency Guides & Details
│   │   ├── home/            # Dashboard & Quick Actions
│   │   ├── hospital/        # Offline Hospital Directory
│   │   ├── learning/        # Educational Modules
│   │   ├── onboarding/      # First-launch flow & Permissions
│   │   └── settings/        # Preferences & App configuration
│   └── util/                # Helpers (TTSManager, LocaleHelper)
└── res/                     # Localized string values (values-kn, values-hi, etc.)
```

---

## 📋 Prerequisites
Before you begin, ensure your development environment meets the following requirements:
- **Android Studio**: Koala Feature Drop (2024.1.2) or newer.
- **Java Development Kit (JDK)**: JDK 17 (Required for modern Gradle & Kotlin 2.0+).
- **Android SDK**: API Level 34 (Android 14) installed via SDK Manager.
- **Minimum Target Device**: Android 8.0 (API Level 26) or newer.

---

## 🚀 Installation & Setup

### Step 1: Clone the Repository
Open your terminal or command prompt and run:
```bash
git clone https://github.com/AlphaDoc1/PrathamChikitse.git
cd PrathamChikitse
```

### Step 2: Open in Android Studio
1. Open **Android Studio**.
2. Click on **File > Open** (or **Open** from the welcome screen).
3. Select the `PrathamChikitse` root directory.
4. Allow Gradle a few moments to sync the project dependencies. 

### Step 3: Zero-Configuration Run
This project is built to be **plug-and-play**. Because it relies entirely on local JSON assets instead of complex backend databases:
- 🚫 **No API Keys Required**: You do not need to hunt for secrets or register for APIs.
- 🚫 **No Manual local.properties**: Android Studio will automatically generate your `local.properties` file with your specific SDK path during the first sync.
- ✅ **JDK Setup**: Just ensure your Java environment is set correctly. Go to `File > Settings` (or `Android Studio > Preferences` on Mac) > `Build, Execution, Deployment > Build Tools > Gradle` and ensure the **Gradle JDK** is set to `jbr-17` or your installed JDK 17.

---

## 📱 Running the Application

### Using an Emulator
1. Open the **Device Manager** in Android Studio.
2. Create a new Virtual Device (e.g., Pixel 7) with System Image API 34.
3. Click the **Run** button (▶️) in the top toolbar.

### Using a Physical Device
1. Enable **Developer Options** on your Android device (Tap "Build Number" 7 times in Settings).
2. Enable **USB Debugging**.
3. Connect your device via USB (or Wireless Debugging).
4. Select your device from the deployment dropdown in Android Studio and click **Run**.

---

## 🌍 Data & Localization
All core application data is stored in localized JSON files within the `assets/` folder. This allows non-developers to easily contribute new emergency content or language translations.

### Adding a New Language
1. Add the language code to `LocaleHelper.kt` and the Settings UI options.
2. Create standard Android strings: `res/values-{lang}/strings.xml`.
3. Create localized JSON assets: e.g., `emergencies_{lang}.json`, `hospitals_{lang}.json`.
4. The internal `JsonDataSource` engine will automatically detect the active locale and fallback to English if a localized file is missing.

---

## 🔧 Troubleshooting

| Issue | Potential Cause | Solution |
| :--- | :--- | :--- |
| **Gradle Sync Failed (Unsupported class file major version)** | Incorrect JDK version. | Change Gradle JDK to Java 17 in Settings. |
| **Text-to-Speech is silent** | Missing TTS Engine on device. | Ensure Google TTS is installed and updated from the Play Store. The app will gracefully fall back to English if the chosen language pack isn't downloaded. |
| **Double spacing above Top App Bar** | Inset/Edge-to-Edge collision. | Fixed in recent commit. Ensure you've pulled the latest branch utilizing `.consumeWindowInsets()` in the NavGraph. |
| **"Unresolved reference: hiltViewModel"** | Hilt KAPT/KSP issue. | Run `Build > Clean Project`, then `Rebuild Project`. |

---

## 🤝 Contributing
We welcome contributions to make Pratham Chikitse a robust global medical tool!
1. Fork the Project
2. Create your Feature Branch (`git checkout -b feature/AmazingFeature`)
3. Commit your Changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the Branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

*Please ensure your code aligns with standard Kotlin formatting and doesn't introduce breaking changes to the offline JSON parsing engine.*

---

## 📄 License & Support
Distributed under the **MIT License**. See `LICENSE` for more information.

**Contact & Support:**
- Issue Tracker: [GitHub Issues](https://github.com/AlphaDoc1/PrathamChikitse/issues)
- Maintainer: AlphaDoc1 / Development Team
- Design & Architecture: Health Application Foundation
