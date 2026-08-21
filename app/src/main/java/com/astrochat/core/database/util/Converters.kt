package com.astrochat.core.database.util

import androidx.room.TypeConverter
import com.astrochat.feature.matches.domain.model.MatchDecision
import com.astrochat.feature.matches.domain.model.SyncStatus

class Converters {
    @TypeConverter
    fun fromMatchDecision(value: MatchDecision): String = value.name

    @TypeConverter
    fun toMatchDecision(value: String): MatchDecision = enumValueOf(value)

    @TypeConverter
    fun fromSyncStatus(value: SyncStatus): String = value.name

    @TypeConverter
    fun toSyncStatus(value: String): SyncStatus = enumValueOf(value)
}
