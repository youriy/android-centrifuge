package com.app.monitor.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.app.monitor.db.log.Log
import com.app.monitor.db.log.LogDao
import com.app.monitor.db.monitor.Monitor
import com.app.monitor.db.monitor.MonitorDao

@Database(entities = [Monitor::class, Log::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun monitorDao(): MonitorDao
    abstract fun logDao(): LogDao

    companion object {
        private var INSTANCE:  AppDatabase? = null

        @JvmStatic
        fun getAppDatabase(context: Context): AppDatabase? {
            if (INSTANCE == null) {
                INSTANCE = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "monitorV7"
                ).allowMainThreadQueries().build()
            }
            return INSTANCE
        }

        @JvmStatic
        fun destroyAppDatabase() {
            INSTANCE = null
        }
    }
}