package com.example.coursework_shev

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.coursework_shev.R
import com.example.coursework_shev.database.DatabaseQueryClass
import com.example.coursework_shev.model.Book
import com.example.coursework_shev.ui.BookAdapter
import com.example.coursework_shev.ui.BookFormDialog
import com.google.android.material.floatingactionbutton.FloatingActionButton

class MainActivity : AppCompatActivity() {

    private lateinit var databaseQueryClass: DatabaseQueryClass
    private val bookList = mutableListOf<Book>()
    private lateinit var adapter: BookAdapter
    private lateinit var tvEmptyState: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main) // Standard Activity layout with a RecyclerView and FAB

        databaseQueryClass = DatabaseQueryClass(this)
        tvEmptyState = findViewById(R.id.tvEmptyState)

        bookList.addAll(databaseQueryClass.getAllBooks())

        adapter = BookAdapter(bookList, databaseQueryClass,
            onEditClicked = { book, position -> openFormDialog(book, position) },
            onListEmpty = { checkEmptyState() }
        )

        findViewById<RecyclerView>(R.id.recyclerView).apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = this@MainActivity.adapter
        }

        findViewById<FloatingActionButton>(R.id.fabAdd).setOnClickListener {
            openFormDialog()
        }

        checkEmptyState()
    }

    private fun openFormDialog(book: Book? = null, position: Int = -1) {
        val dialog = BookFormDialog(book, position) { savedBook, pos ->
            if (pos == -1) {
                bookList.add(savedBook)
                adapter.notifyItemInserted(bookList.size - 1)
            } else {
                bookList[pos] = savedBook
                adapter.notifyItemChanged(pos)
            }
            checkEmptyState()
        }
        dialog.show(supportFragmentManager, "BookForm")
    }

    private fun checkEmptyState() {
        tvEmptyState.visibility = if (bookList.isEmpty()) View.VISIBLE else View.GONE
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.action_delete_all) {
            AlertDialog.Builder(this)
                .setMessage("Delete all books?")
                .setPositiveButton("Yes") { _, _ ->
                    if (databaseQueryClass.deleteAllBooks()) {
                        bookList.clear()
                        adapter.notifyDataSetChanged()
                        checkEmptyState()
                    }
                }
                .setNegativeButton("No", null)
                .show()
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}