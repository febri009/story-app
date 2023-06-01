package com.example.storyapp.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import com.example.storyapp.data.Result
import com.example.storyapp.DataDummy
import com.example.storyapp.data.StoryRepository
import com.example.storyapp.getOrAwaitValue
import com.example.storyapp.model_responses.StoriesResponse
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class MapViewModelTest{
    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @Mock
    private lateinit var storyRepository: StoryRepository
    private lateinit var mapViewModel: MapViewModel
    private var dummyStory = DataDummy.dummyStory()

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        mapViewModel = MapViewModel(storyRepository)
    }

    @Test
    fun `saat Get Story Seharusnya Tidak Null dan Return Berhasil`() {
        val expectedResponse = MutableLiveData<Result<StoriesResponse>>()
        expectedResponse.value = Result.Berhasil(dummyStory)
        `when`(storyRepository.getStories()).thenReturn(expectedResponse)

        val actualResponse = mapViewModel.getStories().getOrAwaitValue()

        verify(storyRepository).getStories()
        assertNotNull(actualResponse)
        assertTrue(actualResponse is Result.Berhasil)
    }

    @Test
    fun `saat Get Story Seharusnya Null dan Return Error`() {
        dummyStory = DataDummy.errorDummyStory()

        val expectedResponse = MutableLiveData<Result<StoriesResponse>>()
        expectedResponse.value = Result.Eror("error")
        `when`(storyRepository.getStories()).thenReturn(expectedResponse)

        val actualResponse = mapViewModel.getStories().getOrAwaitValue()

        verify(storyRepository).getStories()
        assertNotNull(actualResponse)
        assertTrue(actualResponse is Result.Eror)
    }
}