package by.squareroot.windowcontroller.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(entities = arrayOf(WindowBlind::class), version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun windowDao(): WindowBlindDao
}