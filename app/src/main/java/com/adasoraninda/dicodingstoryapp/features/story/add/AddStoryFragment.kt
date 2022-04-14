package com.adasoraninda.dicodingstoryapp.features.story.add

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.adasoraninda.dicodingstoryapp.databinding.FragmentAddStoryBinding
import com.adasoraninda.dicodingstoryapp.databinding.FragmentSplashBinding

class AddStoryFragment: Fragment() {

    private var _binding: FragmentAddStoryBinding? = null
    private val binding get() = _binding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAddStoryBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

}