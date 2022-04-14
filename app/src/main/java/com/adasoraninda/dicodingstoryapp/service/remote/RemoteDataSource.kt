package com.adasoraninda.dicodingstoryapp.service.remote

import com.adasoraninda.dicodingstoryapp.service.remote.api.DicodingStoryApi
import com.adasoraninda.dicodingstoryapp.service.remote.response.BaseResponse
import com.adasoraninda.dicodingstoryapp.service.remote.response.LoginResponse
import com.adasoraninda.dicodingstoryapp.service.remote.response.StoryResponse
import kotlinx.coroutines.flow.Flow
import okhttp3.MultipartBody
import okhttp3.RequestBody

class RemoteDataSource(
    private val service: DicodingStoryApi
) {

    fun register(
        name: String,
        email: String,
        password: String,
    ): Flow<BaseResponse>? {
        return null
    }

    fun login(
        email: String,
        password: String
    ): Flow<LoginResponse>? {
        return null
    }

    fun getStories(): Flow<StoryResponse>? {
        return null
    }

    fun addStory(
        token: String,
        photo: MultipartBody.Part,
        body: Map<String, RequestBody>
    ): Flow<BaseResponse>? {
        return null
    }

}