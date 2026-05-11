package com.example.travelcopilot.infra.location

import android.annotation.SuppressLint
import android.content.Context
import android.location.Geocoder
import com.google.android.gms.location.FusedLocationProviderClient
import com.example.travelcopilot.utils.location.LocationData
import dagger.hilt.android.qualifiers.ApplicationContext
import java.util.Locale
import javax.inject.Inject
import kotlinx.coroutines.tasks.await

class LocationProvider @Inject constructor(
    private val fusedClient: FusedLocationProviderClient,

    @ApplicationContext
    private val context: Context
) {

    @SuppressLint("MissingPermission")
    suspend fun getCurrentLocation(): LocationData? {
        return try {
            val location = fusedClient.lastLocation.await() ?: return null

            val geocoder = Geocoder(context, Locale.getDefault())
            val address = geocoder.getFromLocation(
                location.latitude,
                location.longitude,
                1
            )?.firstOrNull()

            LocationData(
                latitude = location.latitude,
                longitude = location.longitude,
                speed = location.speed,
                placeName = address?.featureName,
                city = address?.locality,
                regionState = address?.adminArea
            )

        } catch (e: Exception) {
            null
        }
    }
}