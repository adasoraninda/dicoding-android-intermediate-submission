package com.adasoraninda.dicodingstoryapp.features.story.list

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.adasoraninda.dicodingstoryapp.databinding.ItemStoryBinding
import com.adasoraninda.dicodingstoryapp.model.Story
import com.adasoraninda.dicodingstoryapp.utils.formatDate
import com.adasoraninda.dicodingstoryapp.utils.loadImage
import timber.log.Timber

private typealias ItemClickListener = ((Array<Pair<View, String>>, Story) -> Unit)

class ListStoryAdapter(
    private val clickListener: ItemClickListener? = null
) : ListAdapter<Story, ListStoryAdapter.StoryViewHolder>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StoryViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemStoryBinding.inflate(inflater, parent, false)
        return StoryViewHolder(binding, clickListener)
    }

    override fun onBindViewHolder(holder: StoryViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class StoryViewHolder(
        private val binding: ItemStoryBinding,
        private val clickListener: ItemClickListener? = null
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(data: Story) {
            Timber.d(data.toString())
            binding.textDate.text = data.createdAt.formatDate()
            binding.textName.text = data.name
            binding.imageStory.loadImage(data.photoUrl)

            val views = arrayOf<Pair<View,String>>(
                binding.imageStory to binding.imageStory.transitionName,
                binding.textName to binding.textName.transitionName,
                binding.textDate to binding.textDate.transitionName,
            )
            binding.root.setOnClickListener {
                clickListener?.invoke(
                    views, data
                )
            }
        }

    }

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Story>() {
            override fun areItemsTheSame(oldItem: Story, newItem: Story): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: Story, newItem: Story): Boolean {
                return oldItem == newItem
            }
        }
    }
}