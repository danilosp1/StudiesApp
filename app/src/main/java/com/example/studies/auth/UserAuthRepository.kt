package com.example.studies.auth

import android.content.Context
import android.util.Log
import com.example.studies.R
import com.example.studies.data.model.User
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.File
import java.io.FileReader
import java.io.FileWriter
import java.io.IOException

class UserAuthRepository(private val context: Context) {

    private val gson = Gson()
    private val usersFileName = "studies_users.json"

    private fun getUsersFile(): File {
        return File(context.filesDir, usersFileName)
    }

    private fun readUsersFromFile(): MutableList<User> {
        val file = getUsersFile()
        if (!file.exists()) {
            Log.d("UserAuthRepository", context.getString(R.string.log_user_auth_file_not_found_creating_new, usersFileName))
            return mutableListOf()
        }
        return try {
            FileReader(file).use { reader ->
                val userListType = object : TypeToken<MutableList<User>>() {}.type
                gson.fromJson(reader, userListType) ?: mutableListOf()
            }
        } catch (e: IOException) {
            Log.e("UserAuthRepository", context.getString(R.string.log_user_auth_error_reading_file, usersFileName), e)
            mutableListOf()
        } catch (e: Exception) {
            Log.e("UserAuthRepository", context.getString(R.string.log_user_auth_error_parsing_file, usersFileName), e)
            file.delete()
            mutableListOf()
        }
    }

    private fun writeUsersToFile(users: List<User>) {
        try {
            FileWriter(getUsersFile()).use { writer ->
                gson.toJson(users, writer)
                Log.d("UserAuthRepository", context.getString(R.string.log_user_auth_users_saved, usersFileName))
            }
        } catch (e: IOException) {
            Log.e("UserAuthRepository", context.getString(R.string.log_user_auth_error_writing_file, usersFileName), e)
        }
    }

    fun registerUser(username: String, password: String): Boolean {
        if (username.isBlank() || password.isBlank()) {
            Log.w("UserAuthRepository", context.getString(R.string.log_user_auth_reg_empty_fields))
            return false
        }

        val users = readUsersFromFile()
        if (users.any { it.username.equals(username, ignoreCase = true) }) {
            Log.w("UserAuthRepository", context.getString(R.string.log_user_auth_reg_user_exists, username))
            return false
        }

        val passwordHash = PasswordHasher.hashPassword(password)
        users.add(User(username, passwordHash))
        writeUsersToFile(users)
        Log.i("UserAuthRepository", context.getString(R.string.log_user_auth_reg_success, username))
        return true
    }

    fun loginUser(username: String, password: String): Boolean {
        if (username.isBlank() || password.isBlank()) {
            Log.w("UserAuthRepository", context.getString(R.string.log_user_auth_login_empty_fields))
            return false
        }

        val users = readUsersFromFile()
        val user = users.find { it.username.equals(username, ignoreCase = true) }

        user?.let {
            val isPasswordCorrect = it.passwordHash == PasswordHasher.hashPassword(password)
            if (isPasswordCorrect) {
                Log.i("UserAuthRepository", context.getString(R.string.log_user_auth_login_success, username))
            } else {
                Log.w("UserAuthRepository", context.getString(R.string.log_user_auth_login_wrong_password, username))
            }
            return isPasswordCorrect
        }
        Log.w("UserAuthRepository", context.getString(R.string.log_user_auth_login_user_not_found, username))
        return false
    }
}