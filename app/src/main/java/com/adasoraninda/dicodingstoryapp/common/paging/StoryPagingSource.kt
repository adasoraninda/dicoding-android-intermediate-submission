package com.adasoraninda.dicodingstoryapp.common.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.adasoraninda.dicodingstoryapp.service.remote.api.DicodingStoryApi
import com.adasoraninda.dicodingstoryapp.service.remote.response.BaseResponse
import com.adasoraninda.dicodingstoryapp.service.remote.response.StoryResultResponse
import com.adasoraninda.dicodingstoryapp.utils.errorHandler

class StoryPagingSource(
    private val token: String,
    private val service: DicodingStoryApi
) : PagingSource<Int, StoryResultResponse>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, StoryResultResponse> {
        return try {
            val position = params.key ?: INITIAL_PAGE_INDEX
            val response = service.getStories(token, position, params.loadSize)

            if (response.isSuccessful.not()) {
                val errorResponse = errorHandler(response, BaseResponse::class.java)
                throw IllegalStateException(errorResponse.message)
            }

            val data = response.body()?.listStory ?: emptyList()

            LoadResult.Page(
                data = data,
                prevKey = if (position == INITIAL_PAGE_INDEX) null else position - 1,
                nextKey = if (data.isNullOrEmpty()) null else position + 1
            )
        } catch (e: Exception) {
            e.printStackTrace()
            return LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, StoryResultResponse>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }

    private companion object {
        const val INITIAL_PAGE_INDEX = 1
    }
}