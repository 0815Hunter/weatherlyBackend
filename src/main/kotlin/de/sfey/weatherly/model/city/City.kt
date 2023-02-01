package de.sfey.weatherly.model.city


import io.quarkus.hibernate.orm.panache.kotlin.PanacheCompanion
import io.quarkus.hibernate.orm.panache.kotlin.PanacheEntityBase
import io.quarkus.panache.common.Page
import javax.persistence.Embedded
import javax.persistence.Entity
import javax.persistence.Id

@Entity
class City() : PanacheEntityBase {

    companion object : PanacheCompanion<City> {

        fun findByName(name: String) = find("name", name).firstResult()
        fun findByNameLike(nameLike: String) = find("name like ?1", nameLike).page(Page.ofSize(5)).list()
    }

    constructor(cityId: Long?, name: String, geoLocation: GeoLocation) : this() {
        this.cityId = cityId
        this.name = name
        this.geoLocation = geoLocation
    }

    @Id
    var cityId: Long? = null

    lateinit var name: String

    @Embedded
    lateinit var geoLocation: GeoLocation

}