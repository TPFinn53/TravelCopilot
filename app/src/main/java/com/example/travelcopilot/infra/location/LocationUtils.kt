package com.example.travelcopilot.infra.location

import android.location.Location

object LocationUtils {

    fun calculateDistanceMeters(points: List<Pair<Double, Double>>): Float {
        var total = 0f

        for (i in 0 until points.size - 1) {
            val start = points[i]
            val end = points[i + 1]

            val results = FloatArray(1)
            Location.distanceBetween(
                start.first, start.second,
                end.first, end.second,
                results
            )

            total += results[0]
        }

        return total
    }
}