package com.astrochat.core.common

sealed class AppError : Exception() {
    sealed class Network : AppError() {
        object NoConnection : Network()
        object Timeout : Network()
        object Server : Network()
        object Unknown : Network()
    }

    sealed class Data : AppError() {
        object Serialization : Data()
        object InvalidResponse : Data()
    }

    sealed class Database : AppError() {
        object Read : Database()
        object Write : Database()
    }

    sealed class Sync : AppError() {
        object TemporaryFailure : Sync()
        object PermanentFailure : Sync()
    }

    object NotFound : AppError()
    object Unknown : AppError()

    fun getUserFriendlyMessage(): String {
        return when (this) {
            is Network.NoConnection -> "No internet connection. Please check your network."
            is Network.Timeout -> "The request timed out. Please try again."
            is Network.Server -> "Server is currently unavailable. Please try again later."
            is Network.Unknown -> "A network error occurred. Please try again."
            is Database.Read -> "Could not read from local database."
            is Database.Write -> "Could not save to local database."
            is Data.Serialization -> "Data processing error (Serialization)."
            is Data.InvalidResponse -> "Received invalid response from server."
            is Sync.TemporaryFailure -> "Sync failed temporarily. Will retry soon."
            is Sync.PermanentFailure -> "Sync failed permanently."
            is NotFound -> "The requested information was not found."
            else -> "An unexpected error occurred."
        }
    }
}
