package com.example.travelcopilot.domain

// ---------------------------
// AppSettings: runtime configuration
// ---------------------------
object AppSettings {
    // true = use Room database
    // false = use in-memory storage
    var useDatabase: Boolean = false
}