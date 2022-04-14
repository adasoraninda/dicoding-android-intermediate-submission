package com.adasoraninda.dicodingstoryapp.utils

import androidx.fragment.app.Fragment
import com.adasoraninda.dicodingstoryapp.DicodingStoryApp

fun Fragment.injector() = (requireActivity().applicationContext as DicodingStoryApp).injector
