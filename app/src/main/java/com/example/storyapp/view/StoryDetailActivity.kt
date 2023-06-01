package com.example.storyapp.view

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import com.bumptech.glide.Glide
import com.example.storyapp.databinding.ActivityStoryDetailBinding
import com.example.storyapp.model_responses.Story
import java.text.SimpleDateFormat
import java.util.*

class StoryDetailActivity : AppCompatActivity() {
    private val detailStory = "DETAIL_STORY"
    private lateinit var binding: ActivityStoryDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStoryDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val detailStory = intent.getParcelableExtra<Story>(detailStory) as Story

        setup(detailStory)
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        finish()
        return true
    }

    @SuppressLint("SimpleDateFormat")
    fun String.withDateFormat(): String {
        val formatter = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")
        formatter.timeZone = TimeZone.getTimeZone("UTC")
        val value = formatter.parse(this) as Date
        val dateFormatter = SimpleDateFormat("dd-MMM-yyyy")
        dateFormatter.timeZone = TimeZone.getDefault()
        return dateFormatter.format(value)
    }

    private fun setup(detailStory: Story) {
        Glide.with(this@StoryDetailActivity)
            .load(detailStory.photoUrl)
            .fitCenter()
            .into(binding.storyImageView)

        detailStory.apply {
            binding.tvUser.text = name
            binding.description.text = description
            binding.tvDate.text = createdAt.withDateFormat()
        }
    }
}