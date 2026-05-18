package com.example.coursework_shev.database

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

import com.example.coursework_shev.util.DatabaseConstants.DATABASE_NAME
import com.example.coursework_shev.util.DatabaseConstants.TABLE_BOOK
import com.example.coursework_shev.util.DatabaseConstants.COLUMN_ID
import com.example.coursework_shev.util.DatabaseConstants.COLUMN_ISBN
import com.example.coursework_shev.util.DatabaseConstants.COLUMN_TITLE
import com.example.coursework_shev.util.DatabaseConstants.COLUMN_AUTHOR
import com.example.coursework_shev.util.DatabaseConstants.COLUMN_IMAGE_URL

class DatabaseHelper private constructor(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, 1) {

    companion object {
        @Volatile
        private var instance: DatabaseHelper? = null

        fun getInstance(context: Context): DatabaseHelper {
            return instance ?: synchronized(this) {
                instance ?: DatabaseHelper(context.applicationContext).also { instance = it }
            }
        }
    }

    override fun onCreate(db: SQLiteDatabase) {
        val createTableQuery = """
            CREATE TABLE $TABLE_BOOK (
                $COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_TITLE TEXT NOT NULL,
                $COLUMN_AUTHOR TEXT NOT NULL,
                $COLUMN_ISBN TEXT NOT NULL UNIQUE,
                $COLUMN_IMAGE_URL TEXT
            )
        """.trimIndent()

        db.execSQL(createTableQuery)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_BOOK")
        onCreate(db)
    }
}