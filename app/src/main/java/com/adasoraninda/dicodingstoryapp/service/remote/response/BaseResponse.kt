package com.adasoraninda.dicodingstoryapp.service.remote.response

import com.google.gson.annotations.SerializedName

open class BaseResponse(
    @SerializedName(value = "error") open var error: Boolean? = null,
    @SerializedName(value = "message") open var message: String? = null
)