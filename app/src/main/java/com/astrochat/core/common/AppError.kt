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

    object NotFound : AppError()
    object Unknown : AppError()
}
