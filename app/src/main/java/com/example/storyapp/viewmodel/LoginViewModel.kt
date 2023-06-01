package com.example.storyapp.viewmodel

import androidx.lifecycle.ViewModel
import com.example.storyapp.data.StoryRepository

class LoginViewModel(private val storyRepository: StoryRepository): ViewModel() {
    fun postLogin(email: String, password: String) = storyRepository.login(email, password)
}