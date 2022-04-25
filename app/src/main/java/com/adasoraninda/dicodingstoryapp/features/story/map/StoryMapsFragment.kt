package com.adasoraninda.dicodingstoryapp.features.story.map

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.adasoraninda.dicodingstoryapp.R
import com.adasoraninda.dicodingstoryapp.common.dialog.StoryLocationDialogFragment
import com.adasoraninda.dicodingstoryapp.databinding.FragmentStoryMapsBinding
import com.adasoraninda.dicodingstoryapp.model.Story
import com.adasoraninda.dicodingstoryapp.utils.ERROR_EMPTY
import com.adasoraninda.dicodingstoryapp.utils.SUCCESS_EMPTY
import com.adasoraninda.dicodingstoryapp.utils.injector
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.snackbar.Snackbar
import timber.log.Timber

class StoryMapsFragment : Fragment(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap

    private var _binding: FragmentStoryMapsBinding? = null
    private val binding get() = _binding

    private var snackbar: Snackbar? = null
    private var storyDialog: StoryLocationDialogFragment? = null

    private val viewModel by viewModels<StoryMapsViewModel> {
        injector().storyMapsFactory
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentStoryMapsBinding.inflate(layoutInflater)
        binding?.mapView?.onCreate(savedInstanceState)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        requireActivity().onBackPressedDispatcher.addCallback {
            findNavController().popBackStack()
        }

        if (savedInstanceState == null && viewModel.initialize.not()) {
            viewModel.initialize()
        }

        binding?.textDataLoaded?.text = getString(R.string.data_loaded, 0)
        binding?.mapView?.getMapAsync(this)

    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        binding?.mapView?.onSaveInstanceState(outState)
    }

    override fun onStart() {
        super.onStart()
        binding?.mapView?.onStart()
    }

    override fun onPause() {
        super.onPause()
        binding?.mapView?.onPause()
    }

    override fun onResume() {
        super.onResume()
        binding?.mapView?.onResume()
    }

    override fun onStop() {
        super.onStop()
        binding?.mapView?.onStop()
    }

    override fun onDestroyView() {
        snackbar?.dismiss()
        binding?.mapView?.onDestroy()
        _binding = null
        super.onDestroyView()
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        setupMap()
        actionListeners()
        observeViewModel()
    }

    private fun setupMap() {
        mMap.uiSettings.isZoomControlsEnabled = true
        mMap.uiSettings.isIndoorLevelPickerEnabled = true

        val indonesia = LatLng(-0.789275, 113.921327)
        mMap.moveCamera(CameraUpdateFactory.newLatLng(indonesia))
    }

    private fun createMarker(story: Story) {
        mMap.addMarker(
            MarkerOptions()
                .title(story.name)
                .snippet(story.description)
                .position(LatLng(story.latitude.toDouble(), story.longitude.toDouble()))
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN))
        )?.tag = story
    }

    private fun actionListeners() {
        mMap.setOnMarkerClickListener {
            it.showInfoWindow()
            true
        }

        mMap.setOnInfoWindowClickListener {
            Timber.d(it.tag.toString())
            val directions = StoryMapsFragmentDirections.navMapToDetailStory(it.tag as Story)
            findNavController().navigate(directions)
        }

        binding?.textDataLoaded?.setOnClickListener {
            viewModel.showDialog()
        }

        binding?.imageBackButton?.setOnClickListener {
            requireActivity().onBackPressed()
        }

        binding?.imageLoadMore?.setOnClickListener {
            viewModel.getAllStories()
        }
    }

    private fun observeViewModel() {
        viewModel.errorMessage.observe(viewLifecycleOwner) { event ->
            var message = event.get() ?: return@observe

            if (message == SUCCESS_EMPTY) {
                message = getString(R.string.success_empty)
            }

            if (message == ERROR_EMPTY) {
                message = getString(R.string.error_occurred)
            }

            snackbar = binding?.root?.let { Snackbar.make(it, message, Snackbar.LENGTH_SHORT) }
            snackbar?.show()
        }

        viewModel.storiesData.observe(viewLifecycleOwner) { stories ->
            mMap.clear()

            val size =
                if (stories.isNotEmpty()) stories.size
                else 0

            Timber.d(size.toString())
            binding?.textDataLoaded?.text = getString(R.string.data_loaded, size)

            stories.forEach(this::createMarker)

            storyDialog =
                StoryLocationDialogFragment(
                    stories,
                    onCancel = { viewModel.dismissDialog() }) {
                    viewModel.dismissDialog()
                    viewModel.setCameraFocus(
                        it.latitude.toDouble(),
                        it.longitude.toDouble()
                    )
                }
        }

        viewModel.showDialog.observe(viewLifecycleOwner) { show ->
            (parentFragmentManager
                .findFragmentByTag(StoryLocationDialogFragment.TAG) as? StoryLocationDialogFragment)
                ?.dismiss()

            if (!show) return@observe

            storyDialog?.show(parentFragmentManager, StoryLocationDialogFragment.TAG)
        }

        viewModel.cameraFocus.observe(viewLifecycleOwner) { latLong ->
            val latLng = LatLng(latLong[0], latLong[1])
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15F))
        }

        viewModel.loading.observe(viewLifecycleOwner) { loading ->
            binding?.textDataLoaded?.isEnabled = !loading
            binding?.imageLoadMore?.isEnabled = !loading
            binding?.progressBar?.isVisible = loading
        }
    }
}