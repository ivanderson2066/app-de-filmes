package com.example.filmeapp

data class Movie(
    val title: String = "",
    val description: String = "",
    val poster_path: String = "",  // Altere para o nome correto do campo no Firestore
    val release_date: String = "",
    val videoUrl: String = ""
)
