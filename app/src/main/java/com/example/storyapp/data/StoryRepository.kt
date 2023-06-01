package com.example.storyapp.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import androidx.paging.*
import com.example.storyapp.api.ApiService
import com.example.storyapp.model_responses.*
import com.example.storyapp.preference.PreferenceLogin
import okhttp3.MultipartBody
import okhttp3.RequestBody

class StoryRepository(private val pref: PreferenceLogin, private val apiService: ApiService) {

    fun getListStories(): LiveData<PagingData<Story>> {
        @OptIn(ExperimentalPagingApi::class)
        return Pager(
            config = PagingConfig(pageSize = 1),
            remoteMediator = StoryRemoteMediator(pref, apiService),
            pagingSourceFactory = {
                StoryPagingSource(pref, apiService)
            }
        ).liveData
    }

    fun login(email: String, password: String): LiveData<Result<LoginResponse>> = liveData { emit(Result.Loading)
        try {
            val response = apiService.login(email, password)
            if (response.error) {
                emit(Result.Eror(response.message))
            } else {
                emit(Result.Berhasil(response))
            }
        } catch (e: Exception) {
            emit(Result.Eror(e.message.toString()))
        }
    }

    fun register(name: String, email: String, password: String): LiveData<Result<RegisterResponse>> = liveData { emit(Result.Loading)
        try {
            val response = apiService.register(name, email, password)
            if (response.error) {
                emit(Result.Eror(response.message))
            } else {
                emit(Result.Berhasil(response))
            }
        } catch (e: Exception) {
            emit(Result.Eror(e.message.toString()))
        }
    }

    fun createStory(imageFile: MultipartBody.Part, desc: RequestBody): LiveData<Result<CreateNewStoryResponse>> = liveData { emit(Result.Loading)
        try {
            val response = apiService.createStory(token = "Bearer ${pref.getUser().token}", file = imageFile, description = desc)
            if (response.error) {
                emit(Result.Eror(response.message))
            } else {
                emit(Result.Berhasil(response))
            }
        } catch (e: Exception) {
            emit(Result.Eror(e.message.toString()))
        }
    }

    fun getStories(): LiveData<Result<StoriesResponse>> = liveData {
        emit(Result.Loading)
        try {
            val response = apiService.getStory(
                token = "Bearer ${pref.getUser().token}",
                page = 1,
                size = 100,
                location = 1,
            )
            if (response.error) {
                emit(Result.Eror(response.message))
            } else {
                emit(Result.Berhasil(response))
            }
        } catch (e: Exception) {
            emit(Result.Eror(e.message.toString()))
        }
    }

    companion object {
        @Volatile
        private var instance: StoryRepository? = null
        fun getInstance(preferences: PreferenceLogin, apiService: ApiService): StoryRepository =
            instance ?: synchronized(this) {
                instance ?: StoryRepository(preferences, apiService)
            }.also { instance = it }
    }
}


