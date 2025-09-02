package com.example.testapplication.payload

import java.io.Serializable

data class Topic(
    val name: String,
    val url: String,
    val startId: Int,
    val endId: Int,
    val description: String
) : Serializable