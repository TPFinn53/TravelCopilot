package com.example.travelcopilot.core.context

data class PlaceContext(
    val name: String,
    val city: String?,
    val state: String?,
    val confidence: Float
)
