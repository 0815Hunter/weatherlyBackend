package de.sfey.weatherly.model.city

import java.math.BigDecimal
import javax.persistence.Embeddable
import kotlin.math.*

@Embeddable
class GeoLocation() {
    lateinit var lat: BigDecimal
    lateinit var long: BigDecimal

    constructor(lat: BigDecimal, long: BigDecimal) : this() {
        this.lat = lat
        this.long = long
    }

    fun distanceTo(other: GeoLocation): Double {
        val thisRad = GeoLocationRad.fromGeolocation(this)
        val otherRad = GeoLocationRad.fromGeolocation(other)

        return thisRad.distanceTo(otherRad)
    }


    class GeoLocationRad(private val latRad: Double, private val longRad: Double) {

        fun distanceTo(other: GeoLocationRad): Double {

            val lat1 = latRad
            val lat2 = other.latRad

            val long1 = longRad
            val long2 = other.longRad

            val dLon = long2 - long1
            val dLat = lat2 - lat1

            val a = sin(dLat / 2).pow(2) + cos(lat1) * cos(lat2) * sin(dLon / 2).pow(2)
            val c = 2 * asin(sqrt(a))

            val dKilometer = 6371 * c

            return dKilometer

        }

        companion object {
            fun fromGeolocation(geoLocation: GeoLocation): GeoLocationRad =
                GeoLocationRad(
                    Math.toRadians(geoLocation.lat.toDouble()),
                    Math.toRadians(geoLocation.long.toDouble())
                )
        }

    }
}