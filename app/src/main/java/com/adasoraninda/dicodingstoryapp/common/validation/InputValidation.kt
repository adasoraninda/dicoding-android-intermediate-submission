package com.adasoraninda.dicodingstoryapp.common.validation

interface InputValidation {

    fun <T> validate(data: T): Int?

}