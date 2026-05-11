package com.example.travelcopilot.data.geocoding

import android.content.Context
import android.location.Address
import android.location.Geocoder
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

class ReverseGeocoder(context: Context) {

    private val geocoder = Geocoder(context)

    suspend fun getAddress(
        latitude: Double,
        longitude: Double
    ): String? = suspendCancellableCoroutine { cont ->

        geocoder.getFromLocation(
            latitude,
            longitude,
            1,
            object : Geocoder.GeocodeListener {

                override fun onGeocode(addresses: MutableList<Address>) {
                    val result = addresses.firstOrNull()?.getAddressLine(0)
                    cont.resume(result)
                }

                override fun onError(errorMessage: String?) {
                    cont.resume(null)
                }
            }
        )
    }
}