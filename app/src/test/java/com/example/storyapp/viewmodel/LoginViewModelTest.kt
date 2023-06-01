package com.example.storyapp.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import com.example.storyapp.data.Result
import com.example.storyapp.DataDummy
import com.example.storyapp.data.StoryRepository
import com.example.storyapp.getOrAwaitValue
import com.example.storyapp.model_responses.LoginResponse
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
class LoginViewModelTest{
    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @Mock
    private lateinit var storyRepository: StoryRepository
    private lateinit var loginViewModel: LoginViewModel
    private var dummyLoginResponse = DataDummy.dummyLoginSuccess()
    private val dummyEmail = "email"
    private val dummyPassword = "password"

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        loginViewModel = LoginViewModel(storyRepository)
    }

    @Test
    fun `saat Post Login Seharusnya Tidak Null dan Return Berhasil`() {
        val expectedLogin = MutableLiveData<Result<LoginResponse>>()
        expectedLogin.value = Result.Berhasil(dummyLoginResponse)
        `when`(storyRepository.login(dummyEmail, dummyPassword)).thenReturn(expectedLogin)

        val actualResponse = loginViewModel.postLogin(dummyEmail, dummyPassword).getOrAwaitValue()

        verify(storyRepository).login(dummyEmail, dummyPassword)
        assertNotNull(actualResponse)
        assertTrue(actualResponse is Result.Berhasil)
    }

    @Test
    fun `saat Post Login Seharusnya Null dan Return Error`() {
        dummyLoginResponse = DataDummy.dummyLoginError()

        val expectedLogin = MutableLiveData<Result<LoginResponse>>()
        expectedLogin.value = Result.Eror("invalid password")
        `when`(storyRepository.login(dummyEmail, dummyPassword)).thenReturn(expectedLogin)

        val actualResponse = loginViewModel.postLogin(dummyEmail, dummyPassword).getOrAwaitValue()

        verify(storyRepository).login(dummyEmail, dummyPassword)
        assertNotNull(actualResponse)
        assertTrue(actualResponse is Result.Eror)
    }
}