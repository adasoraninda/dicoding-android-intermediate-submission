package com.adasoraninda.dicodingstoryapp.service.remote

import androidx.annotation.VisibleForTesting
import com.adasoraninda.dicodingstoryapp.model.InputAddStory
import com.adasoraninda.dicodingstoryapp.service.remote.api.DicodingStoryApi
import com.adasoraninda.dicodingstoryapp.service.remote.request.LoginRequest
import com.adasoraninda.dicodingstoryapp.service.remote.request.RegisterRequest
import com.adasoraninda.dicodingstoryapp.service.remote.response.BaseResponse
import com.adasoraninda.dicodingstoryapp.service.remote.response.LoginResponse
import com.adasoraninda.dicodingstoryapp.service.remote.response.StoryResponse
import com.adasoraninda.dicodingstoryapp.utils.EMPTY_ERROR
import com.adasoraninda.dicodingstoryapp.utils.formatToken
import com.google.gson.GsonBuilder
import kotlinx.coroutines.CoroutineDispatcher
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
import retrofit2.Response
import timber.log.Timber

class RemoteDataSource(
    private val service: DicodingStoryApi,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO,
) {

    fun register(
        name: String,
        email: String,
        password: String,
    ): Flow<BaseResponse> = flow {
        val registerRequest = RegisterRequest(name, email, password)
        val response = service.register(registerRequest)

        Timber.d(registerRequest.toString())

        if (response.isSuccessful.not()) {
            emit(errorHandler(response))
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
                    message = EMPTY_ERROR
                )
            )
        }

    fun login(
        email: String,
        password: String
    ): Flow<LoginResponse> = flow {
        val loginRequest = LoginRequest(email, password)
        val response = service.login(loginRequest)

        Timber.d(loginRequest.toString())

        if (response.isSuccessful.not()) {
            val errorResponse = errorHandler(response)
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
                message = EMPTY_ERROR
            })
        }

    fun getStories(token: String): Flow<Result<StoryResponse>> = flow {
        val response = service.getStories(token)

        if (response.isSuccessful.not()) {
            val errorBody = errorHandler(response)
            val exception = IllegalStateException(errorBody.message)
            emit(Result.failure(exception))
            return@flow
        }

        val body = response.body() ?: throw IllegalStateException()
        Timber.d(body.message)
        emit(Result.success(body))
    }
        .flowOn(dispatcher)
        .catch { emit(Result.failure(IllegalStateException(EMPTY_ERROR))) }

    fun addStory(
        body: InputAddStory
    ): Flow<BaseResponse> = flow {
        val token = body.token ?: throw IllegalStateException()
        val file = body.file ?: throw IllegalStateException()
        val desc = body.description ?: throw IllegalStateException()

        val descRequest = desc.toRequestBody("text/plain".toMediaType())
        val imageRequest = file.asRequestBody("image/jpeg".toMediaTypeOrNull())
        val imagePart = MultipartBody.Part.createFormData(
            "photo",
            file.name,
            imageRequest
        )

        val response = service.addStory(token.formatToken(), imagePart, descRequest)

        if (response.isSuccessful.not()) {
            emit(errorHandler(response))
            return@flow
        }

        val responseBody = response.body() ?: throw IllegalStateException()
        Timber.d(responseBody.message)
        emit(responseBody)
    }
        .flowOn(dispatcher)
        .catch {
            emit(BaseResponse(error = true, message = EMPTY_ERROR))
        }


    @VisibleForTesting
    fun <T> errorHandler(response: Response<T>): BaseResponse {
        val errorBody = response.errorBody()?.string()
        Timber.e(errorBody)
        return GsonBuilder().create().fromJson(errorBody, BaseResponse::class.java)
    }
}