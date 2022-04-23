package com.adasoraninda.dicodingstoryapp.service.remote.api

import com.adasoraninda.dicodingstoryapp.service.remote.request.LoginRequest
import com.adasoraninda.dicodingstoryapp.service.remote.request.RegisterRequest
import com.adasoraninda.dicodingstoryapp.service.remote.response.BaseResponse
import com.adasoraninda.dicodingstoryapp.service.remote.response.LoginResponse
import com.adasoraninda.dicodingstoryapp.service.remote.response.StoryResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.*

interface DicodingStoryApi {

    @POST(value = PATH_REGISTER)
    suspend fun register(
        @Body request: RegisterRequest,
    ): Response<BaseResponse>

    @POST(value = PATH_LOGIN)
    suspend fun login(
        @Body request: LoginRequest
    ): Response<LoginResponse>

    @GET(value = PATH_STORIES)
    suspend fun getStories(
        @Header(HEADER_AUTH) token: String,
        @Query(QUERY_PAGE) page: Int = 1,
        @Query(QUERY_PAGE_SIZE) size: Int = 10,
        @Query(QUERY_LOCATION) location: Int = 0,
    ): Response<StoryResponse>

    @Multipart
    @POST(value = PATH_STORIES)
    suspend fun addStory(
        @Header(HEADER_AUTH) token: String,
        @Part photo: MultipartBody.Part,
        @Part(PART_DESCRIPTION) description: RequestBody,
        @Part(PART_LAT) lat: RequestBody,
        @Part(PART_LON) lon: RequestBody,
    ): Response<BaseResponse>

    @Multipart
    @POST(value = PATH_STORIES)
    suspend fun addStory(
        @Header(HEADER_AUTH) token: String,
        @Part photo: MultipartBody.Part,
        @Part(PART_DESCRIPTION) description: RequestBody,
    ): Response<BaseResponse>
}