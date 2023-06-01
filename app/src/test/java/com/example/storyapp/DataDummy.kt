package com.example.storyapp

import com.example.storyapp.model_responses.*

object DataDummy {
    fun dummyLoginSuccess(): LoginResponse =
         LoginResponse(
            error = false,
            message = "success",
            loginResult = LoginResultResponse(
                userId = "userId",
                name = "name",
                token = "token"
            )
        )

    fun dummyLoginError(): LoginResponse =
        LoginResponse(
            error = true,
            message = "invalid password"
        )

    fun generateDummyRegisterSuccess(): RegisterResponse =
        RegisterResponse(
            error = false,
            message = "success"
        )

    fun dummyRegisterError(): RegisterResponse =
        RegisterResponse(
            error = true,
            message = "bad request"
        )

    fun dummyCreateStorySuccess(): CreateNewStoryResponse =
        CreateNewStoryResponse(
            error = false,
            message = "success"
        )

    fun dummyCreateStoryError(): CreateNewStoryResponse =
        CreateNewStoryResponse(
            error = true,
            message = "error"
        )

    fun dummyStory(): StoriesResponse =
        StoriesResponse(
            error = false,
            message = "success",
            listStory = arrayListOf(
                Story(
                    id = "id",
                    name = "name",
                    description = "description",
                    photoUrl = "photoUrl",
                    createdAt = "createdAt",
                    lat = 0.01,
                    lon = 0.01
                )
            )
        )

    fun errorDummyStory(): StoriesResponse =
        StoriesResponse(
            error = true,
            message = "error",
            listStory = arrayListOf(
                Story(
                    id = "id",
                    name = "name",
                    description = "description",
                    photoUrl = "photoUrl",
                    createdAt = "createdAt",
                    lat = 0.01,
                    lon = 0.01
                )
            )
        )

}