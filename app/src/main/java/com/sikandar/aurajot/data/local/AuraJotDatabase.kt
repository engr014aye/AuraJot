package com.sikandar.aurajot.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.sikandar.aurajot.data.local.dao.NoteDao
import com.sikandar.aurajot.data.local.dao.TaskDao
import com.sikandar.aurajot.data.local.entity.NoteEntity
import com.sikandar.aurajot.data.local.entity.TaskEntity

@Database(
    entities = [NoteEntity::class, TaskEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AuraJotDatabase : RoomDatabase() {
    abstract val noteDao: NoteDao
    abstract val taskDao: TaskDao

    companion object {
        const val DATABASE_NAME = "aurajot.db"

        @Volatile
        private var INSTANCE: AuraJotDatabase? = null

        fun getInstance(context: Context): AuraJotDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AuraJotDatabase::class.java,
                    DATABASE_NAME
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
