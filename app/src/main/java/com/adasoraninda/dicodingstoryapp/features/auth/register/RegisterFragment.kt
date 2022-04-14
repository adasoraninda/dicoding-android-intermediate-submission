package com.adasoraninda.dicodingstoryapp.features.auth.register

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.DecelerateInterpolator
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.adasoraninda.dicodingstoryapp.databinding.FragmentRegisterBinding
import kotlinx.coroutines.delay

class RegisterFragment : Fragment() {

    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding

    private val listOfViews = mutableListOf<View>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentRegisterBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        requireActivity().onBackPressedDispatcher.addCallback {
            lifecycleScope.launchWhenResumed {
                endAnimation()
                delay(400L)
                findNavController().popBackStack()
            }
        }

        setupView()
        viewActionListeners()
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    override fun onStart() {
        super.onStart()
        startAnimation()
    }

    private fun setupView() {
        val view = binding ?: return

        listOfViews.addAll(
            listOf(
                view.textTitle,
                view.textRegister,
                view.textLabelName,
                view.inputName,
                view.textLabelEmail,
                view.inputEmail,
                view.textLabelPassword,
                view.inputPassword,
                view.buttonRegister
            )
        )
    }

    private fun viewActionListeners() {
        val view = binding ?: return

        view.buttonRegister.setOnClickListener {

        }

        view.imageBackButton.setOnClickListener {
            requireActivity().onBackPressed()
        }
    }

    private fun startAnimation() {
        val backButton = binding?.imageBackButton ?: return
        val endPosition = backButton.y

        backButton.alpha = 0F
        backButton.translationY = -100F

        val backButtonTranslateAnim =
            ObjectAnimator.ofFloat(backButton, View.TRANSLATION_Y, endPosition)
        val backButtonAlphaAnim = ObjectAnimator.ofFloat(backButton, View.ALPHA, 1F)

        val backButtonAnimator = AnimatorSet().apply {
            startDelay = 500L
            duration = 500L
            interpolator = AccelerateDecelerateInterpolator()
            playTogether(backButtonTranslateAnim, backButtonAlphaAnim)
        }

        val newListViews = mutableListOf(*listOfViews.toTypedArray())
        val listOfEndPositions = newListViews.map { it.x }
        newListViews.forEach {
            it.alpha = 0F
            it.translationX = -400F
        }

        val animatedViews = newListViews.mapIndexed { i, view ->
            val translateX = ObjectAnimator.ofFloat(view, View.TRANSLATION_X, listOfEndPositions[i])
            val alpha = ObjectAnimator.ofFloat(view, View.ALPHA, 1F)
            AnimatorSet().apply {
                duration = 1000L
                interpolator = DecelerateInterpolator()
                playTogether(alpha, translateX)
            }
        }

        AnimatorSet().apply {
            playTogether(*animatedViews.toTypedArray(), backButtonAnimator)
        }.start()
    }

    private fun endAnimation() {
        val backButton = binding?.imageBackButton ?: return

        val backButtonTranslateAnim =
            ObjectAnimator.ofFloat(backButton, View.TRANSLATION_Y, -100f)
        val backButtonAlphaAnim = ObjectAnimator.ofFloat(backButton, View.ALPHA, 0F)

        val backButtonAnimator = AnimatorSet().apply {
            duration = 500L
            interpolator = AccelerateDecelerateInterpolator()
            playTogether(backButtonTranslateAnim, backButtonAlphaAnim)
        }

        val newListViews = mutableListOf(*listOfViews.toTypedArray())

        val animatedViews = newListViews.map {
            val translateX = ObjectAnimator.ofFloat(it, View.TRANSLATION_X, -400F)
            val alpha = ObjectAnimator.ofFloat(it, View.ALPHA, 0F)
            AnimatorSet().apply {
                duration = 400L
                interpolator = DecelerateInterpolator()
                playTogether(alpha, translateX)
            }
        }

        AnimatorSet().apply {
            playTogether(*animatedViews.toTypedArray(), backButtonAnimator)
        }.start()
    }

}