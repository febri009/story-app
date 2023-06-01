package com.example.storyapp.data

sealed class Result<out R> private constructor() {
    data class Berhasil<out T>(val data: T) : Result<T>()
    data class Eror(val data: String) : Result<Nothing>()
    object Loading : Result<Nothing>()
}