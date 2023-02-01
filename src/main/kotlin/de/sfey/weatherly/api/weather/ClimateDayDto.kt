package de.sfey.weatherly.api.weather

import java.math.BigDecimal
import java.time.LocalDate

data class ClimateDayDto(val measureDate: LocalDate, val meanTemperature: BigDecimal)