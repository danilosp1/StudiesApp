package com.example.studies.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.studies.data.dao.DisciplineDao
import com.example.studies.data.dao.TaskDao
import com.example.studies.data.model.DisciplineEntity
import com.example.studies.data.model.SubjectScheduleEntity
import com.example.studies.data.model.TaskEntity

@Database(
    entities = [
        DisciplineEntity::class,
        SubjectScheduleEntity::class,
        TaskEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun disciplineDao(): DisciplineDao
    abstract fun taskDao(): TaskDao

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
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}