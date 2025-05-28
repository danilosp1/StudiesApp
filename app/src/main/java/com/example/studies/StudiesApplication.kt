package com.example.studies

import android.app.Application
import android.util.Log
import com.example.studies.data.AppDatabase
import com.example.studies.data.repository.AppRepository

class StudiesApplication : Application() {

    // Usando 'by lazy' para que o database e o repository sejam criados apenas quando realmente necessários.
    // "this" aqui refere-se ao ApplicationContext, que é o que queremos para o Room :)
    val database by lazy { AppDatabase.getDatabase(this) }
    val repository by lazy {
        AppRepository(
            database.disciplineDao(),
            database.taskDao()
        )
    }

    override fun onCreate() {
        super.onCreate()
        Log.d("StudiesApplication", "StudiesApplication onCreate called.")
    }
}