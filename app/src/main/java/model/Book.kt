package com.example.coursework_shev.model

data class Book(
    var id: Long = -1,
    var title: String,
    var author: String,
    var isbn: String,
    var imageUrl: String? = null // New field for the image link
)