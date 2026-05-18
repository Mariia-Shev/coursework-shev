package com.example.coursework_shev.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.example.coursework_shev.R
import com.example.coursework_shev.database.DatabaseQueryClass
import com.example.coursework_shev.model.Book

class BookAdapter(
    private val bookList: MutableList<Book>,
    private val databaseQueryClass: DatabaseQueryClass,
    private val onEditClicked: (Book, Int) -> Unit,
    private val onListEmpty: () -> Unit
) : RecyclerView.Adapter<BookAdapter.BookViewHolder>() {

    class BookViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvTitle: TextView = view.findViewById(R.id.tvTitle)
        val tvAuthor: TextView = view.findViewById(R.id.tvAuthor)
        val ivBookCover: ImageView = view.findViewById(R.id.ivBookCover)
        val ivEdit: ImageView = view.findViewById(R.id.ivEdit)
        val ivDelete: ImageView = view.findViewById(R.id.ivDelete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_book, parent, false)
        return BookViewHolder(view)
    }

    override fun onBindViewHolder(holder: BookViewHolder, position: Int) {
        val book = bookList[position]
        holder.tvTitle.text = book.title
        holder.tvAuthor.text = book.author

        // Note: To load the image from the URL string safely, you should add the Glide or Coil
        // library to your build.gradle and call it here.
        // Example: Glide.with(holder.itemView.context).load(book.imageUrl).into(holder.ivBookCover)

        holder.ivDelete.setOnClickListener {
            AlertDialog.Builder(holder.itemView.context)
                .setMessage("Are you sure you want to delete this book?")
                .setPositiveButton("Yes") { _, _ -> deleteBook(position) }
                .setNegativeButton("No", null)
                .show()
        }

        holder.ivEdit.setOnClickListener {
            onEditClicked(book, position)
        }
    }

    private fun deleteBook(position: Int) {
        val book = bookList[position]
        val result = databaseQueryClass.deleteBook(book.id)
        if (result > 0) {
            bookList.removeAt(position)
            notifyItemRemoved(position)
            if (bookList.isEmpty()) onListEmpty()
        }
    }

    override fun getItemCount() = bookList.size
}