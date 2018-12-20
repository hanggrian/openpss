package com.hendraanggrian.openpss.api

import com.hendraanggrian.openpss.db.schemas.DigitalPrice
import com.hendraanggrian.openpss.db.schemas.Employee
import com.hendraanggrian.openpss.db.schemas.OffsetPrice
import com.hendraanggrian.openpss.db.schemas.PlatePrice
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.http.HttpMethod
import kotlinx.nosql.Id

interface NamedApi : Api {

    suspend fun getPlatePrices(): List<PlatePrice> = client.get {
        apiUrl("plate-prices")
    }

    suspend fun addPlatePrice(name: String): PlatePrice? = client.post {
        apiUrl("plate-prices")
        parameters("name" to name)
    }

    suspend fun getPlatePrice(id: Id<String, *>): PlatePrice = client.get {
        apiUrl("plate-prices/$id")
    }

    suspend fun editPlatePrice(id: Id<String, *>, price: Double): Boolean = client.requestStatus {
        apiUrl("plate-prices/$id")
        method = HttpMethod.Put
        parameters("price" to price)
    }

    suspend fun deletePlatePrice(id: Id<String, *>): Boolean = client.requestStatus {
        apiUrl("plate-prices/$id")
        method = HttpMethod.Delete
    }

    suspend fun getOffsetPrices(): List<OffsetPrice> = client.get {
        apiUrl("offset-prices")
    }

    suspend fun addOffsetPrice(name: String): OffsetPrice? = client.post {
        apiUrl("offset-prices")
        parameters("name" to name)
    }

    suspend fun getOffsetPrice(id: Id<String, *>): OffsetPrice = client.get {
        apiUrl("offset-prices/$id")
    }

    suspend fun editOffsetPrice(id: Id<String, *>, minQty: Int, minPrice: Double, excessPrice: Double): Boolean =
        client.requestStatus {
            apiUrl("offset-prices/$id")
            method = HttpMethod.Put
            parameters(
                "minQty" to minQty,
                "minPrice" to minPrice,
                "excessPrice" to excessPrice
            )
        }

    suspend fun deleteOffsetPrice(id: Id<String, *>): Boolean = client.requestStatus {
        apiUrl("offset-prices/$id")
        method = HttpMethod.Delete
    }

    suspend fun getDigitalPrices(): List<DigitalPrice> = client.get {
        apiUrl("digital-prices")
    }

    suspend fun addDigitalPrice(name: String): DigitalPrice? = client.post {
        apiUrl("digital-prices")
        parameters("name" to name)
    }

    suspend fun getDigitalPrice(id: Id<String, *>): DigitalPrice = client.get {
        apiUrl("digital-prices/$id")
    }

    suspend fun editDigitalPrice(id: Id<String, *>, oneSidePrice: Double, twoSidePrice: Double): Boolean =
        client.requestStatus {
            apiUrl("digital-prices/$id")
            method = HttpMethod.Put
            parameters(
                "oneSidePrice" to oneSidePrice,
                "twoSidePrice" to twoSidePrice
            )
        }

    suspend fun deleteDigitalPrice(id: Id<String, *>): Boolean = client.requestStatus {
        apiUrl("digital-prices/$id")
        method = HttpMethod.Delete
    }

    suspend fun getEmployees(): List<Employee> = client.get {
        apiUrl("employees")
    }

    suspend fun addEmployee(name: String): Employee? = client.post {
        apiUrl("employees")
        parameters("name" to name)
    }

    suspend fun getEmployee(id: Id<String, *>): Employee = client.get {
        apiUrl("employees/$id")
    }

    suspend fun editEmployee(login: Employee, id: Id<String, *>, password: String, isAdmin: Boolean): Boolean =
        client.requestStatus {
            apiUrl("employees/$id")
            method = HttpMethod.Put
            parameters(
                "password" to password,
                "isAdmin" to isAdmin,
                "login" to login.name
            )
        }

    suspend fun deleteEmployee(login: Employee, id: Id<String, *>): Boolean = client.requestStatus {
        apiUrl("employees/$id")
        method = HttpMethod.Delete
        parameters("login" to login.name)
    }
}