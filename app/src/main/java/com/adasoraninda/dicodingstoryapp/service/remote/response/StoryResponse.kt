package com.adasoraninda.dicodingstoryapp.service.remote.response

import com.google.gson.annotations.SerializedName

data class StoryResultResponse(
    @SerializedName(value = "id") val id: String? = null,
    @SerializedName(value = "name") val name: String? = null,
    @SerializedName(value = "description") val description: String? = null,
    @SerializedName(value = "photoUrl") val photoUrl: String? = null,
    @SerializedName(value = "createdAt") val createdAt: String? = null,
    @SerializedName(value = "lat") val latitude: String? = null,
    @SerializedName(value = "lon") val longitude: String? = null,
)

data class StoryResponse(
    @SerializedName(value = "listStory") val listStory: List<StoryResultResponse>? = null
) : BaseResponse()