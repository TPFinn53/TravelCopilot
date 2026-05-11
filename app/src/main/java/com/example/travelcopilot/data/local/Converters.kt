package com.example.travelcopilot.data.local

import androidx.room.TypeConverter
import com.example.travelcopilot.domain.model.TripState
import com.example.travelcopilot.domain.model.TripEventType

class Converters {

    @TypeConverter
    fun fromTripState(state: TripState): String {
        return state.name
    }

    @TypeConverter
    fun toTripState(value: String): TripState {
        return TripState.valueOf(value)
    }

    @TypeConverter
    fun fromEventType(type: TripEventType): String = type.name

    @TypeConverter
    fun toEventType(value: String): TripEventType =
        TripEventType.valueOf(value)
}