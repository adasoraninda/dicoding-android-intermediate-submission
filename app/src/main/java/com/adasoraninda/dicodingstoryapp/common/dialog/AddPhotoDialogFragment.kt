package com.adasoraninda.dicodingstoryapp.common.dialog

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import com.adasoraninda.dicodingstoryapp.databinding.FragmentDialogAddPhotoBinding

class AddPhotoDialogFragment(
    private val optionCameraClicked: (() -> Unit)? = null,
    private val optionGalleryClicked: (() -> Unit)? = null
) : DialogFragment() {

    private var _binding: FragmentDialogAddPhotoBinding? = null
    private val binding get() = _binding

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        _binding = FragmentDialogAddPhotoBinding.inflate(layoutInflater)

        binding?.optionCamera?.setOnClickListener {
            optionCameraClicked?.invoke()
            dismiss()
        }

        binding?.optionGallery?.setOnClickListener {
            optionGalleryClicked?.invoke()
            dismiss()
        }

        return AlertDialog.Builder(requireContext())
            .setView(binding?.root)
            .create()
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    companion object {
        const val TAG = "AddPhotoDialogFragment"
    }

}