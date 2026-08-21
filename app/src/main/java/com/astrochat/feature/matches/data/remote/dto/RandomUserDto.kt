package com.astrochat.feature.matches.data.remote.dto

import com.google.gson.annotations.SerializedName

data class RandomUserResponse(
    @SerializedName("results") val results: List<UserDto>,
    @SerializedName("info") val info: InfoDto
)

data class UserDto(
    @SerializedName("login") val login: LoginDto,
    @SerializedName("name") val name: NameDto,
    @SerializedName("dob") val dob: DobDto,
    @SerializedName("location") val location: LocationDto,
    @SerializedName("picture") val picture: PictureDto
)

data class LoginDto(
    @SerializedName("uuid") val uuid: String
)

data class NameDto(
    @SerializedName("first") val first: String,
    @SerializedName("last") val last: String
)

data class DobDto(
    @SerializedName("age") val age: Int
)

data class LocationDto(
    @SerializedName("city") val city: String,
    @SerializedName("state") val state: String,
    @SerializedName("country") val country: String
)

data class PictureDto(
    @SerializedName("large") val large: String
)

data class InfoDto(
    @SerializedName("seed") val seed: String,
    @SerializedName("page") val page: Int
)
