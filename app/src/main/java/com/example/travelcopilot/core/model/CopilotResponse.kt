package com.example.travelcopilot.core.model

import com.example.travelcopilot.domain.model.TripEvent
data class CopilotResponse(
    val message: String,
    val tripEvent: TripEvent? = null,
    val note: LogNote? = null   // 👈 NEW
)
