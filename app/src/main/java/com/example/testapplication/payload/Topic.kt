package com.example.testapplication.payload

data class Topic(
    val name: String,
    val url: String,
    val startId: Int,
    val endId: Int,
    val description: String
) : java.io.Serializable
