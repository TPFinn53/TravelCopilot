package com.example.travelcopilot.infra.location

import android.content.Context
import android.location.Address
import android.location.Geocoder
import android.os.Build
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

class LocationHelper(context: Context) {

    private val geocoder = Geocoder(context)

    // =====================================================
    // 🔹 Core Geocoding (Single Source of Truth)
    // =====================================================

    private suspend fun getAddressInternal(
        latitude: Double,
        longitude: Double
    ): Address? = suspendCancellableCoroutine { cont ->

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            // ✅ Modern async API
            geocoder.getFromLocation(
                latitude,
                longitude,
                1,
                object : Geocoder.GeocodeListener {

                    override fun onGeocode(addresses: MutableList<Address>) {
                        cont.resume(addresses.firstOrNull())
                    }

                    override fun onError(errorMessage: String?) {
                        cont.resume(null)
                    }
                }
            )
        } else {
            // ⚠️ Legacy fallback (required)
            try {
                @Suppress("DEPRECATION")
                val addresses = geocoder.getFromLocation(latitude, longitude, 1)
                cont.resume(addresses?.firstOrNull())
            } catch (e: Exception) {
                cont.resume(null)
            }
        }
    }

    // =====================================================
    // 🌍 Public API (Clean + Reusable)
    // =====================================================

    suspend fun getFullAddress(
        latitude: Double,
        longitude: Double
    ): String? {
        return getAddressInternal(latitude, longitude)
            ?.getAddressLine(0)
    }

    suspend fun getCity(
        latitude: Double,
        longitude: Double
    ): String? {
        return getAddressInternal(latitude, longitude)
            ?.locality
    }

    suspend fun getState(
        latitude: Double,
        longitude: Double
    ): String? {
        return getAddressInternal(latitude, longitude)
            ?.adminArea
    }

    suspend fun getCountry(
        latitude: Double,
        longitude: Double
    ): String? {
        return getAddressInternal(latitude, longitude)
            ?.countryName
    }

    suspend fun getLocationSummary(
        latitude: Double,
        longitude: Double
    ): String {
        val address = getAddressInternal(latitude, longitude)

        val city = address?.locality
        val state = address?.adminArea
        val country = address?.countryName

        return listOfNotNull(city, state, country)
            .joinToString(", ")
            .ifEmpty { "Unknown location" }
    }
}