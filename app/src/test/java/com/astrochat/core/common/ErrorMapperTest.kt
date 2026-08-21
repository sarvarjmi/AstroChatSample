package com.astrochat.core.common

import org.junit.Assert.assertEquals
import org.junit.Test
import java.io.IOException
import java.net.SocketTimeoutException

class ErrorMapperTest {

    @Test
    fun `SocketTimeoutException maps to Network Timeout`() {
        val exception = SocketTimeoutException()
        val error = exception.toAppError()
        assertEquals(AppError.Network.Timeout, error)
    }

    @Test
    fun `IOException maps to Network NoConnection`() {
        val exception = IOException()
        val error = exception.toAppError()
        assertEquals(AppError.Network.NoConnection, error)
    }

    @Test
    fun `Unknown exception maps to Unknown error`() {
        val exception = Exception("Random")
        val error = exception.toAppError()
        assertEquals(AppError.Unknown, error)
    }
}
