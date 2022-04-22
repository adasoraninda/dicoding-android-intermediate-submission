package com.adasoraninda.dicodingstoryapp.common.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.adasoraninda.dicodingstoryapp.R
import com.adasoraninda.dicodingstoryapp.databinding.ItemStoryLocationBinding
import com.adasoraninda.dicodingstoryapp.model.Story

class StoryLocationAdapter(
    private val itemClickListener: ((Story) -> Unit)? = null,
) : ListAdapter<Story, StoryLocationAdapter.StoryLocationViewHolder>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StoryLocationViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemStoryLocationBinding.inflate(inflater, parent, false)
        return StoryLocationViewHolder(binding, itemClickListener)
    }

    override fun onBindViewHolder(holder: StoryLocationViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class StoryLocationViewHolder(
        private val binding: ItemStoryLocationBinding,
        private val itemClickListener: ((Story) -> Unit)? = null,
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(data: Story) {
            binding.textNo.text = binding.root.context.getString(
                R.string.format_number, bindingAdapterPosition.plus(1)
            )
            binding.textName.text = data.name
            binding.textLatLot.text = binding.root.context.getString(
                R.string.format_location,
                data.latitude,
                data.longitude
            )

            binding.root.setOnClickListener {
                itemClickListener?.invoke(data)
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