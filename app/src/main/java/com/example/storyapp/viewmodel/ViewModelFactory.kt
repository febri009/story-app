package com.example.storyapp.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.storyapp.api.ApiConfig
import com.example.storyapp.data.StoryRepository
import com.example.storyapp.preference.PreferenceLogin
import com.example.storyapp.viewmodel.*

class ViewModelFactory private constructor(private val repo: StoryRepository) : ViewModelProvider.NewInstanceFactory() {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(StoriesViewModel::class.java) -> StoriesViewModel(repo) as T
            modelClass.isAssignableFrom(LoginViewModel::class.java) -> LoginViewModel(repo) as T
            modelClass.isAssignableFrom(RegisterViewModel::class.java) -> RegisterViewModel(repo) as T
            modelClass.isAssignableFrom(CreateNewStoryViewModel::class.java) -> CreateNewStoryViewModel(repo) as T
            modelClass.isAssignableFrom(MapViewModel::class.java) -> MapViewModel(repo) as T
            else -> throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
        }
    }

    companion object {
        private val apiService = ApiConfig.getApiService()

        private fun provideRepository(context: Context): StoryRepository {
            val preferences = PreferenceLogin(context.applicationContext)
            return StoryRepository.getInstance(preferences, apiService)
        }

        private var instance: ViewModelFactory? = null
        fun getInstance(context: Context): ViewModelFactory {
            return instance ?: synchronized(this) {
                instance ?: ViewModelFactory(provideRepository(context))
            }.also { instance = it }
        }
    }
}

