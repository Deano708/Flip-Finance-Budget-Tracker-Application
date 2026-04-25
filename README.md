# FlipFinance

FlipFinance is a production-grade **Android mobile application** built to demonstrate modern software engineering principles, scalable architecture, and robust financial tracking. It leverages **Jetpack Compose** for a reactive UI and **Firebase** for cloud-based identity management.

---

## Features

### User Authentication & Identity
- **Secure Onboarding**: Login and Registration flows with real-time input validation.
- **Identity Management**: Powered by **Firebase Authentication** for secure email/password sign-in.
- **Self-Service Password Recovery**: Integrated "Forgot Password" flow utilizing Firebase's secure email reset system.
- **First-Launch Experience**: A high-fidelity, 3-screen interactive onboarding pager designed to introduce core value propositions.

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

---

## Tech Stack

| Category | Technology |
| :--- | :--- |
| **Language** | Kotlin |
| **UI Framework** | Jetpack Compose (Material 3) |
| **Dependency Injection** | Hilt (Dagger) |
| **Authentication** | Firebase Auth |
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
3. **Build**:
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
- [ ] **Next**: RoomDB implementation for local expense persistence.
- [ ] **Next**: Supabase integration for receipt image storage.
