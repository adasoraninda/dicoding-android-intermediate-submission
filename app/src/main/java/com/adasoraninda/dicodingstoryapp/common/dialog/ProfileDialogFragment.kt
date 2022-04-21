package com.adasoraninda.dicodingstoryapp.common.dialog


import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import com.adasoraninda.dicodingstoryapp.databinding.FragmentDialogProfileBinding

class ProfileDialogFragment(
    private var userId: String? = null,
    private var name: String? = null,
    private val logoutClicked: (() -> Unit)? = null
) : DialogFragment() {

    private var _binding: FragmentDialogProfileBinding? = null
    private val binding get() = _binding

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        _binding = FragmentDialogProfileBinding.inflate(layoutInflater)

        setupView()
        actionListeners()

        return AlertDialog.Builder(requireContext())
            .setView(binding?.root)
            .create()
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    private fun setupView() {
        val view = binding ?: return

        view.textUserId.text = userId
        view.textName.text = name
    }

    private fun actionListeners() {
        val view = binding ?: return

        view.buttonLogout.setOnClickListener {
            logoutClicked?.invoke()
            dismiss()
        }
    }

    companion object {
        const val TAG = "ProfileDialogFragment"
    }

}