package com.adasoraninda.dicodingstoryapp.remote

import com.adasoraninda.dicodingstoryapp.model.InputAddStory
import com.adasoraninda.dicodingstoryapp.service.remote.RemoteDataSource
import com.adasoraninda.dicodingstoryapp.service.remote.api.DicodingStoryApi
import com.adasoraninda.dicodingstoryapp.service.remote.request.LoginRequest
import com.adasoraninda.dicodingstoryapp.service.remote.request.RegisterRequest
import com.adasoraninda.dicodingstoryapp.service.remote.response.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.test.runTest
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.`when`
import org.mockito.Mockito.only
import org.mockito.kotlin.argumentCaptor
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import retrofit2.Response
import java.io.File

@OptIn(ExperimentalCoroutinesApi::class)
class RemoteDataSourceTest {

    private lateinit var service: DicodingStoryApi
    private lateinit var remoteDataSource: RemoteDataSource

    private val name = "name"
    private val email = "email@email.com"
    private val password = "password"

    @Before
    fun setup() {
        service = mock()
    }

    @Test
    fun register_test() = runTest {
        // arrange
        remoteDataSource = RemoteDataSource(service, testScheduler)

        val argCaptor = argumentCaptor<RegisterRequest>()
        val response = BaseResponse(error = false, message = "Success")
        val responseSuccess = Response.success(response)

        `when`(service.register(argCaptor.capture()))
            .thenReturn(responseSuccess)

        val result = remoteDataSource.register(name, email, password).firstOrNull()
        val registerArgs = argCaptor.firstValue

        verify(service, only()).register(argCaptor.capture())
        Assert.assertNotNull(result)
        Assert.assertEquals(response.error, result?.error)
        Assert.assertEquals(response.message, result?.message)
        Assert.assertEquals(name, registerArgs.name)
        Assert.assertEquals(email, registerArgs.email)
        Assert.assertEquals(password, registerArgs.password)
    }

    @Test
    fun login_test() = runTest {
        // arrange
        remoteDataSource = RemoteDataSource(service, testScheduler)

        val argCaptor = argumentCaptor<LoginRequest>()
        val baseResponse = BaseResponse(error = false, message = "Success")
        val response = LoginResponse(
            LoginResultResponse(
                userId = "id", name = "name", token = "token"
            )
        ).apply {
            error = baseResponse.error
            message = baseResponse.message
        }
        val responseSuccess = Response.success(response)

        `when`(service.login(argCaptor.capture()))
            .thenReturn(responseSuccess)

        // act
        val result = remoteDataSource.login(email, password).firstOrNull()
        val loginArgs = argCaptor.firstValue

        // assert
        verify(service, only()).login(argCaptor.capture())
        Assert.assertNotNull(result)
        Assert.assertEquals(email, loginArgs.email)
        Assert.assertEquals(password, loginArgs.password)
        Assert.assertEquals(response.error, result?.error)
        Assert.assertEquals(response.message, result?.message)
        Assert.assertEquals(response.loginResult?.userId, result?.loginResult?.userId)
        Assert.assertEquals(response.loginResult?.name, result?.loginResult?.name)
        Assert.assertEquals(response.loginResult?.token, result?.loginResult?.token)
    }

    @Test
    fun get_paging_stories_test() = runTest {
        remoteDataSource = RemoteDataSource(service, testScheduler)
        val token = "token"
        val size = 10

        val result = remoteDataSource.getPagingStories(token, size).firstOrNull()

        Assert.assertNotNull(result)
    }

    @Test
    fun get_stories_test() = runTest {
        // arrange
        remoteDataSource = RemoteDataSource(service, testScheduler)
        val token = "token"
        val page = 1
        val size = 10
        val location = 1
        val stories = listOf(StoryResultResponse(id = "id"))
        val storyResponse = StoryResponse(listStory = stories).apply {
            error = false
            message = "Success"
        }
        val successResponse = Response.success(storyResponse)

        `when`(service.getStories(token, page, size, location))
            .thenReturn(successResponse)

        val result = remoteDataSource.getStories(token, page, size, location).firstOrNull()

        verify(service, only()).getStories(token, page, size, location)
        Assert.assertNotNull(result)
        Assert.assertEquals(true, result?.isSuccess)
        Assert.assertEquals(false, result?.getOrThrow()?.error)
        Assert.assertEquals("Success", result?.getOrThrow()?.message)
        Assert.assertEquals(storyResponse.listStory?.size, result?.getOrThrow()?.listStory?.size)
    }

    @Test
    fun add_story_if_lat_lon_not_null_test() = runTest {
        // arrange
        remoteDataSource = RemoteDataSource(service, testScheduler)
        val response = BaseResponse(error = false, message = "Success")
        val responseSuccess = Response.success(response)
        val tokenCaptor = argumentCaptor<String>()
        val imagePartCaptor = argumentCaptor<MultipartBody.Part>()
        val descReqCaptor = argumentCaptor<RequestBody>()
        val latReqCaptor = argumentCaptor<RequestBody>()
        val lonReqCaptor = argumentCaptor<RequestBody>()
        val input = InputAddStory(
            token = "token",
            file = File("file"),
            description = "desc",
            lat = 1.0,
            lon = 1.0
        )

        `when`(
            service.addStory(
                tokenCaptor.capture(),
                imagePartCaptor.capture(),
                descReqCaptor.capture(),
                latReqCaptor.capture(),
                lonReqCaptor.capture()
            )
        ).thenReturn(responseSuccess)

        // act
        val result = remoteDataSource.addStory(input).firstOrNull()

        // assert
        verify(service, only()).addStory(
            tokenCaptor.capture(),
            imagePartCaptor.capture(),
            descReqCaptor.capture(),
            latReqCaptor.capture(),
            lonReqCaptor.capture()
        )
        Assert.assertNotNull(result)
        Assert.assertEquals(response.error, result?.error)
        Assert.assertEquals(response.message, result?.message)
    }

    @Test
    fun add_story_if_lat_lon_is_null_test() = runTest {
        // arrange
        remoteDataSource = RemoteDataSource(service, testScheduler)
        val response = BaseResponse(error = false, message = "Success")
        val responseSuccess = Response.success(response)
        val tokenCaptor = argumentCaptor<String>()
        val imagePartCaptor = argumentCaptor<MultipartBody.Part>()
        val descReqCaptor = argumentCaptor<RequestBody>()
        val input = InputAddStory(
            token = "token",
            file = File("file"),
            description = "desc",
            lat = null,
            lon = null
        )

        `when`(
            service.addStory(
                tokenCaptor.capture(),
                imagePartCaptor.capture(),
                descReqCaptor.capture(),
            )
        ).thenReturn(responseSuccess)

        // act
        val result = remoteDataSource.addStory(input).firstOrNull()

        // assert
        verify(service, only()).addStory(
            tokenCaptor.capture(),
            imagePartCaptor.capture(),
            descReqCaptor.capture(),
        )
        Assert.assertNotNull(result)
        Assert.assertEquals(response.error, result?.error)
        Assert.assertEquals(response.message, result?.message)
    }

}