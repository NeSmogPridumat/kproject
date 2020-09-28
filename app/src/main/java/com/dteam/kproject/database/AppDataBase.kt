package com.dteam.kproject.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.dteam.kproject.data.MyTimetable

@Database(
    entities = [MyTimetable::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(TimeQueueConverter::class)

abstract class AppDataBase : RoomDatabase() {
    abstract fun myTimetableDao(): MyTimetableDao

    companion object {
        private var INSTANCE: AppDataBase? = null

        fun getAppDataBase(context: Context): AppDataBase? {
            if (INSTANCE == null) {
                synchronized(AppDataBase::class) {
                    INSTANCE = Room.databaseBuilder(
                        context.applicationContext,
                        AppDataBase::class.java, "myDB"
                    )
                        .fallbackToDestructiveMigration()
                        .build()
                }
            }
            return INSTANCE
        }

        fun destroyDataBase() {
            INSTANCE = null
        }
    }

}