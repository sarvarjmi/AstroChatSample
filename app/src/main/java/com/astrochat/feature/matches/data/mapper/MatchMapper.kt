package com.astrochat.feature.matches.data.mapper

import com.astrochat.feature.matches.data.remote.dto.UserDto
import com.astrochat.feature.matches.domain.model.MatchProfile

fun UserDto.toDomain(): MatchProfile {
    return MatchProfile(
        id = login.uuid,
        firstName = name.first,
        lastName = name.last,
        age = dob.age,
        city = location.city,
        state = location.state,
        country = location.country,
        imageUrl = picture.large
    )
}
