# MatchMate — Offline-First Discovery App

**MatchMate** is a high-performance, production-grade Android application designed for discovering potential matches. Built with an **Offline-First** philosophy, it ensures a seamless, high-quality user experience even in challenging network conditions.

---

## 🚀 Key Features

- **🎯 Mandatory Pagination (Paging 3):** Implements a robust pagination logic using the **RemoteMediator** pattern. Fetches profiles incrementally as you scroll, bridging the network API and local Room storage efficiently.
- **🌙 Premium Dark Mode by Default:** Launches in a modern dark theme with a rich color palette, featuring a dynamic **Theme Toggle** and **Manual Refresh** in the toolbar.
- **🔌 Offline-Ready Decisions:** Browse previously cached profiles and make "Accept" or "Decline" decisions while completely offline.
- **🛡️ Data Integrity & Decision Preservation:** Custom transactional logic ensures that your local decisions are never overwritten by network data refreshes.
- **🔄 Background Sync Coordinator:** Automatically synchronizes pending offline actions when connectivity is restored, utilizing an **exponential backoff retry policy**.
- **✨ Zero-Flicker UX:** Optimized loading logic that prevents UI jumping and flashes of empty states during initial data fetches.
- **📡 Real-time Connectivity Monitoring:** Features an animated status banner that reacts instantly to network changes.

---

## 🏗 Technical Architecture

The project follows **Clean Architecture** principles and the **MVI (Model-View-Intent)** pattern to ensure complete separation of concerns and maximum testability.

### 1. Presentation Layer (MVI)
- **Jetpack Compose & Material 3:** Modern, declarative UI with rich custom color schemes.
- **State Management:** A single immutable `UiState` managed by a Hilt-powered `ViewModel`.
- **Unidirectional Data Flow:** User actions are dispatched as `UiEvents`, and one-time feedbacks are handled via `UiEffects` (Snackbars).

### 2. Domain Layer
- **Pure Kotlin:** Zero dependencies on Android or external frameworks.
- **Business Logic:** Defines the `MatchProfile` core model and the `MatchRepository` contract.

### 3. Data Layer (Offline-First)
- **Room Persistence:** Acts as the Single Source of Truth for the UI.
- **Paging 3:** Manages incremental data loading and memory-efficient list updates.
- **Retrofit & OkHttp:** Handles deterministic API calls using session-based seeding.
- **Sync Infrastructure:** Dedicated sync operations table and connectivity-aware coordinator.

---

## 🛠 Tech Stack

- **UI:** Jetpack Compose, Material 3, Coil (Image Loading).
- **Persistence:** Room Database.
- **Networking:** Retrofit, OkHttp, Gson.
- **DI:** Hilt (Dagger).
- **Asynchrony:** Kotlin Coroutines & Flow.
- **Pagination:** Paging 3 (Network + DB).
- **Architecture:** MVI + Clean Architecture.

---

## 🧪 Quality & Testing (22 Tests Passed)

The application is backed by a comprehensive suite of **22 unit and integration tests** ensuring reliability across all architectural layers.

- **Logic Tests:** Verified mapping, error classification, and MVI state transitions using **MockK** and **Turbine**.
- **Persistence Tests:** Validated Room transactions and the "Decision Preservation" merge logic.
- **Integration Tests:** Used **Robolectric** to verify end-to-end data flow from repository actions to database persistence.
- **UI Tests:** Verified profile rendering, connectivity feedback, and accessibility semantics.

---

## 📝 Technical Decisions & Known Limitations

- **Simulated Sync:** The supplied Random User API is read-only. I have implemented a full production-ready sync architecture (`SyncCoordinator`) with a documented simulation point for the mutation call.
- **Deterministic Experience:** The app uses a session-based seed to ensure that random profile lists remain consistent for the user throughout their session.
- **Accessibility:** 100% semantic coverage. Every profile card and action is optimized for screen readers with meaningful content descriptions.

---

## ⚙️ Setup & Installation

1.  **Clone the Repo:** `git clone <repo-url>`
2.  **Environment:** Open in **Android Studio (Ladybug or later)**.
3.  **Build:** Sync Gradle and run the `:app` module.
4.  **Target SDK:** Android 13+ (API 33+) with a minimum SDK of 24.
5.  **Tests:** Execute `./gradlew test` to run the 22-test suite.

---
*Developed by Sharvare for the AstroChat Android Developer Assignment.*
