package com.example.travelcopilot.data.local
enum class MoodType {
    HAPPY, STRESSED, TIRED, NEUTRAL
}

data class Mood(
    val happiness: Float = 0.5f,
    val stress: Float = 0.5f,
    val alertness: Float = 0.5f
) {
    fun dominant(): MoodType {
        return when {

            happiness > 0.7f -> MoodType.HAPPY
            stress > 0.7f -> MoodType.STRESSED
            alertness < 0.3f -> MoodType.TIRED
            else -> MoodType.NEUTRAL
        }
    }
}
