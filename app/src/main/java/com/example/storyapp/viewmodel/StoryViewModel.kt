package com.example.storyapp.viewmodel

import androidx.lifecycle.*
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.storyapp.data.StoryRepository
import com.example.storyapp.model_responses.Story

class StoriesViewModel(repo: StoryRepository): ViewModel() {
    val getListStory: LiveData<PagingData<Story>> =
        repo.getListStories().cachedIn(viewModelScope)
}