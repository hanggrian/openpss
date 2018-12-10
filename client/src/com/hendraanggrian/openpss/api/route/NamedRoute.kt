package com.hendraanggrian.openpss.api.route

import com.hendraanggrian.openpss.api.Route
import com.hendraanggrian.openpss.db.schemas.DigitalPrice
import com.hendraanggrian.openpss.db.schemas.Employee
import com.hendraanggrian.openpss.db.schemas.OffsetPrice
import com.hendraanggrian.openpss.db.schemas.PlatePrice
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.http.HttpMethod

interface NamedRoute : Route {

    suspend fun getPlatePrices(): List<PlatePrice> = client.get {
        apiUrl("plate-prices")
    }

    suspend fun addPlatePrice(name: String): PlatePrice? = client.post {
        apiUrl("plate-prices")
        parameters("name" to name)
    }

    suspend fun editPlatePrice(name: String, price: Double): Boolean = client.requestStatus {
        apiUrl("plate-prices/$name")
        method = HttpMethod.Put
        parameters("price" to price)
    }

    suspend fun deletePlatePrice(name: String): Boolean = client.requestStatus {
        apiUrl("plate-prices/$name")
        method = HttpMethod.Delete
    }

    suspend fun getOffsetPrices(): List<OffsetPrice> = client.get {
        apiUrl("offset-prices")
    }

    suspend fun addOffsetPrice(name: String): OffsetPrice? = client.post {
        apiUrl("offset-prices")
        parameters("name" to name)
    }

    suspend fun editOffsetPrice(name: String, minQty: Int, minPrice: Double, excessPrice: Double): Boolean =
        client.requestStatus {
            apiUrl("offset-prices/$name")
            method = HttpMethod.Put
            parameters(
                "minQty" to minQty,
                "minPrice" to minPrice,
                "excessPrice" to excessPrice
            )
        }

    suspend fun deleteOffsetPrice(name: String): Boolean = client.requestStatus {
        apiUrl("offset-prices/$name")
        method = HttpMethod.Delete
    }

    suspend fun getDigitalPrices(): List<DigitalPrice> = client.get {
        apiUrl("digital-prices")
    }

    suspend fun addDigitalPrice(name: String): DigitalPrice? = client.post {
        apiUrl("digital-prices")
        parameters("name" to name)
    }

    suspend fun editDigitalPrice(name: String, oneSidePrice: Double, twoSidePrice: Double): Boolean =
        client.requestStatus {
            apiUrl("digital-prices/$name")
            method = HttpMethod.Put
            parameters(
                "oneSidePrice" to oneSidePrice,
                "twoSidePrice" to twoSidePrice
            )
        }

    suspend fun deleteDigitalPrice(name: String): Boolean = client.requestStatus {
        apiUrl("digital-prices/$name")
        method = HttpMethod.Delete
    }

    suspend fun getEmployees(): List<Employee> = client.get {
        apiUrl("employees")
    }

    suspend fun addEmployee(name: String): Employee? = client.post {
        apiUrl("employees")
        parameters("name" to name)
    }

    suspend fun editEmployee(name: String, password: String, isAdmin: Boolean): Boolean = client.requestStatus {
        apiUrl("employees/$name")
        method = HttpMethod.Put
        parameters(
            "password" to password,
            "isAdmin" to isAdmin
        )
    }

    suspend fun deleteEmployee(name: String): Boolean = client.requestStatus {
        apiUrl("employees/$name")
        method = HttpMethod.Delete
    }
}