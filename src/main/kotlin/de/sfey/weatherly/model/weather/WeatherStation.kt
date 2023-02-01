package de.sfey.weatherly.model.weather

import de.sfey.weatherly.model.city.GeoLocation
import io.quarkus.hibernate.orm.panache.kotlin.PanacheCompanion
import io.quarkus.hibernate.orm.panache.kotlin.PanacheEntityBase
import java.time.LocalDate
import javax.persistence.Embedded
import javax.persistence.Entity
import javax.persistence.Id

@Entity
class WeatherStation() : PanacheEntityBase {

    companion object : PanacheCompanion<WeatherStation> {
        fun findByStationId(stationId: String) = find("stationId", stationId).firstResult()
    }

    constructor(stationId: String?, stationName: String, fromDate: LocalDate, toDate: LocalDate, geoLocation: GeoLocation) : this() {
        this.stationId = stationId
        this.stationName = stationName
        this.fromDate = fromDate
        this.toDate = toDate
        this.geoLocation = geoLocation
    }

    @Id
    var stationId: String? = null

    lateinit var stationName: String

    lateinit var fromDate: LocalDate
    lateinit var toDate: LocalDate

    @Embedded
    lateinit var geoLocation: GeoLocation

}