package com.adasoraninda.dicodingstoryapp.features.story.list

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.adasoraninda.dicodingstoryapp.R
import com.adasoraninda.dicodingstoryapp.common.dialog.ProfileDialogFragment
import com.adasoraninda.dicodingstoryapp.databinding.FragmentListStoryBinding
import com.adasoraninda.dicodingstoryapp.utils.EMPTY_ERROR
import com.adasoraninda.dicodingstoryapp.utils.EMPTY_TOKEN_ERROR
import com.adasoraninda.dicodingstoryapp.utils.injector

class ListStoryFragment : Fragment() {

    private var _binding: FragmentListStoryBinding? = null
    private val binding get() = _binding

    private val viewModel by viewModels<ListStoryViewModel> { injector().listStoryFactory }

    private var profileDialog: ProfileDialogFragment? = null

    private val storiesAdapter by lazy {
        ListStoryAdapter { views, data ->
            val extras = FragmentNavigatorExtras(*views)
            val direction = ListStoryFragmentDirections.navListToDetailStory(data)
            findNavController().navigate(direction, extras)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentListStoryBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        requireActivity().onBackPressedDispatcher.addCallback {
            requireActivity().finishAffinity()
        }

        setupView()
        actionListeners()
        observeViewModel()
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    private fun setupView() {
        val view = binding ?: return

        view.toolbar.menu.findItem(R.id.action_profile).isEnabled = false

        view.listStories.adapter = storiesAdapter
        view.listStories.layoutManager = LinearLayoutManager(requireContext())
    }

    private fun actionListeners() {
        val view = binding ?: return

        view.swipeRefresh.setOnRefreshListener {
            viewModel.getStories()
        }

        view.buttonAddStory.setOnClickListener {
            findNavController().navigate(R.id.nav_list_to_add_story)
        }

        view.toolbar.setOnMenuItemClickListener { menu ->
            if (menu.itemId != R.id.action_profile) {
                return@setOnMenuItemClickListener false
            }

            viewModel.showProfileDialog()
            true
        }

        view.listStories.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)

                if ((newState == RecyclerView.SCROLL_STATE_DRAGGING || newState == RecyclerView.SCROLL_STATE_SETTLING)
                    && newState != RecyclerView.SCROLL_STATE_IDLE
                ) {
                    view.buttonAddStory.hide()
                } else {
                    view.buttonAddStory.show()
                }
            }
        })
    }

    private fun observeViewModel() {
        viewModel.loading.observe(viewLifecycleOwner) { loading ->
            binding?.progressBar?.isVisible = loading
        }

        viewModel.enableMenu.observe(viewLifecycleOwner) {
            val menu = binding?.toolbar?.menu?.findItem(R.id.action_profile)
            menu?.isEnabled = it
        }

        viewModel.storiesData.observe(viewLifecycleOwner) {
            binding?.swipeRefresh?.isRefreshing = false
            binding?.textError?.isVisible = false
            binding?.listStories?.isVisible = it.isNotEmpty()

            storiesAdapter.submitList(it)
        }

        viewModel.errorMessage.observe(viewLifecycleOwner) { error ->
            binding?.swipeRefresh?.isRefreshing = false

            if (error == null) return@observe

            val message = when (error) {
                EMPTY_ERROR -> {
                    getString(R.string.error_occurred)
                }
                EMPTY_TOKEN_ERROR -> {
                    getString(R.string.error_token)
                }
                else -> {
                    error
                }
            }

            binding?.listStories?.isVisible = false
            binding?.textError?.apply {
                isVisible = error.isNotEmpty()
                text = message
            }
        }

        viewModel.profileDialog.observe(viewLifecycleOwner) { event ->
            val user = event?.get() ?: return@observe

            val f = parentFragmentManager.findFragmentByTag(ProfileDialogFragment.TAG)
            if (f != null) (f as ProfileDialogFragment).dismiss()

            profileDialog = ProfileDialogFragment(
                userId = user.userId,
                name = user.name,
            ) {
                viewModel.logout()
                findNavController().navigate(R.id.nav_list_to_auth)
            }

            profileDialog?.show(parentFragmentManager, ProfileDialogFragment.TAG)
        }

    }

}