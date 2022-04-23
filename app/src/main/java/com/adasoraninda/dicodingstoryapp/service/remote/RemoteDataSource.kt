package com.adasoraninda.dicodingstoryapp.service.remote

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.adasoraninda.dicodingstoryapp.common.paging.StoryPagingSource
import com.adasoraninda.dicodingstoryapp.model.InputAddStory
import com.adasoraninda.dicodingstoryapp.service.remote.api.DicodingStoryApi
import com.adasoraninda.dicodingstoryapp.service.remote.request.LoginRequest
import com.adasoraninda.dicodingstoryapp.service.remote.request.RegisterRequest
import com.adasoraninda.dicodingstoryapp.service.remote.response.BaseResponse
import com.adasoraninda.dicodingstoryapp.service.remote.response.LoginResponse
import com.adasoraninda.dicodingstoryapp.service.remote.response.StoryResponse
import com.adasoraninda.dicodingstoryapp.service.remote.response.StoryResultResponse
import com.adasoraninda.dicodingstoryapp.utils.ERROR_EMPTY
import com.adasoraninda.dicodingstoryapp.utils.errorHandler
import com.adasoraninda.dicodingstoryapp.utils.formatToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import timber.log.Timber
import kotlin.coroutines.CoroutineContext

class RemoteDataSource(
    private val service: DicodingStoryApi,
    private val dispatcher: CoroutineContext = Dispatchers.IO,
) : IRemoteDataSource {

    override fun register(
        name: String,
        email: String,
        password: String,
    ): Flow<BaseResponse> = flow {
        val registerRequest = RegisterRequest(name, email, password)
        val response = service.register(registerRequest)

        Timber.d(registerRequest.toString())

        if (response.isSuccessful.not()) {
            emit(errorHandler(response, BaseResponse::class.java))
            return@flow
        }

        val body = response.body() ?: throw IllegalStateException()
        Timber.d(body.message)
        emit(body)
    }
        .flowOn(dispatcher)
        .catch {
            emit(
                BaseResponse(
                    error = true,
                    message = ERROR_EMPTY
                )
            )
        }

    override fun login(
        email: String,
        password: String
    ): Flow<LoginResponse> = flow {
        val loginRequest = LoginRequest(email, password)
        val response = service.login(loginRequest)

        Timber.d(loginRequest.toString())

        if (response.isSuccessful.not()) {
            val errorResponse = errorHandler(response, BaseResponse::class.java)
            emit(LoginResponse().apply {
                error = errorResponse.error
                message = errorResponse.message
            })
            return@flow
        }

        val body = response.body() ?: throw IllegalStateException()
        Timber.d(body.message)
        emit(body)
    }
        .flowOn(dispatcher)
        .catch {
            emit(LoginResponse().apply {
                error = true
                message = ERROR_EMPTY
            })
        }

    override fun getPagingStories(token: String, size: Int): Flow<PagingData<StoryResultResponse>> {
        return Pager(
            config = PagingConfig(
                pageSize = size
            ),
            pagingSourceFactory = {
                StoryPagingSource(token, service)
            })
            .flow
            .flowOn(dispatcher)
    }

    override fun getStories(
        token: String,
        page: Int,
        size: Int,
        location: Int,
    ): Flow<Result<StoryResponse>> = flow {
        val response = service.getStories(token, page, size, location)

        if (response.isSuccessful.not()) {
            val errorBody = errorHandler(response, BaseResponse::class.java)
            val exception = IllegalStateException(errorBody.message)
            emit(Result.failure(exception))
            return@flow
        }

        val body = response.body() ?: throw IllegalStateException()
        Timber.d(body.message)
        emit(Result.success(body))
    }
        .flowOn(dispatcher)
        .catch { emit(Result.failure(IllegalStateException(ERROR_EMPTY))) }

    override fun addStory(
        body: InputAddStory
    ): Flow<BaseResponse> = flow {
        Timber.d(body.toString())

        val token = body.token ?: throw IllegalStateException()
        val file = body.file ?: throw IllegalStateException()
        val desc = body.description ?: throw IllegalStateException()
        val lat = body.lat
        val lon = body.lon

        val descRequest = desc.toRequestBody("text/plain".toMediaType())
        val imageRequest = file.asRequestBody("image/jpeg".toMediaTypeOrNull())
        val imagePart = MultipartBody.Part.createFormData(
            "photo",
            file.name,
            imageRequest
        )

        val response = if (lat != null && lon != null) {
            val latRequest = lat.toString().toRequestBody("text/plain".toMediaType())
            val lonRequest = lon.toString().toRequestBody("text/plain".toMediaType())

            service.addStory(token.formatToken(), imagePart, descRequest, latRequest, lonRequest)
        } else {
            service.addStory(token.formatToken(), imagePart, descRequest)
        }

        if (response.isSuccessful.not()) {
            emit(errorHandler(response, BaseResponse::class.java))
            return@flow
        }

        val responseBody = response.body() ?: throw IllegalStateException()
        Timber.d(responseBody.message)
        emit(responseBody)
    }
        .flowOn(dispatcher)
        .catch {
            emit(BaseResponse(error = true, message = ERROR_EMPTY))
        }

}