package com.adasoraninda.dicodingstoryapp.features.auth.login

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.DecelerateInterpolator
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.adasoraninda.dicodingstoryapp.R
import com.adasoraninda.dicodingstoryapp.databinding.FragmentLoginBinding
import kotlinx.coroutines.delay

class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding

    private val listOfViews = mutableListOf<View>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        requireActivity().onBackPressedDispatcher.addCallback {
            requireActivity().finishAffinity()
        }

        setupView()
        viewActionListeners()
    }

    override fun onStart() {
        super.onStart()
        startAnimation()
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    private fun setupView() {
        val view = binding ?: return

        listOfViews.addAll(
            listOf(
                view.textTitle,
                view.textLogin,
                view.textLabelEmail,
                view.inputEmail,
                view.textLabelPassword,
                view.inputPassword,
                view.buttonLogin,
                view.buttonRegister
            )
        )
    }

    private fun viewActionListeners() {
        val view = binding ?: return

        view.buttonRegister.setOnClickListener {
            lifecycleScope.launchWhenResumed {
                endAnimation()
                delay(400L)
                findNavController().navigate(R.id.nav_login_to_register)
            }
        }

        view.buttonLogin.setOnClickListener {

        }
    }

    private fun startAnimation() {
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
            playTogether(animatedViews)
        }.start()
    }

    private fun endAnimation() {
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
            playTogether(animatedViews)
        }.start()
    }


}