package com.example.coursework_shev.ui

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.DialogFragment
import com.bumptech.glide.Glide
import com.example.coursework_shev.R
import com.example.coursework_shev.database.DatabaseQueryClass
import com.example.coursework_shev.model.Book
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream

class BookFormDialog(
    private val existingBook: Book? = null,
    private val position: Int = -1,
    private val onSaveSuccess: (Book, Int) -> Unit
) : DialogFragment() {

    private lateinit var databaseQueryClass: DatabaseQueryClass

    // Register the modern photo picker
    private val pickImageLauncher = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        uri?.let {
            // Copy the image to internal storage so we keep access to it permanently
            val savedPath = saveImageToInternalStorage(it)
            if (savedPath != null) {
                // Populate the existing EditText with the local path
                view?.findViewById<EditText>(R.id.etImageUrl)?.setText(savedPath)
                // Update the preview ImageView
                view?.findViewById<ImageView>(R.id.bookImageView)?.let { iv ->
                    Glide.with(requireContext()).load(savedPath).into(iv)
                }
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.dialog_book_form, container, false)
        databaseQueryClass = DatabaseQueryClass(requireContext())

        val etTitle = view.findViewById<EditText>(R.id.etTitle)
        val etAuthor = view.findViewById<EditText>(R.id.etAuthor)
        val etIsbn = view.findViewById<EditText>(R.id.etIsbn)
        val etImageUrl = view.findViewById<EditText>(R.id.etImageUrl)
        val btnSave = view.findViewById<Button>(R.id.btnSave)
        val btnCancel = view.findViewById<Button>(R.id.btnCancel)
        val btnPickImage = view.findViewById<Button>(R.id.btnPickImage)

        // Launch the image picker when the button is clicked
        btnPickImage.setOnClickListener {
            pickImageLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }

        // Pre-fill if updating
        existingBook?.let { book ->
            etTitle.setText(book.title)
            etAuthor.setText(book.author)
            etIsbn.setText(book.isbn)
            etImageUrl.setText(book.imageUrl)

            // Show the image preview in the dialog form
            if (!book.imageUrl.isNullOrEmpty()) {
                val bookImageView = view.findViewById<ImageView>(R.id.bookImageView)
                Glide.with(requireContext())
                    .load(book.imageUrl)
                    .into(bookImageView)
            }
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

    // Helper method to copy the selected file to your app's private files directory
    private fun saveImageToInternalStorage(uri: Uri): String? {
        return try {
            val inputStream: InputStream? = requireContext().contentResolver.openInputStream(uri)
            val fileName = "book_cover_${System.currentTimeMillis()}.jpg"
            val file = File(requireContext().filesDir, fileName)
            val outputStream = FileOutputStream(file)

            inputStream?.copyTo(outputStream)
            inputStream?.close()
            outputStream.close()

            file.absolutePath // We return the absolute local file path to save into SQLite
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}