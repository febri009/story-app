package com.example.storyapp.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import com.example.storyapp.DataDummy
import com.example.storyapp.data.Result
import com.example.storyapp.data.StoryRepository
import com.example.storyapp.getOrAwaitValue
import com.example.storyapp.model_responses.CreateNewStoryResponse
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
import org.mockito.junit.MockitoJUnitRunner
import java.io.File

@RunWith(MockitoJUnitRunner::class)
class CreateStoryViewModelTest {
    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @Mock
    private lateinit var storyRepository: StoryRepository
    private lateinit var createNewStoryViewModel: CreateNewStoryViewModel
    private var dummyResponse = DataDummy.dummyCreateStorySuccess()
    private var dummyDesc = "description".toRequestBody("text/plain".toMediaType())

    private val file: File = Mockito.mock(File::class.java)
    private val requestImageFile = file.asRequestBody("image/jpg".toMediaTypeOrNull())
    private val imageMultipart: MultipartBody.Part = MultipartBody.Part.createFormData(
        "photo",
        file.name,
        requestImageFile
    )

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        createNewStoryViewModel = CreateNewStoryViewModel(storyRepository)
    }

    @Test
    fun `saat Post Create Story Seharusnya Tidak Null dan Return Berhasil`() {
        val expectedCreateStory = MutableLiveData<Result<CreateNewStoryResponse>>()
        expectedCreateStory.value = Result.Berhasil(dummyResponse)
        `when`(storyRepository.createStory(imageFile = imageMultipart, desc = dummyDesc))
            .thenReturn(expectedCreateStory)

        val actualResponse =
            createNewStoryViewModel.postCreateStory(imageFile = imageMultipart, desc = dummyDesc)
                .getOrAwaitValue()

        verify(storyRepository).createStory(imageFile = imageMultipart, desc = dummyDesc)
        assertNotNull(actualResponse)
        assertTrue(actualResponse is Result.Berhasil)
    }

    @Test
    fun `saat Post Create Story Seharusnya Null dan Return Error`() {
        dummyResponse = DataDummy.dummyCreateStoryError()

        val expectedCreateStory = MutableLiveData<Result<CreateNewStoryResponse>>()
        expectedCreateStory.value = Result.Eror("error")
        `when`(storyRepository.createStory(imageFile = imageMultipart, desc = dummyDesc))
            .thenReturn(expectedCreateStory)

        val actualResponse =
            createNewStoryViewModel.postCreateStory(imageFile = imageMultipart, desc = dummyDesc)
                .getOrAwaitValue()

        verify(storyRepository).createStory(imageFile = imageMultipart, desc = dummyDesc)
        assertNotNull(actualResponse)
        assertTrue(actualResponse is Result.Eror)
    }
}