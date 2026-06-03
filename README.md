# FlipFinance

FlipFinance is a production-grade **Android mobile application** built to demonstrate modern software engineering principles, scalable architecture, and robust financial tracking. It leverages **Jetpack Compose** for a reactive UI and **Firebase** for cloud-based identity management.

---

## Purpose of the Application

FlipFinance was conceptualized and developed to address the real-world complexities of personal wealth tracking. The primary goal is to close the gap between secure cloud multi-device sync and complete offline capability. 

By serving as a transparent ledger, FlipFinance empowers users to capture transaction details, index physical expense documentation (receipts) on the move, and parse through personal financial health through multi-variable background data pipelines. It scales seamlessly from simple cash ledger logging to multi-currency budget management across distributed layers.

---

## Design Considerations

The architecture of FlipFinance is shaped by deliberate engineering and product lifecycle considerations:

* **State-Driven Unidirectional Data Flow (UDF):** Every element relies strictly on an immutable state captured in background processing pipelines. This guarantees that explicit actions (like applying a date boundary or searching) scale predictably without race conditions or memory leaks.
* **Infrastructure Fault Tolerance:** The app implements a decoupled data boundary pattern. Transactions stream asynchronously over a remote cloud mesh, whereas operational preferences and criteria are anchored locally via disk storage. This balances structural continuity with instantaneous offline loading.
* **Resource and Core Performance Constancy:** Multi-variable dataset aggregation, categorization, and cross-month trend processing are explicitly extracted into non-blocking background workers. This guarantees zero frame drops on the main thread and enforces a stable 60FPS drawing cycle during rendering.
* **Encapsulated Component Reusability:** Every visible surface component is constructed as a decoupled, isolated widget layer adhering to rigid styling boundaries. This design keeps layouts maintainable and simple to iterate upon.

---

## Features

### User Authentication & Identity
- **Secure Onboarding**: Login and Registration flows with real-time input validation.
- **Identity Management**: Powered by **Firebase Authentication** for secure email/password sign-in.
- **Self-Service Password Recovery**: Integrated "Forgot Password" flow utilizing Firebase's secure email reset system.
- **First-Launch Experience**: A high-fidelity, 3-screen interactive onboarding pager designed to introduce core value propositions.

### Global Settings & Personalization
- **Multi-Currency Support**: A centralized preference system allowing users to toggle between international currency symbols (ZAR, USD, EUR, GBP). This state is injected globally, ensuring every financial string in the app updates reactively.
- **Dynamic Greeting System**: Implements time-aware logic to provide contextual greetings (Morning/Afternoon/Evening) paired with adaptive iconography.
- **Persistence-Driven Dark Mode**: Integrates Jetpack DataStore with the Material 3 ColorScheme to provide a persistent Dark Theme that respects user choice across application restarts.

### Transaction Management & Analytics
- **Asynchronous Cloud Source**: Full CRUD operations streams directly from **Firebase Realtime Database**, facilitating cloud synchronization across different device targets.
- **Multi-Cloud Hybrid Storage**: Transaction metadata is stored locally, while physical receipt images are securely offloaded to **Supabase Storage**.
- **Contextual Filtering**: A reactive filter system allowing users to toggle between Income/Expense types and specific categories (Food, Transport, etc.).
- **Reactive Financial Summary**: A dynamic Finance Summary Card that provides real-time totals for income and expenses based on the active filter and search criteria.
- **Real-Time Search**: High-performance transaction search implementation covering titles and descriptions using Kotlin Flow.
- **Visualisation**: Grouped transaction history with sticky date headers and dynamic color-coding for financial health tracking.
- **Dynamic Category Allocation**: "+ Add" interactive pill injection inside filter states for provisioning runtime, user-defined custom categories seamlessly.
- **Gesture-Driven Deletion Flow**: Advanced UX supporting a "Long-Press to Edit Mode" on transaction filter elements, dynamically sliding out quick-removal controls.

### Input Validation & UX
- **Context-Aware Error Handling**: Field-level validation that guides users with specific messages (e.g., "Invalid email format" or "Password too short").
- **Reactive UI**: Error states clear automatically as the user begins typing, providing immediate positive feedback.
- **Server-Side Feedback**: Technical Firebase exceptions are mapped to human-readable strings for a smoother user experience.

### Navigation & Architecture
- **Multi-Screen Navigation**: Implemented using **Jetpack Compose Navigation** with a centralized `NavGraph`.
- **Bottom Navigation System**: A modern, Material 3-compliant bottom bar allowing seamless transitions between Expenses, Goals, and Profile.
- **Conditional UI**: The bottom navigation is context-aware, hiding itself during the authentication flow and only appearing once a user is verified.

