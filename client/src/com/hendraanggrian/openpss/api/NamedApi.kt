package com.hendraanggrian.openpss.api

import com.hendraanggrian.openpss.data.DigitalPrice
import com.hendraanggrian.openpss.data.Employee
import com.hendraanggrian.openpss.data.OffsetPrice
import com.hendraanggrian.openpss.data.PlatePrice
import com.hendraanggrian.openpss.schema.DigitalPrices
import com.hendraanggrian.openpss.schema.Employees
import com.hendraanggrian.openpss.schema.OffsetPrices
import com.hendraanggrian.openpss.schema.PlatePrices
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.http.HttpMethod
import kotlinx.nosql.Id

interface NamedApi : Api {

    suspend fun getPlatePrices(): List<PlatePrice> = client.get {
        apiUrl("$PlatePrices")
    }

    suspend fun addPlatePrice(name: String): PlatePrice? = client.post {
        apiUrl("$PlatePrices")
        parameters("name" to name)
    }

    suspend fun getPlatePrice(id: Id<String, *>): PlatePrice = client.get {
        apiUrl("$PlatePrices/$id")
    }

    suspend fun editPlatePrice(id: Id<String, *>, price: Double): Boolean = client.requestStatus(HttpMethod.Put) {
        apiUrl("$PlatePrices/$id")
        parameters("price" to price)
    }

    suspend fun deletePlatePrice(id: Id<String, *>): Boolean = client.requestStatus(HttpMethod.Delete) {
        apiUrl("$PlatePrices/$id")
    }

    suspend fun getOffsetPrices(): List<OffsetPrice> = client.get {
        apiUrl("$OffsetPrices")
    }

    suspend fun addOffsetPrice(name: String): OffsetPrice? = client.post {
        apiUrl("$OffsetPrices")
        parameters("name" to name)
    }

    suspend fun getOffsetPrice(id: Id<String, *>): OffsetPrice = client.get {
        apiUrl("$OffsetPrices/$id")
    }

    suspend fun editOffsetPrice(id: Id<String, *>, minQty: Int, minPrice: Double, excessPrice: Double): Boolean =
        client.requestStatus(HttpMethod.Put) {
            apiUrl("$OffsetPrices/$id")
            parameters(
                "minQty" to minQty,
                "minPrice" to minPrice,
                "excessPrice" to excessPrice
            )
        }

    suspend fun deleteOffsetPrice(id: Id<String, *>): Boolean = client.requestStatus(HttpMethod.Delete) {
        apiUrl("$OffsetPrices/$id")
    }

    suspend fun getDigitalPrices(): List<DigitalPrice> = client.get {
        apiUrl("$DigitalPrices")
    }

    suspend fun addDigitalPrice(name: String): DigitalPrice? = client.post {
        apiUrl("$DigitalPrices")
        parameters("name" to name)
    }

    suspend fun getDigitalPrice(id: Id<String, *>): DigitalPrice = client.get {
        apiUrl("$DigitalPrices/$id")
    }

    suspend fun editDigitalPrice(id: Id<String, *>, oneSidePrice: Double, twoSidePrice: Double): Boolean =
        client.requestStatus(HttpMethod.Put) {
            apiUrl("$DigitalPrices/$id")
            parameters(
                "oneSidePrice" to oneSidePrice,
                "twoSidePrice" to twoSidePrice
            )
        }

    suspend fun deleteDigitalPrice(id: Id<String, *>): Boolean = client.requestStatus(HttpMethod.Delete) {
        apiUrl("$DigitalPrices/$id")
    }

    suspend fun getEmployees(): List<Employee> = client.get {
        apiUrl("$Employees")
    }

    suspend fun addEmployee(name: String): Employee? = client.post {
        apiUrl("$Employees")
        parameters("name" to name)
    }

    suspend fun getEmployee(id: Id<String, *>): Employee = client.get {
        apiUrl("$Employees/$id")
    }

    suspend fun editEmployee(login: Employee, id: Id<String, *>, password: String, isAdmin: Boolean): Boolean =
        client.requestStatus(HttpMethod.Put) {
            apiUrl("$Employees/$id")
            parameters(
                "password" to password,
                "isAdmin" to isAdmin,
                "login" to login.name
            )
        }

    suspend fun deleteEmployee(login: Employee, id: Id<String, *>): Boolean = client.requestStatus(HttpMethod.Delete) {
        apiUrl("$Employees/$id")
        parameters("login" to login.name)
    }
}