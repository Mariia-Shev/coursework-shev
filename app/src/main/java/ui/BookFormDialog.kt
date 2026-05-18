package com.example.coursework_shev.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.DialogFragment
import com.example.coursework_shev.R
import com.example.coursework_shev.database.DatabaseQueryClass
import com.example.coursework_shev.model.Book

class BookFormDialog(
    private val existingBook: Book? = null,
    private val position: Int = -1,
    private val onSaveSuccess: (Book, Int) -> Unit
) : DialogFragment() {

    private lateinit var databaseQueryClass: DatabaseQueryClass

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.dialog_book_form, container, false)
        databaseQueryClass = DatabaseQueryClass(requireContext())

        val etTitle = view.findViewById<EditText>(R.id.etTitle)
        val etAuthor = view.findViewById<EditText>(R.id.etAuthor)
        val etIsbn = view.findViewById<EditText>(R.id.etIsbn)
        val etImageUrl = view.findViewById<EditText>(R.id.etImageUrl)
        val btnSave = view.findViewById<Button>(R.id.btnSave)
        val btnCancel = view.findViewById<Button>(R.id.btnCancel)

        // Pre-fill if updating
        existingBook?.let {
            etTitle.setText(it.title)
            etAuthor.setText(it.author)
            etIsbn.setText(it.isbn)
            etImageUrl.setText(it.imageUrl)
        }

        btnSave.setOnClickListener {
            val book = Book(
                id = existingBook?.id ?: -1,
                title = etTitle.text.toString(),
                author = etAuthor.text.toString(),
                isbn = etIsbn.text.toString(),
                imageUrl = etImageUrl.text.toString()
            )

            if (existingBook == null) {
                val id = databaseQueryClass.insertBook(book)
                if (id > 0) {
                    book.id = id
                    onSaveSuccess(book, -1)
                    dismiss()
                }
            } else {
                val rows = databaseQueryClass.updateBook(book)
                if (rows > 0) {
                    onSaveSuccess(book, position)
                    dismiss()
                }
            }
        }

        btnCancel.setOnClickListener { dismiss() }

        return view
    }
}