package com.example.coursework_shev.database

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteException
import android.widget.Toast
import com.example.coursework_shev.model.Book

import com.example.coursework_shev.util.DatabaseConstants.DATABASE_NAME
import com.example.coursework_shev.util.DatabaseConstants.TABLE_BOOK
import com.example.coursework_shev.util.DatabaseConstants.COLUMN_ID
import com.example.coursework_shev.util.DatabaseConstants.COLUMN_ISBN
import com.example.coursework_shev.util.DatabaseConstants.COLUMN_TITLE
import com.example.coursework_shev.util.DatabaseConstants.COLUMN_AUTHOR
import com.example.coursework_shev.util.DatabaseConstants.COLUMN_IMAGE_URL

class DatabaseQueryClass(private val context: Context) {
    private val dbHelper = DatabaseHelper.getInstance(context)

    fun insertBook(book: Book): Long {
        var id: Long = -1
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_TITLE, book.title)
            put(COLUMN_AUTHOR, book.author)
            put(COLUMN_ISBN, book.isbn)
            put(COLUMN_IMAGE_URL, book.imageUrl)
        }

        try {
            id = db.insertOrThrow(TABLE_BOOK, null, values)
        } catch (e: SQLiteException) {
            Toast.makeText(context, "Operation failed: ${e.message}", Toast.LENGTH_LONG).show()
        } finally {
            db.close()
        }
        return id
    }

    fun getAllBooks(): List<Book> {
        val bookList = mutableListOf<Book>()
        val db = dbHelper.readableDatabase
        var cursor: Cursor? = null

        try {
            cursor = db.query(TABLE_BOOK, null, null, null, null, null, null)
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    val id = cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_ID))
                    val title = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TITLE))
                    val author = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_AUTHOR))
                    val isbn = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ISBN))
                    val imageUrl = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_IMAGE_URL))

                    bookList.add(Book(id, title, author, isbn, imageUrl))
                } while (cursor.moveToNext())
            }
        } catch (e: Exception) {
            Toast.makeText(context, "Failed to load books", Toast.LENGTH_SHORT).show()
        } finally {
            cursor?.close()
            db.close()
        }
        return bookList
    }

    fun updateBook(book: Book): Long {
        var rowCount: Long = 0
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_TITLE, book.title)
            put(COLUMN_AUTHOR, book.author)
            put(COLUMN_ISBN, book.isbn)
            put(COLUMN_IMAGE_URL, book.imageUrl)
        }

        try {
            rowCount = db.update(TABLE_BOOK, values, "$COLUMN_ID = ?", arrayOf(book.id.toString())).toLong()
        } catch (e: SQLiteException) {
            Toast.makeText(context, e.message, Toast.LENGTH_LONG).show()
        } finally {
            db.close()
        }
        return rowCount
    }

    fun deleteBook(id: Long): Long {
        var deletedRowCount: Long = -1
        val db = dbHelper.writableDatabase
        try {
            deletedRowCount = db.delete(TABLE_BOOK, "$COLUMN_ID = ?", arrayOf(id.toString())).toLong()
        } catch (e: SQLiteException) {
            Toast.makeText(context, e.message, Toast.LENGTH_LONG).show()
        } finally {
            db.close()
        }
        return deletedRowCount
    }

    fun deleteAllBooks(): Boolean {
        var deleteStatus = false
        val db = dbHelper.writableDatabase
        try {
            db.delete(TABLE_BOOK, null, null)
            deleteStatus = true
        } catch (e: SQLiteException) {
            Toast.makeText(context, e.message, Toast.LENGTH_LONG).show()
        } finally {
            db.close()
        }
        return deleteStatus
    }
}