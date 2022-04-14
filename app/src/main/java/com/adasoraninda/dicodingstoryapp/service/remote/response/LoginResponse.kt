package com.adasoraninda.dicodingstoryapp.service.remote.response

import com.google.gson.annotations.SerializedName

data class LoginResultResponse(
    @SerializedName(value = "userId") val userId: String? = null,
    @SerializedName(value = "name") val name: String? = null,
    @SerializedName(value = "token") val token: String? = null,
)

data class LoginResponse(
    @SerializedName(value = "loginResult") val loginResult: LoginResultResponse? = null
) : BaseResponse()