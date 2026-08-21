package com.astrochat.core.common

sealed class DataResult<out T> {
    data class Success<out T>(val data: T) : DataResult<T>()
    data class Error(val error: AppError) : DataResult<Nothing>()
    object Loading : DataResult<Nothing>()
}

inline fun <T> DataResult<T>.onSuccess(action: (T) -> Unit): DataResult<T> {
    if (this is DataResult.Success) action(data)
    return this
}

inline fun <T> DataResult<T>.onError(action: (AppError) -> Unit): DataResult<T> {
    if (this is DataResult.Error) action(error)
    return this
}
