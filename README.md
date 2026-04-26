# FlipFinance

FlipFinance is a production-grade **Android mobile application** built to demonstrate modern software engineering principles, scalable architecture, and robust financial tracking. It leverages **Jetpack Compose** for a reactive UI and **Firebase** for cloud-based identity management.

---

## Features

### User Authentication & Identity
- **Secure Onboarding**: Login and Registration flows with real-time input validation.
- **Identity Management**: Powered by **Firebase Authentication** for secure email/password sign-in.
- **Self-Service Password Recovery**: Integrated "Forgot Password" flow utilizing Firebase's secure email reset system.
- **First-Launch Experience**: A high-fidelity, 3-screen interactive onboarding pager designed to introduce core value propositions.

### Transaction Management & Analytics
- **Local Persistence**: Full CRUD operations powered by **RoomDB SQLite**, ensuring data is accessible offline.
- **Multi-Cloud Hybrid Storage**: Transaction metadata is stored locally, while physical receipt images are securely offloaded to **Supabase Storage**.
- **Contextual Filtering**: A reactive filter system allowing users to toggle between Income/Expense types and specific categories (Food, Transport, etc.).
- **Real-Time Search**: High-performance transaction search implementation covering titles and descriptions using Kotlin Flow.
- **Visualisation**: Grouped transaction history with sticky date headers and dynamic color-coding for financial health tracking.

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

## Tech Stack

| Category | Technology |
| :--- | :--- |
| **Language** | Kotlin |
| **UI Framework** | Jetpack Compose (Material 3) |
| **Dependency Injection** | Hilt (Dagger) |
| **Authentication** | Firebase Auth |
| **Local Database** | Room Persistence Library (SQLite) |
| **Cloud Storage** | Supabase Storage (Receipt Uploads) |
| **Persistence** | Jetpack DataStore (Preferences) |
| **Architecture** | Clean Architecture + MVVM |
| **Asynchronous Logic** | Kotlin Coroutines & Flow |
| **Navigation** | Compose Navigation Component |

---

## Implementation Details

### Authentication Flow
- **AuthViewModel**: Manages authentication states (`isLoading`, `error`, `isAuthenticated`).
- **AuthValidator**: A specialized utility object that enforces business rules for passwords and email formats before network calls are made.
- **AppModule**: Provides Hilt bindings for `FirebaseAuth` and the repository implementation.

### Data Management
- **FlipFinanceDatabase**: A Room-managed singleton ensuring thread-safe access to user transaction history.
- **TransactionViewModel**: Uses `StateFlow` and `SharingStarted.WhileSubscribed` to provide an efficient, lifecycle-aware stream of filtered data to the UI.
- **Supabase Integration**: Implements a `NonCancellable` coroutine context for receipt uploads to ensure data integrity during network transitions.

### Navigation Routing
- **MainActivity**: Acts as the single entry point, observing both `currentUser` and `hasCompletedOnboarding` states to determine the UI entry point.
- **Screen**: A sealed class hierarchy defining routes, titles, and icons for type-safe navigation.
- **Persistent Preferences**: Leverages **Jetpack DataStore** to ensure the onboarding experience is only displayed on the first launch or until successfully completed.

---

## Setup & Installation

1. **Clone the repository.**
2. **Firebase Setup**:
    - Create a project in the [Firebase Console](https://console.firebase.google.com/).
    - Add an Android App with the package name `com.example.flipfinance`.
    - Download the `google-services.json` and place it in the `app/` directory.
    - Enable **Email/Password** authentication in the Firebase Auth settings.
3. **Supabase Setup**:
    - Create a bucket named `RecieptStorage` in your Supabase project.
    - Ensure public access or appropriate RLS policies are set for receipt retrieval.
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
- [ ] **Next**: Interactive spending analytics with MPAndroidChart.
