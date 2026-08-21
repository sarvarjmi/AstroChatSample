# Fix SyncCoordinator Cancellation Loop and Error Handling

The sync failure reported in the logs ("An unexpected error occurred") is caused by a cancellation loop in `SyncCoordinator`. When `syncPendingOperations` is called, it updates the database (incrementing the attempt count). This update triggers the `getPendingOperations()` flow observed in `initialize()`. Since `collectLatest` is used, it cancels the currently running `syncPendingOperations` and starts a new one, leading to a `CancellationException` which is incorrectly logged as an "unexpected error."

## Proposed Changes

### [feature/matches]

#### [MODIFY] [SyncCoordinator.kt](file:///C:/Users/Sarva/Documents/AndroidStudioProjects/AstroChatSample/app/src/main/java/com/astrochat/feature/matches/data/sync/SyncCoordinator.kt)
- Add `distinctUntilChanged()` to the flow in `initialize()` to prevent re-triggering the sync unless the connectivity status or the "is not empty" condition actually changes.
- Modify the `catch` block in `syncPendingOperations` to rethrow `CancellationException`. This ensures that coroutine cancellation is handled correctly by the framework and not treated as a sync failure.
- Add logging of the actual exception message in the `catch` block for better debugging in the future.

#### [MODIFY] [ErrorMapper.kt](file:///C:/Users/Sarva/Documents/AndroidStudioProjects/AstroChatSample/app/src/main/java/com/astrochat/core/common/ErrorMapper.kt)
- Update `toAppError()` to handle `CancellationException` explicitly if it ever reaches there, although rethrowing it in the caller is preferred. (Actually, I will stick to rethrowing in the caller as it is more idiomatic for coroutines).

## Verification Plan

### Automated Tests
- Run existing unit tests for `SyncCoordinator` to ensure no regressions:
  `./gradlew :app:testDebugUnitTest --tests "com.astrochat.feature.matches.data.sync.SyncCoordinatorTest"`

### Manual Verification
- Deploy the app and trigger a sync operation (e.g., by making a match decision).
- Observe the logs to ensure "Successfully synced profile" appears instead of "Sync failed."
