package com.adasoraninda.dicodingstoryapp.service.remote.request

import com.google.gson.annotations.SerializedName

data class RegisterRequest(
    @SerializedName(value = "name") val name: String,
    @SerializedName(value = "email") val email: String,
    @SerializedName(value = "password") val password: String,
)