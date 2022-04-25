package com.adasoraninda.dicodingstoryapp.common.dialog

import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.adasoraninda.dicodingstoryapp.common.adapter.StoryLocationAdapter
import com.adasoraninda.dicodingstoryapp.databinding.FragmentDialogStoryLocationBinding
import com.adasoraninda.dicodingstoryapp.model.Story
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class StoryLocationDialogFragment(
    private val data: (List<Story>)? = null,
    private val onCancel: (() -> Unit)? = null,
    private val onItemClick: ((Story) -> Unit)? = null
) : BottomSheetDialogFragment() {

    private var _binding: FragmentDialogStoryLocationBinding? = null
    private val binding get() = _binding

    private val storyLocationAdapter by lazy {
        StoryLocationAdapter {
            onItemClick?.invoke(it)
            dismiss()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentDialogStoryLocationBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupView()
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    override fun onCancel(dialog: DialogInterface) {
        super.onCancel(dialog)
        onCancel?.invoke()
    }

    private fun setupView() {
        val view = binding ?: return

        storyLocationAdapter.submitList(data)

        view.listStories.adapter = storyLocationAdapter
        view.listStories.layoutManager = LinearLayoutManager(requireContext())
        view.listStories.addItemDecoration(
            DividerItemDecoration(
                requireContext(),
                RecyclerView.VERTICAL
            )
        )
    }

    companion object {
        const val TAG = "StoryLocationDialogFragment"
    }

}