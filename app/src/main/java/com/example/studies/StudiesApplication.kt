package com.example.studies

import android.app.Application
import android.util.Log
import com.example.studies.data.AppDatabase
import com.example.studies.data.repository.AppRepository

class StudiesApplication : Application() {

    val database by lazy { AppDatabase.getDatabase(this) }
    val repository by lazy {
        AppRepository(
            database.disciplineDao(),
            database.taskDao(),
            database.materialLinkDao()
        )
    }

    override fun onCreate() {
        super.onCreate()
        Log.d("StudiesApplication", "StudiesApplication onCreate called.")
    }
}