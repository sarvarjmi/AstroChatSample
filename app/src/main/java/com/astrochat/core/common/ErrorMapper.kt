package com.astrochat.core.common

import com.google.gson.JsonParseException
import retrofit2.HttpException
import java.io.IOException
import java.net.SocketTimeoutException

fun Throwable.toAppError(): AppError {
    return when (this) {
        is AppError -> this
        is SocketTimeoutException -> AppError.Network.Timeout
        is IOException -> AppError.Network.NoConnection
        is JsonParseException -> AppError.Data.Serialization
        is HttpException -> {
            when (code()) {
                404 -> AppError.NotFound
                in 500..599 -> AppError.Network.Server
                else -> AppError.Network.Unknown
            }
        }
        else -> AppError.Unknown
    }
}
