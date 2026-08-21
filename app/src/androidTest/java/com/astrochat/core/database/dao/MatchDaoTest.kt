package com.astrochat.core.database.dao

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.astrochat.core.database.MatchMateDatabase
import com.astrochat.core.database.entity.MatchEntity
import com.astrochat.feature.matches.domain.model.MatchDecision
import com.astrochat.feature.matches.domain.model.SyncStatus
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MatchDaoTest {

    private lateinit var database: MatchMateDatabase
    private lateinit var matchDao: MatchDao

    @Before
    fun setup() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            MatchMateDatabase::class.java
        ).allowMainThreadQueries().build()
        matchDao = database.matchDao()
    }

    @After
    fun teardown() {
        database.close()
    }

    @Test
    fun insertAndGetMatches() = runBlocking {
        val match = MatchEntity(
            id = "1",
            firstName = "John",
            lastName = "Doe",
            age = 30,
            city = "NY",
            state = "NY",
            country = "USA",
            imageUrl = "url",
            decision = MatchDecision.PENDING,
            syncStatus = SyncStatus.SYNCED,
            pageIndex = 1
        )

        matchDao.insertMatches(listOf(match))
        val matches = matchDao.getMatches().first()

        assertEquals(1, matches.size)
        assertEquals("John", matches[0].firstName)
    }

    @Test
    fun updateDecision() = runBlocking {
        val match = MatchEntity(
            id = "1",
            firstName = "John",
            lastName = "Doe",
            age = 30,
            city = "NY",
            state = "NY",
            country = "USA",
            imageUrl = "url",
            decision = MatchDecision.PENDING,
            syncStatus = SyncStatus.SYNCED,
            pageIndex = 1
        )

        matchDao.insertMatches(listOf(match))
        matchDao.updateDecision("1", MatchDecision.ACCEPTED, SyncStatus.PENDING_SYNC)

        val updatedMatch = matchDao.getMatches().first()[0]
        assertEquals(MatchDecision.ACCEPTED, updatedMatch.decision)
        assertEquals(SyncStatus.PENDING_SYNC, updatedMatch.syncStatus)
    }
}
