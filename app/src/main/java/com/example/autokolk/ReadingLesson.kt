package com.example.autokolk

data class ReadingLesson(
    val category: String,
    val text: String,
    val imagePath: String? = null,
    val isLastSlide: Boolean = false
) 