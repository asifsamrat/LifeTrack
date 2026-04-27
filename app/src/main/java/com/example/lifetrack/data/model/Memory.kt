package com.example.lifetrack.data.model

data class Memory (
    val title: String = "",
    val description: String = "",
    val date: String = "",
    val time: String = "",
    val imageUrls: List<String> = emptyList(),
    val videoUrls: List<String> = emptyList(),
    val userId: String = ""
)