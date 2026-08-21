package com.astrochat.feature.matches.data.remote

import com.astrochat.core.common.AppError
import com.astrochat.core.common.DataResult
import com.astrochat.feature.matches.data.remote.dto.*
import kotlinx.coroutines.test.runTest
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MatchRemoteDataSourceTest {

    private lateinit var mockWebServer: MockWebServer
    private lateinit var api: MatchApi
    private lateinit var dataSource: MatchRemoteDataSource

    @Before
    fun setup() {
        mockWebServer = MockWebServer()
        api = Retrofit.Builder()
            .baseUrl(mockWebServer.url("/"))
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(MatchApi::class.java)
        dataSource = MatchRemoteDataSource(api)
    }

    @After
    fun teardown() {
        mockWebServer.shutdown()
    }

    @Test
    fun `getMatches returns success when API responds with 200`() = runTest {
        val json = """
            {
              "results": [
                {
                  "login": { "uuid": "1" },
                  "name": { "first": "A", "last": "B" },
                  "dob": { "age": 20 },
                  "location": { "city": "C", "state": "S", "country": "CO" },
                  "picture": { "large": "L" }
                }
              ],
              "info": { "seed": "abc", "page": 1 }
            }
        """.trimIndent()

        mockWebServer.enqueue(MockResponse().setResponseCode(200).setBody(json))

        val result = dataSource.getMatches(1, 10)

        assertTrue(result is DataResult.Success)
        assertEquals(1, (result as DataResult.Success).data.results.size)
        assertEquals("1", result.data.results[0].login.uuid)
    }

    @Test
    fun `getMatches returns network error when API throws IOException`() = runTest {
        mockWebServer.enqueue(MockResponse().setSocketPolicy(okhttp3.mockwebserver.SocketPolicy.DISCONNECT_AT_START))

        val result = dataSource.getMatches(1, 10)

        assertTrue(result is DataResult.Error)
        assertTrue((result as DataResult.Error).error is AppError.Network.NoConnection)
    }

    @Test
    fun `getMatches returns server error when API responds with 500`() = runTest {
        mockWebServer.enqueue(MockResponse().setResponseCode(500))

        val result = dataSource.getMatches(1, 10)

        assertTrue(result is DataResult.Error)
        assertTrue((result as DataResult.Error).error is AppError.Network.Server)
    }
}
