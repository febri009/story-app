package com.example.storyapp.viewmodel

import androidx.lifecycle.ViewModel
import com.example.storyapp.data.StoryRepository

class MapViewModel(private val storyRepository: StoryRepository) : ViewModel() {
    fun getStories() = storyRepository.getStories()
}