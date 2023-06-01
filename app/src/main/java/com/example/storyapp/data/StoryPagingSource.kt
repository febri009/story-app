package com.example.storyapp.data

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.storyapp.api.ApiService
import com.example.storyapp.model_responses.Story
import com.example.storyapp.preference.PreferenceLogin

class StoryPagingSource(
    private val pref: PreferenceLogin,
    private val apiService: ApiService
) : PagingSource<Int, Story>() {
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Story> {
        return try {
            val page = params.key ?: INITIAL_PAGE_INDEX
            val token = pref.getUser().token.toString()

            if (token.isNotEmpty()) {
                val dataResponse =
                    token.let { apiService.getStories("Bearer $it", page, params.loadSize, 0) }
                if (dataResponse.isSuccessful) {
                    val story = dataResponse.body()?.listStory ?: emptyList()
                    val keyPrev = if (page == INITIAL_PAGE_INDEX) null else page - 1
                    val keyNext = if (story.isEmpty()) null else page + 1
                    LoadResult.Page(data = story, prevKey = keyPrev, nextKey = keyNext)
                } else {
                    LoadResult.Error(Exception("Gagal memuat story"))
                }
            } else {
                LoadResult.Error(Exception("Tidak ada token "))
            }
        } catch (exception: Exception) {
            LoadResult.Error(exception)
        }
    }
    override fun getRefreshKey(state: PagingState<Int, Story>): Int? {
        val anchorPosition = state.anchorPosition ?: return null
        val anchorPageData = state.closestPageToPosition(anchorPosition) ?: return null
        return anchorPageData.prevKey?.plus(1) ?: anchorPageData.nextKey?.minus(1)
    }

    private companion object {
        const val INITIAL_PAGE_INDEX = 1
    }
}