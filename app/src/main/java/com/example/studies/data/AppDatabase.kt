package com.example.studies.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.studies.data.dao.DisciplineDao
import com.example.studies.data.dao.MaterialLinkDao
import com.example.studies.data.dao.TaskDao
import com.example.studies.data.model.DisciplineEntity
import com.example.studies.data.model.MaterialLinkEntity
import com.example.studies.data.model.SubjectScheduleEntity
import com.example.studies.data.model.TaskEntity

@Database(
    entities = [
        DisciplineEntity::class,
        SubjectScheduleEntity::class,
        TaskEntity::class,
        MaterialLinkEntity::class
    ],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun disciplineDao(): DisciplineDao
    abstract fun taskDao(): TaskDao
    abstract fun materialLinkDao(): MaterialLinkDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "studies_app_database"
                )
                    .fallbackToDestructiveMigration(true)
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}