---

## Technical Architecture

The application is built following **Clean Architecture** and **MVVM (Model-View-ViewModel)** patterns to ensure the codebase remains maintainable and testable as it scales.

### Layered Separation
- **UI Layer**: Built 100% in **Jetpack Compose**. Uses **Hilt** for ViewModel injection and captures UI state using `StateFlow`.
- **Domain Layer**: Contains the core business logic, including the `AuthRepository` interface and use-case-specific models like the `User` entity.
- **Data Layer**: Handles data sourcing. Currently implements `FirebaseAuthRepository` to communicate with the Firebase SDK.
- **Preferences Layer** Uses `UserPreferences` for local Offline settings.

---

## UI / UX Design

### Modern Design System
- **Soft Palette**: A custom-defined color system supporting both **Light and Dark Themes**.
- **Rounded Aesthetics**: Extensive use of the **Material 3 Shape system**, featuring rounded corners (up to 24.dp) for components to create a friendly, modern feel.
- **Dynamic Asset Management**: Integration of **Coil** for asynchronous receipt image loading from Supabase public URLs.

---

## HCI & Usability Principles
The development of FlipFinance is guided by Shneiderman’s Eight Golden Rules of Interface Design to ensure superior User Experience (UX):

### Modern Design System
- **Strive for Consistency**: Universal application of currency symbols, padding constants, and iconographic metaphors across the Home, Transactions, and Settings modules.
- **Offer Informative Feedback**: The Budget Progress Card provides immediate visual status of monthly spending. The progress bar utilizes semantic coloring, transitioning to an **Error** state (Red) once the user exceeds 80% of their defined budget.
- **Enable Frequent Users to Use Shortcuts**: Navigation is optimized through a persistent Bottom Bar and a prominent Floating Action Button (FAB) for the most frequent task: adding a transaction.
- **Reduce Short-term Memory Load**: Adheres to the **Recognition** over **Recall** principle by using distinct, category-specific icons and color-coding income (Green) vs. expenses (Red), allowing users to process financial health at a glance.

---

## Tech Stack

| Category | Technology |
| :--- | :--- |
| **Language** | Kotlin |
| **UI Framework** | Jetpack Compose (Material 3) |
| **Dependency Injection** | Hilt (Dagger) |
| **Authentication** | Firebase Auth |
| **Local Database** | Room Persistence Library (SQLite) |
| **Cloud Storage** | Supabase Storage (Receipt Uploads) |
| **Cloud Database** | Firebase Realtime Database (RTDB) |
| **Persistence** | Jetpack DataStore (Preferences) |
| **Architecture** | Clean Architecture + MVVM |
| **Asynchronous Logic** | Kotlin Coroutines & Flow |
| **Navigation** | Compose Navigation Component |

---

## Version Control & CI/CD Cloud Automation

FlipFinance uses standard industry practices for distributed team workflows, utilizing **GitHub** as the source control engine alongside continuous automation via **GitHub Actions**.

### Git Branching Strategy
The project follows a strict branch-protection model to safeguard main-line release integrity:
* `main`: Holds production-stable code. Direct pushes are restricted.
* `development`: The integration node for staging features.
* `feature/*`: Granular, task-isolated working branches (e.g., `feature/date-range-filter`). Merges into development require an approved Pull Request (PR).

### GitHub Actions Automation Pipeline
Every time a developer opens a Pull Request or pushes code to the repository, an automated CI script triggers via a headless Ubuntu pipeline (`.github/workflows/android-ci.yml`). This workflow verifies code safety through the following sequence:

```yaml
# Summary of the automated checks executed on GitHub Actions cloud nodes
name: Android CI Verification Pipeline

on:
  push:
    branches: [ main, development ]
  pull_request:
    branches: [ main, development ]

jobs:
  verify-build:
    runs-on: ubuntu-latest
    steps:
      - name: Checkout Source Code repository
        uses: actions/checkout@v4

      - name: Setup Java Development Kit (JDK 17)
        uses: actions/setup-java@v4
        with:
          distribution: 'zulu'
          java-version: '17'

      - name: Cache Gradle Packages (Speeds up sequential run processes)
        uses: actions/cache@v4
        with:
          path: |
            ~/.gradle/caches
            ~/.gradle/wrapper
          key: ${{ runner.os }}-gradle-${{ hashFiles('**/*.gradle*', '**/gradle-wrapper.properties') }}

      - name: Execute Static Code Analysis & Unit Tests
        run: ./gradlew testDebugUnitTest

      - name: Verify Clean Production Compilations
        run: ./gradlew assembleDebug
```

