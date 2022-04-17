package com.adasoraninda.dicodingstoryapp.features.auth.login

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.DecelerateInterpolator
import androidx.activity.addCallback
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.adasoraninda.dicodingstoryapp.R
import com.adasoraninda.dicodingstoryapp.common.dialog.InfoDialogFragment
import com.adasoraninda.dicodingstoryapp.databinding.FragmentLoginBinding
import com.adasoraninda.dicodingstoryapp.utils.*
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.delay

class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding

    private val viewModel: LoginViewModel by viewModels {
        injector().loginFactory
    }

    private val listOfViews = mutableListOf<View>()
    private var snackbar: Snackbar? = null
    private var dialogInfo: InfoDialogFragment? = null

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
        observeViewModel()
    }

    override fun onStart() {
        super.onStart()
        startAnimation()
    }

    override fun onDestroyView() {
        snackbar?.dismiss()
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

    private fun observeViewModel() {
        val view = binding ?: return

        viewModel.loginSuccess.observe(viewLifecycleOwner) { event ->
            event.get() ?: return@observe
            findNavController().navigate(R.id.nav_login_to_story)
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
                    viewModel.dismissError()
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
            lifecycleScope.launchWhenResumed {
                endAnimation()
                delay(400L)
                findNavController().navigate(R.id.nav_login_to_register)
            }
        }

        view.buttonLogin.setOnClickListener {
            (it.rootView as ViewGroup).clearAllFocus()
            it.hideKeyboard()
            val email = view.inputEmail.text.toString().trim()
            val password = view.inputPassword.text.toString().trim()

            viewModel.login(email, password)
        }
    }

    private fun startAnimation() {
        val newListViews = mutableListOf(*listOfViews.toTypedArray())
        newListViews.forEach {
            it.alpha = 0F
            it.translationX = -400F
        }

        val animatedViews = newListViews.map { view ->
            val translateX = ObjectAnimator.ofFloat(view, View.TRANSLATION_X, 0F)
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