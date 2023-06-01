package com.example.storyapp.data

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import com.example.storyapp.api.ApiService
import com.example.storyapp.model_responses.Story
import com.example.storyapp.preference.PreferenceLogin

@OptIn(ExperimentalPagingApi::class)
class StoryRemoteMediator(
    private val pref: PreferenceLogin,
    private val apiService: ApiService
) : RemoteMediator<Int, Story>() {

    override suspend fun initialize(): InitializeAction {
        return InitializeAction.LAUNCH_INITIAL_REFRESH
    }

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, Story>
    ): MediatorResult {
        val page = when (loadType) {
            LoadType.REFRESH -> INITIAL_PAGE_INDEX
            LoadType.PREPEND -> {
                val anchorPosition = state.anchorPosition ?: return MediatorResult.Success(endOfPaginationReached = true)
                val previousKey = state.closestPageToPosition(anchorPosition)?.prevKey
                previousKey ?: return MediatorResult.Success(endOfPaginationReached = true)
            }
            LoadType.APPEND -> {
                val anchorPosition = state.anchorPosition ?: return MediatorResult.Success(endOfPaginationReached = true)
                val nextKey = state.closestPageToPosition(anchorPosition)?.nextKey
                nextKey ?: return MediatorResult.Success(endOfPaginationReached = true)
            }
        }


        val token = pref.getUser().token.toString()

        return try {
            val responseData = token.let { apiService.getStories(
                "Bearer $it",
                page,
                state.config.pageSize,
                0
            ) }

            if (responseData.isSuccessful) {
                val endOfPaginationReached = responseData.body()?.listStory?.isEmpty() == true

                MediatorResult.Success(endOfPaginationReached = endOfPaginationReached)
            } else {
                MediatorResult.Error(Exception("Failed load story"))
            }
        } catch (exception: Exception) {
            MediatorResult.Error(exception)
        }
    }

    private companion object {
        const val INITIAL_PAGE_INDEX = 1
    }
}