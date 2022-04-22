package com.adasoraninda.dicodingstoryapp.features.story.add

import android.Manifest
import android.app.Activity.RESULT_OK
import android.content.Intent
import android.content.Intent.ACTION_GET_CONTENT
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.addCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.adasoraninda.dicodingstoryapp.R
import com.adasoraninda.dicodingstoryapp.common.dialog.AddPhotoDialogFragment
import com.adasoraninda.dicodingstoryapp.common.dialog.InfoDialogFragment
import com.adasoraninda.dicodingstoryapp.databinding.FragmentAddStoryBinding
import com.adasoraninda.dicodingstoryapp.utils.*
import com.google.android.material.snackbar.Snackbar
import timber.log.Timber
import java.io.File

class AddStoryFragment : Fragment() {


    private var _binding: FragmentAddStoryBinding? = null
    private val binding get() = _binding

    private var addPhotoDialog: AddPhotoDialogFragment? = null

    private val viewModel by viewModels<AddStoryViewModel> { injector().addStoryFactory }

    private var dialogInfo: InfoDialogFragment? = null
    private var snackbar: Snackbar? = null

    private val requestCameraPermission = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        Timber.d("Camera permission: $isGranted")
        if (!isGranted) {
            Toast.makeText(
                requireContext(),
                getString(R.string.error_permission),
                Toast.LENGTH_SHORT
            ).show()
            findNavController().popBackStack()
        }
    }

    private val launcherIntentCameraX = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == CAMERA_X_RESULT) {
            val myFile = it.data?.getSerializableExtra(CameraActivity.EXTRA_PICTURE) as File

            val isBackCamera =
                it.data?.getBooleanExtra(CameraActivity.EXTRA_IS_BACK_CAMERA, true) as Boolean

            val result = rotateBitmap(
                BitmapFactory.decodeFile(myFile.path),
                isBackCamera
            )

            binding?.textInfoClickImage?.isVisible = false
            binding?.imageStory?.setImageBitmap(result)

            viewModel.setImageFile(reduceFileImageCameraX(myFile, isBackCamera))
        }
    }

    private val launcherIntentGallery = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val selectedImg: Uri = result.data?.data as Uri
            val myFile = uriToFile(selectedImg, requireContext())

            binding?.textInfoClickImage?.isVisible = false
            binding?.imageStory?.setImageURI(selectedImg)

            viewModel.setImageFile(reduceFileImage(myFile))
        }
    }

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

        requireActivity().onBackPressedDispatcher.addCallback {
            findNavController().popBackStack()
        }

        if (!allPermissionsGranted()) {
            requestCameraPermission.launch(Manifest.permission.CAMERA)
        }

        actionListeners()
        observeViewModel()
    }

    override fun onDestroyView() {
        snackbar?.dismiss()
        _binding = null
        super.onDestroyView()
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(requireContext(), it) == PackageManager.PERMISSION_GRANTED
    }

    private fun actionListeners() {
        val view = binding ?: return

        view.imageStory.setOnClickListener {
            it.hideKeyboard()
            addPhotoDialog = AddPhotoDialogFragment(
                optionCameraClicked = { startCameraX() },
                optionGalleryClicked = { startGallery() }
            )

            addPhotoDialog?.show(parentFragmentManager, AddPhotoDialogFragment.TAG)
        }

        view.imageBackButton.setOnClickListener {
            it.hideKeyboard()
            requireActivity().onBackPressed()
        }

        view.buttonCreateStory.setOnClickListener {
            it.hideKeyboard()
            val desc = view.inputDesc.text.toString().trim()

            viewModel.addStory(desc)
        }
    }

    private fun observeViewModel() {
        val view = binding ?: return

        viewModel.dialogInfoSuccess.observe(viewLifecycleOwner) {
            var message = it ?: return@observe

            if (message == SUCCESS_EMPTY) {
                message = getString(R.string.success_occurred)
            }

            val f = parentFragmentManager.findFragmentByTag(InfoDialogFragment.TAG)
            if (f != null) {
                (f as InfoDialogFragment).dismiss()
            }

            dialogInfo = InfoDialogFragment(
                message = message,
                acceptText = requireContext().getString(R.string.ok),
                onAcceptClicked = {
                    viewModel.dismissSuccessDialog()
                    findNavController().navigate(R.id.nav_add_to_list_story)
                }
            ).apply { isCancelable = false }

            dialogInfo?.show(parentFragmentManager, InfoDialogFragment.TAG)
        }

        viewModel.dialogInfoError.observe(viewLifecycleOwner) {
            var message = it ?: return@observe

            if (message == ERROR_EMPTY) {
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

    private fun startGallery() {
        val intent = Intent()
        intent.action = ACTION_GET_CONTENT
        intent.type = "image/*"
        val chooser = Intent.createChooser(intent, "")
        launcherIntentGallery.launch(chooser)
    }

    private fun startCameraX() {
        val intent = Intent(requireContext(), CameraActivity::class.java)
        launcherIntentCameraX.launch(intent)
    }

    companion object {
        const val CAMERA_X_RESULT = 200

        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
    }

}