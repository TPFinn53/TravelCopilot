package com.example.travelcopilot.core.context

data class TimeContext(
    val currentTime: Long,
    val driveDurationMs: Long,
    val idleDurationMs: Long,
    val lastAiResponseTime: Long?
)
