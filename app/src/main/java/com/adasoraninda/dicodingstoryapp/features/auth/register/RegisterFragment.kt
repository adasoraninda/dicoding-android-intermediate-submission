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
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.adasoraninda.dicodingstoryapp.R
import com.adasoraninda.dicodingstoryapp.common.dialog.InfoDialogFragment
import com.adasoraninda.dicodingstoryapp.databinding.FragmentRegisterBinding
import com.adasoraninda.dicodingstoryapp.utils.*
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.delay

class RegisterFragment : Fragment() {

    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding

    private val viewModel: RegisterViewModel by viewModels {
        injector().registerFactory
    }

    private val listOfViews = mutableListOf<View>()

    private var snackbar: Snackbar? = null
    private var dialogInfo: InfoDialogFragment? = null

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
        observeViewModel()
    }

    override fun onDestroyView() {
        snackbar?.dismiss()
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

    private fun observeViewModel() {
        val view = binding ?: return

        viewModel.dialogInfoSuccess.observe(viewLifecycleOwner) {
            var message = it ?: return@observe

            if (message == EMPTY_SUCCESS) {
                message = getString(R.string.success_occurred)
            }

            val f = parentFragmentManager.findFragmentByTag(InfoDialogFragment.TAG)
            if (f != null) {
                (f as InfoDialogFragment).dismiss()
            }

            dialogInfo = InfoDialogFragment(
                message = message,
                acceptText = requireContext().getString(R.string.go_to_login),
                cancelText = requireContext().getString(R.string.cancel),
                onAcceptClicked = {
                    viewModel.dismissSuccessDialog()
                    findNavController().navigate(R.id.nav_register_to_login)
                },
                onCancelClicked = {
                    viewModel.dismissSuccessDialog()
                }
            ).apply { isCancelable = false }

            dialogInfo?.show(parentFragmentManager, InfoDialogFragment.TAG)
        }

        viewModel.dialogInfoError.observe(viewLifecycleOwner) {
            var message = it ?: return@observe

            if (message == EMPTY_ERROR) {
                message = getString(R.string.error_occurred)
            }

            val f = parentFragmentManager.findFragmentByTag(InfoDialogFragment.TAG)
            if (f != null) (f as InfoDialogFragment).dismiss()

            dialogInfo = InfoDialogFragment(
                message = message,
                acceptText = requireContext().getString(R.string.ok),
                onAcceptClicked = {
                    viewModel.dismissErrorDialog()
                }
            ).apply { isCancelable = false }

            dialogInfo?.show(parentFragmentManager, InfoDialogFragment.TAG)
        }

        viewModel.errorMessage.observe(viewLifecycleOwner) { event ->
            val message = event.get() ?: return@observe

            snackbar = Snackbar.make(view.root, message, Snackbar.LENGTH_LONG)
            snackbar?.show()
        }

        viewModel.loading.observe(viewLifecycleOwner) { loading ->
            view.progressBar.isVisible = if (loading) {
                disableUserInteraction()
                true
            } else {
                enableUserInteraction()
                false
            }
        }
    }

    private fun viewActionListeners() {
        val view = binding ?: return

        view.buttonRegister.setOnClickListener {
            (it.rootView as ViewGroup).clearAllFocus()
            it.hideKeyboard()
            val name = view.inputName.text.toString().trim()
            val email = view.inputEmail.text.toString().trim()
            val password = view.inputPassword.text.toString().trim()

            viewModel.register(name, email, password)
        }

        view.imageBackButton.setOnClickListener {
            (it.rootView as ViewGroup).clearAllFocus()
            it.hideKeyboard()
            requireActivity().onBackPressed()
        }
    }

    private fun startAnimation() {
        // animate back button
        val backButton = binding?.imageBackButton ?: return
        backButton.alpha = 0F
        backButton.translationY = -100F

        val backButtonTranslateAnim =
            ObjectAnimator.ofFloat(backButton, View.TRANSLATION_Y, 0F)
        val backButtonAlphaAnim = ObjectAnimator.ofFloat(backButton, View.ALPHA, 1F)

        val backButtonAnimator = AnimatorSet().apply {
            startDelay = 500L
            duration = 500L
            interpolator = AccelerateDecelerateInterpolator()
            playTogether(backButtonTranslateAnim, backButtonAlphaAnim)
        }

        val newListViews = mutableListOf(*listOfViews.toTypedArray())
        newListViews.forEach {
            it.alpha = 0F
            it.translationX = -400F
        }

        // animate views
        val animatedViews = newListViews.map { view ->
            val translateX = ObjectAnimator.ofFloat(view, View.TRANSLATION_X, 0F)
            val alpha = ObjectAnimator.ofFloat(view, View.ALPHA, 1F)
            AnimatorSet().apply {
                duration = 1000L
                interpolator = DecelerateInterpolator()
                playTogether(alpha, translateX)
            }
        }

        // play both animations
        AnimatorSet().apply {
            playTogether(*animatedViews.toTypedArray(), backButtonAnimator)
        }.start()
    }

    private fun endAnimation() {
        // animate back button
        val backButton = binding?.imageBackButton ?: return

        val backButtonTranslateAnim =
            ObjectAnimator.ofFloat(backButton, View.TRANSLATION_Y, -100f)
        val backButtonAlphaAnim = ObjectAnimator.ofFloat(backButton, View.ALPHA, 0F)

        val backButtonAnimator = AnimatorSet().apply {
            duration = 500L
            interpolator = AccelerateDecelerateInterpolator()
            playTogether(backButtonTranslateAnim, backButtonAlphaAnim)
        }

        // animate views
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

        // play both animations
        AnimatorSet().apply {
            playTogether(*animatedViews.toTypedArray(), backButtonAnimator)
        }.start()
    }


}