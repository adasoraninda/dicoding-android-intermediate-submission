package com.adasoraninda.dicodingstoryapp.service.remote

import androidx.paging.PagingData
import com.adasoraninda.dicodingstoryapp.model.InputAddStory
import com.adasoraninda.dicodingstoryapp.service.remote.response.BaseResponse
import com.adasoraninda.dicodingstoryapp.service.remote.response.LoginResponse
import com.adasoraninda.dicodingstoryapp.service.remote.response.StoryResponse
import com.adasoraninda.dicodingstoryapp.service.remote.response.StoryResultResponse
import kotlinx.coroutines.flow.Flow

interface IRemoteDataSource {

    fun register(
        name: String,
        email: String,
        password: String,
    ): Flow<BaseResponse>

    fun login(
        email: String,
        password: String
    ): Flow<LoginResponse>

    fun getPagingStories(token: String, size: Int = 10): Flow<PagingData<StoryResultResponse>>

    fun getStories(
        token: String,
        page: Int = 1,
        size: Int = 10,
        location: Int = 0,
    ): Flow<Result<StoryResponse>>

    fun addStory(
        body: InputAddStory
    ): Flow<BaseResponse>

}