package com.adasoraninda.dicodingstoryapp.features.story.detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.transition.TransitionInflater
import com.adasoraninda.dicodingstoryapp.databinding.FragmentDetailStoryBinding
import com.adasoraninda.dicodingstoryapp.utils.formatDate
import com.adasoraninda.dicodingstoryapp.utils.loadImage

class DetailStoryFragment : Fragment() {

    private var _binding: FragmentDetailStoryBinding? = null
    private val binding get() = _binding

    private val detailArgs by navArgs<DetailStoryFragmentArgs>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentDetailStoryBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sharedElementEnterTransition =
            TransitionInflater.from(requireContext()).inflateTransition(android.R.transition.move)

        requireActivity().onBackPressedDispatcher.addCallback {
            findNavController().popBackStack()
        }

        setupView()
        actionListeners()
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    private fun setupView() {
        val view = binding ?: return

        val data = detailArgs.story

        view.imageStory.loadImage(data.photoUrl)
        view.textName.text = data.name
        view.textDate.text = data.createdAt.formatDate()
        view.textDesc.text = data.description
    }

    private fun actionListeners() {
        val view = binding ?: return

        view.imageBackButton.setOnClickListener {
            requireActivity().onBackPressed()
        }
    }

}