package com.example.storyapp.adapter

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.app.ActivityOptionsCompat
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.storyapp.databinding.ItemStoryBinding
import com.example.storyapp.model_responses.Story
import com.example.storyapp.view.StoryDetailActivity
import java.text.SimpleDateFormat
import java.util.*

class ListStoryAdapter :
    PagingDataAdapter<Story, ListStoryAdapter.ListViewHolder>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        val binding = ItemStoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ListViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        val data = getItem(position)
        data?.let {
            holder.bind(it)
        }
    }


    inner class ListViewHolder(private val binding: ItemStoryBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(story: Story) {
            with(binding) {
                tvUser.text = story.name
                tvDate.text = story.createdAt.withDateFormat()
                description.text = story.description
                Glide.with(itemView.context)
                    .load(story.photoUrl)
                    .fitCenter()
                    .into(storyImageView)

                storyItem.setOnClickListener {
                    val intent = Intent(itemView.context, StoryDetailActivity::class.java)
                    intent.putExtra(DETAIL_STORY, story)
                    itemView.context.startActivity(
                        intent,
                        ActivityOptionsCompat.makeSceneTransitionAnimation(binding.root.context as Activity).toBundle()
                    )
                }
            }
        }
    }

    companion object {
        private const val DETAIL_STORY = "DETAIL_STORY"

        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Story>() {
            override fun areItemsTheSame(oldItem: Story, newItem: Story): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: Story, newItem: Story): Boolean {
                return oldItem.hashCode() == newItem.hashCode()
            }
        }

        @SuppressLint("SimpleDateFormat")
        fun String.withDateFormat(): String {
            val format = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")
            format.timeZone = TimeZone.getTimeZone("UTC")
            val value = format.parse(this) as Date
            val dateFormat = SimpleDateFormat("dd-MMM-yyyy")
            dateFormat.timeZone = TimeZone.getDefault()
            return dateFormat.format(value)
        }
    }
}

