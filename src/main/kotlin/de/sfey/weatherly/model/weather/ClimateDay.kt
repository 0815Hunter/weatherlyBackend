package de.sfey.weatherly.model.weather

import io.quarkus.hibernate.orm.panache.kotlin.PanacheCompanion
import io.quarkus.hibernate.orm.panache.kotlin.PanacheEntityBase
import java.math.BigDecimal
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.persistence.*

@Entity
class ClimateDay() : PanacheEntityBase {
    companion object : PanacheCompanion<ClimateDay> {

        fun findByStationIdAndDates(stationId: String, dates: List<LocalDate>) = find("station_id = ?1 and measureDate in (?2)", stationId, dates).list()

        private const val MESS_DATUM_INDEX = 1
        private const val TMK_INDEX = 13 // daily mean of temperature
        fun fromDwdTageswerteLine(tageswerteLine: String): ClimateDay {

            val values = tageswerteLine.split(";")

            val measureDateString = values[MESS_DATUM_INDEX].trim()
            val measureDate = LocalDate.parse(measureDateString, DateTimeFormatter.BASIC_ISO_DATE)

            val meanTemperature = values[TMK_INDEX].trim().toBigDecimal()

            return ClimateDay(measureDate, meanTemperature)
        }
    }

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "id", nullable = false)
    var id: Long? = null

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "station_id", nullable = false)
    lateinit var weatherStation: WeatherStation

    lateinit var measureDate: LocalDate
    lateinit var meanTemperature: BigDecimal

    constructor(measureDate: LocalDate, meanTemperature: BigDecimal) : this() {
        this.measureDate = measureDate
        this.meanTemperature = meanTemperature
    }

}