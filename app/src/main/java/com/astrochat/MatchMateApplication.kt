package com.astrochat

import android.app.Application
import com.astrochat.feature.matches.data.sync.SyncCoordinator
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class MatchMateApplication : Application() {
    @Inject
    lateinit var syncCoordinator: SyncCoordinator

    override fun onCreate() {
        super.onCreate()
        syncCoordinator.initialize()
    }
}
