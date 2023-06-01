package com.example.storyapp.viewmodel

import androidx.lifecycle.ViewModel
import com.example.storyapp.data.StoryRepository
import okhttp3.MultipartBody
import okhttp3.RequestBody

class CreateNewStoryViewModel(private val storyRepository: StoryRepository): ViewModel() {
    fun postCreateStory(imageFile: MultipartBody.Part, desc: RequestBody) = storyRepository.createStory(imageFile, desc)
}