package com.example.storyapp.preference

import android.content.Context
import com.example.storyapp.model_responses.LoginModel

class PreferenceLogin(context: Context) {

    companion object {
        private const val NAMA_PREF = "pref_login"
        private const val NAMA = "nama"
        private const val USER_ID = "userId"
        private const val TOKEN = "token"
    }

    private val preferences = context.getSharedPreferences(NAMA_PREF, Context.MODE_PRIVATE)

    fun setLogin(value: LoginModel) {
        val editor = preferences.edit()
        editor.putString(NAMA, value.name)
        editor.putString(USER_ID, value.userId)
        editor.putString(TOKEN, value.token)
        editor.apply()
    }

    fun getUser(): LoginModel {
        val nama = preferences.getString(NAMA, null)
        val idUser = preferences.getString(USER_ID, null)
        val token = preferences.getString(TOKEN, null)
        return LoginModel(idUser, nama, token)
    }

    fun removeUser() {
        val editor = preferences.edit().clear()
        editor.apply()
    }
}