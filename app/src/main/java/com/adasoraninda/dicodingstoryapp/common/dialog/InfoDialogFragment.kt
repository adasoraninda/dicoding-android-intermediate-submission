package com.adasoraninda.dicodingstoryapp.common.dialog

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.view.View
import androidx.fragment.app.DialogFragment
import com.adasoraninda.dicodingstoryapp.databinding.FragmentDialogInfoBinding

private typealias onClickListener = () -> Unit

class InfoDialogFragment(
    private val message: String? = null,
    private val acceptText: String? = null,
    private val cancelText: String? = null,
    private val onAcceptClicked: onClickListener? = null,
    private val onCancelClicked: onClickListener? = null
) : DialogFragment() {

    private var _binding: FragmentDialogInfoBinding? = null
    private val binding get() = _binding

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        _binding = FragmentDialogInfoBinding.inflate(layoutInflater)

        setupView()
        actionListeners()

        if (acceptText.isNullOrEmpty()) removeAcceptButton()
        if (cancelText.isNullOrEmpty()) removeCancelButton()

        return AlertDialog.Builder(requireContext())
            .setView(binding?.root)
            .create()
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    private fun removeCancelButton() {
        binding?.buttonCancel?.visibility = View.GONE
    }

    private fun removeAcceptButton() {
        binding?.buttonAccept?.visibility = View.GONE
    }

    private fun setupView() {
        val view = binding ?: return

        view.textInfo.text = message
        view.buttonAccept.text = acceptText
        view.buttonCancel.text = cancelText
    }

    private fun actionListeners() {
        val view = binding ?: return

        view.buttonAccept.setOnClickListener {
            onAcceptClicked?.invoke()
            dismiss()
        }

        view.buttonCancel.setOnClickListener {
            onCancelClicked?.invoke()
            dismiss()
        }
    }

    companion object {
        const val TAG = "DialogInfoFragment"
    }
}