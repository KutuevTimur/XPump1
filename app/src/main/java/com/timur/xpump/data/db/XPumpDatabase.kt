package com.timur.xpump.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.timur.xpump.data.db.dao.WorkoutDao
import com.timur.xpump.data.db.entities.WorkoutEntity
import com.timur.xpump.data.db.entities.WorkoutSetEntity

@Database(
    entities = [WorkoutEntity::class, WorkoutSetEntity::class],
    version = 1,
    exportSchema = false
)
abstract class XPumpDatabase : RoomDatabase() {

    abstract fun workoutDao(): WorkoutDao

    companion object {
        @Volatile
        private var INSTANCE: XPumpDatabase? = null

        fun getDatabase(context: Context): XPumpDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    XPumpDatabase::class.java,
                    "xpump_database"
                )
                    .fallbackToDestructiveMigration() // Удалит данные при смене версии (для разработки ок) [cite: 404]
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}