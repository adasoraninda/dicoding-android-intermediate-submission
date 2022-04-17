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



        if (savedInstanceState != null) {
            userId = savedInstanceState.getString(USER_ID_KEY)
            name = savedInstanceState.getString(NAME_KEY)
        }

        setupView()
        actionListeners()

        return AlertDialog.Builder(requireContext())
            .setView(binding?.root)
            .create()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(NAME_KEY, name)
        outState.putString(USER_ID_KEY, userId)
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
        private const val USER_ID_KEY = "id"
        private const val NAME_KEY = "name"
    }

}