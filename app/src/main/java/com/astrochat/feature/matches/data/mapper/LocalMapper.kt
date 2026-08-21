package com.astrochat.feature.matches.data.mapper

import com.astrochat.core.database.entity.MatchEntity
import com.astrochat.feature.matches.domain.model.MatchProfile

fun MatchEntity.toDomain(): MatchProfile {
    return MatchProfile(
        id = id,
        firstName = firstName,
        lastName = lastName,
        age = age,
        city = city,
        state = state,
        country = country,
        imageUrl = imageUrl,
        decision = decision,
        syncStatus = syncStatus
    )
}

fun MatchProfile.toEntity(pageIndex: Int): MatchEntity {
    return MatchEntity(
        id = id,
        firstName = firstName,
        lastName = lastName,
        age = age,
        city = city,
        state = state,
        country = country,
        imageUrl = imageUrl,
        decision = decision,
        syncStatus = syncStatus,
        pageIndex = pageIndex
    )
}
