package com.adasoraninda.dicodingstoryapp.service.remote.response

import com.google.gson.annotations.SerializedName

open class BaseResponse(
    @SerializedName(value = "error") open val error: Boolean? = null,
    @SerializedName(value = "message") open val message: String? = null
)