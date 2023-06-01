package com.example.storyapp.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import com.example.storyapp.data.Result
import com.example.storyapp.DataDummy
import com.example.storyapp.data.StoryRepository
import com.example.storyapp.getOrAwaitValue
import com.example.storyapp.model_responses.RegisterResponse
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
class RegisterViewModelTest{
    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @Mock
    private lateinit var storyRepository: StoryRepository
    private lateinit var registerViewModel: RegisterViewModel
    private var dummyRegisterResponse = DataDummy.generateDummyRegisterSuccess()

    private val dummyName = "name"
    private val dummyEmail = "email"
    private val dummyPassword = "password"

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        registerViewModel = RegisterViewModel(storyRepository)
    }

    @Test
    fun `saat Post Register Seharusnya Tidak Null dan Return Success`() {
        val expectedRegister = MutableLiveData<Result<RegisterResponse>>()
        expectedRegister.value = Result.Berhasil(dummyRegisterResponse)
        `when`(storyRepository.register(dummyName, dummyEmail, dummyPassword)).thenReturn(expectedRegister)

        val actualResponse = registerViewModel.postRegister(dummyName, dummyEmail, dummyPassword).getOrAwaitValue()

        verify(storyRepository).register(dummyName, dummyEmail, dummyPassword)
        assertNotNull(actualResponse)
        assertTrue(actualResponse is Result.Berhasil)
    }

    @Test
    fun `saat Post Register Seharusnya Null dan Return Error`() {
        dummyRegisterResponse = DataDummy.dummyRegisterError()

        val expectedRegister = MutableLiveData<Result<RegisterResponse>>()
        expectedRegister.value = Result.Eror("bad request")
        `when`(storyRepository.register(dummyName, dummyEmail, dummyPassword)).thenReturn(expectedRegister)

        val actualResponse = registerViewModel.postRegister(dummyName, dummyEmail, dummyPassword).getOrAwaitValue()

        verify(storyRepository).register(dummyName, dummyEmail, dummyPassword)
        assertNotNull(actualResponse)
        assertTrue(actualResponse is Result.Eror)
    }
}