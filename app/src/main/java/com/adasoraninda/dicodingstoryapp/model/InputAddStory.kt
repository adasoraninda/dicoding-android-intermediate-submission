package com.adasoraninda.dicodingstoryapp.model

import java.io.File

data class InputAddStory(
    val token: String? = null,
    val file: File? = null,
    val description: String? = null
)