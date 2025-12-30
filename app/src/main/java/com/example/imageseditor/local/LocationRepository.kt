package com.example.imageseditor.local

import android.content.Context
import com.google.android.gms.location.LocationServices


class LocationRepository(context: Context) {

    private val fusedLocationClient =
        LocationServices.getFusedLocationProviderClient(context)

    @SuppressLint("MissingPermission")
    suspend fun getCurrentLocation(): Pair<Double, Double>? =
        suspendCancellableCoroutine { cont ->

            fusedLocationClient.lastLocation
                .addOnSuccessListener { location ->
                    if (location != null) {
                        cont.resume(
                            Pair(location.latitude, location.longitude)
                        )
                    } else {
                        cont.resume(null)
                    }
                }
                .addOnFailureListener {
                    cont.resume(null)
                }
        }
}