## Implementation Details

### Authentication Flow
- **AuthViewModel**: Manages authentication states (`isLoading`, `error`, `isAuthenticated`).
- **AuthValidator**: A specialized utility object that enforces business rules for passwords and email formats before network calls are made.
- **AppModule**: Provides Hilt bindings for `FirebaseAuth` and the repository implementation.

### Data Management
- **FlipFinanceDatabase**: A Room-managed singleton ensuring thread-safe access to user transaction history.
- **TransactionViewModel**: Uses `StateFlow` and `SharingStarted.WhileSubscribed` to provide an efficient, lifecycle-aware stream of filtered data to the UI.
- **Dynamic Aggregation**: Leverages Kotlin's sumOf within reactive remember blocks to calculate filtered financial totals without manual list traversals.
- **Supabase Integration**: Implements a `NonCancellable` coroutine context for receipt uploads to ensure data integrity during network transitions.

### Profile & Preference Management
- **Profile Synchronization**: The AuthViewModel reactively fetches user metadata (`First Name`, `Last Name`). from **Firebase Realtime Database** upon successful authentication, ensuring the UI is personalized to the specific user session.
- **Settings State Machine**: Uses a dedicated `SettingsViewModel` to expose a single `uiState` representing the user's localized preferences, reducing the complexity of managing global configurations.
- **Numeric Localization**: Employs `Locale.ENGLISH` formatting for all monetary calculations to ensure decimal precision and consistency across different Android system locales.
- **Profile Photo Management**: Uses Coil's `AsyncImage` composable for successful remote image loading, integrates `Supabase Storage` for profile photo uploads, and stores the returned public URL in `Firebase Realtime Database` under the node of the authenticated user.
- **Credential Management**: Allows authenticated users to update their first name, last name and password using a `Change Credentials` screen, with changes saved to `Firebase Realtime Database` and `Firebase Authentication` respectively, with field level validation and confirmation before saving.

### Navigation Routing
- **MainActivity**: Acts as the single entry point, observing both `currentUser` and `hasCompletedOnboarding` states to determine the UI entry point.
- **Screen**: A sealed class hierarchy defining routes, titles, and icons for type-safe navigation.
- **Persistent Preferences**: Leverages **Jetpack DataStore** to ensure the onboarding experience is only displayed on the first launch or until successfully completed.

### Settings Management
- **SettingsPreference**: Manages dark/light theme user preference, currency and budget limit and notifications.
- **Settings ViewModel**: Logic layer acting as middleman between UI and storage.
- **Settings Page**: Visual screen for settings budget limits, currency icons and theme toggle.

---

## Setup & Installation

1. **Clone the repository.**
2. **Firebase Setup**:
    - Create a project in the [Firebase Console](https://console.firebase.google.com/).
    - Add an Android App with the package name `com.example.flipfinance`.
    - Download the `google-services.json` and place it in the `app/` directory.
    - Enable **Email/Password** authentication in the Firebase Auth settings.
3. **Supabase Setup**:
    - Create a buckets named `RecieptStorage` and `avatars` in your Supabase project.
    - Ensure public access or appropriate RLS policies are set for receipt retrieval.
    - Insert your SUPABASE_URL and SUPABASE_KEY into local.properties and supabase clients respectively. 
4. **Build**:
    - Open the project in **Android Studio (Ladybug or newer)**.
    - Sync Gradle and run the app on an emulator or physical device.

---

## Development Roadmap

- [x] Project Structure & Clean Architecture Setup.
- [x] Design System (Theme, Type, Shapes).
- [x] Adaptive Icon & Branding Implementation.
- [x] Firebase Authentication Integration.
- [x] Persistent Onboarding with Jetpack DataStore.
- [x] Bottom Navigation & Global Routing.
- [x] RoomDB implementation for local expense persistence.
- [x] Supabase integration for receipt image storage.
- [x] Advanced Search and Category Filtering.
- [x] Reactive Finance Summary Card (Income/Expense totals).
- [x] Global Multi-Currency and Dark Mode support.
- [x] Relational Database Schema Normalization (Room DB v2).
- [x] Firebase Realtime Database integration for user custom criteria syncing.
- [x] Custom Category Insertion.
- [x] Gesture-Driven Cascading Category Removal.
- [x] Reactive Date Range Boundary Processing Flow.
- [x] Interactive analytics Graph.
