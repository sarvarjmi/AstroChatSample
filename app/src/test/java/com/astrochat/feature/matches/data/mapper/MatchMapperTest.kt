package com.astrochat.feature.matches.data.mapper

import com.astrochat.feature.matches.data.remote.dto.*
import com.astrochat.feature.matches.domain.model.MatchDecision
import org.junit.Assert.assertEquals
import org.junit.Test

class MatchMapperTest {

    @Test
    fun `UserDto to MatchProfile mapping is correct`() {
        val userDto = UserDto(
            login = LoginDto("uuid-123"),
            name = NameDto("John", "Doe"),
            dob = DobDto(30),
            location = LocationDto("New York", "NY", "USA"),
            picture = PictureDto("https://example.com/image.jpg")
        )

        val domain = userDto.toDomain()

        assertEquals("uuid-123", domain.id)
        assertEquals("John", domain.firstName)
        assertEquals("Doe", domain.lastName)
        assertEquals(30, domain.age)
        assertEquals("New York", domain.city)
        assertEquals("NY", domain.state)
        assertEquals("USA", domain.country)
        assertEquals("https://example.com/image.jpg", domain.imageUrl)
        assertEquals(MatchDecision.PENDING, domain.decision)
    }
}
