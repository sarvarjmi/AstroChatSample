# MatchMate

MatchMate is a modern Android matrimonial-style application that allows users to discover potential matches. It is built with an offline-first architecture, ensuring a seamless user experience even without an active internet connection.

## 🚀 Features

- **Paginated Discovery**: Profiles are fetched incrementally from the Random User API as you scroll.
- **Offline Mode**: View cached profiles and make Accept/Decline decisions while offline.
- **Persistent Decisions**: Your decisions are saved locally and survive app restarts.
- **Background Synchronization**: Pending offline decisions are automatically synchronized once connectivity is restored.
- **Resilient UX**: Real-time connectivity monitoring with an animated status banner and user-friendly error feedback.

## 🏗 Architecture

The project follows **Clean Architecture** principles and the **MVI (Model-View-Intent)** pattern:

- **Presentation Layer**: Built with **Jetpack Compose** and **Material 3**. Uses a `ViewModel` as a state holder, exposing an immutable `UiState` and processing `UiEvents`.
- **Domain Layer**: Contains business logic, including the `MatchProfile` model and the `MatchRepository` contract. Independent of any framework or data source.
- **Data Layer**: Manages data orchestration between the **Remote API (Retrofit)** and the **Local Database (Room)**. Uses **Paging 3** with a `RemoteMediator` for incremental loading.

## 🛠 Tech Stack

- **UI**: Jetpack Compose, Material 3, Coil (Image Loading).
- **Persistence**: Room Database.
- **Networking**: Retrofit, OkHttp, Gson.
- **DI**: Hilt (Dagger).
- **Asynchrony**: Kotlin Coroutines & Flow.
- **Pagination**: Paging 3.

## 🧪 Testing

The project includes a comprehensive suite of **21 unit and integration tests** ensuring reliability across all layers:

- **Domain/Data Unit Tests**: Verified mapping, repository logic, and error handling.
- **Database Tests**: Verified transactional integrity and user decision preservation during refreshes.
- **Integration Tests**: Robolectric-based tests verifying the end-to-end flow from repository actions to persistence.
- **UI Tests**: Compose UI tests verifying profile rendering and connectivity status changes.

## 📝 Known Limitations

- **API Limitation**: The supplied Random User API is read-only. While the application implements a full synchronization coordinator with retry and backoff logic, actual server-side mutation calls are simulated.
- **Stable Identity**: The app uses a session-based seed to ensure deterministic pagination from the Random User API.

## ⚙️ Setup & Run

1. Clone the repository.
2. Open in Android Studio (Ladybug or later recommended).
3. Sync Gradle and run the `:app` module on an emulator or physical device (API 24+).
4. To run tests: `./gradlew test` and `./gradlew connectedAndroidTest`.

---
*Built as part of an Android Coding Assignment.*
