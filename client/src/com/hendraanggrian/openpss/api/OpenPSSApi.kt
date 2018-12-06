package com.hendraanggrian.openpss.api

import com.hendraanggrian.openpss.content.Page
import com.hendraanggrian.openpss.db.schemas.Customer
import com.hendraanggrian.openpss.db.schemas.DigitalPrice
import com.hendraanggrian.openpss.db.schemas.Employee
import com.hendraanggrian.openpss.db.schemas.Invoice
import com.hendraanggrian.openpss.db.schemas.Log
import com.hendraanggrian.openpss.db.schemas.OffsetPrice
import com.hendraanggrian.openpss.db.schemas.PlatePrice
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.put

class OpenPSSApi : Api("http://localhost:8080") {

    suspend fun login(name: String, password: String): Employee = client.get {
        apiUrl("login")
        parameters(
            "name" to name,
            "password" to password
        )
    }

    suspend fun getCustomers(search: String, page: Int, count: Int): Page<Customer> = client.get {
        apiUrl("customer")
        parameters(
            "search" to search,
            "page" to page
        )
    }

    suspend fun addCustomer(name: String, isCompany: Boolean): Customer = client.post {
        apiUrl("customer")
        json()
        parameters(
            "name" to name,
            "isCompany" to isCompany
        )
    }

    suspend fun getLogs(page: Int, count: Int): Page<Log> = client.get {
        apiUrl("log")
        parameters(
            "page" to page,
            "count" to count
        )
    }

    suspend fun getInvoices(
        search: Int,
        customer: String?,
        isPaid: Boolean?,
        date: Any?,
        page: Int,
        count: Int
    ): Page<Invoice> = client.get {
        apiUrl("log")
        parameters(
            "search" to search,
            "customer" to customer,
            "isPaid" to isPaid,
            "date" to date,
            "page" to page,
            "count" to count
        )
    }

    suspend fun getPlatePrices(): List<PlatePrice> = client.get {
        apiUrl("plate-price")
    }

    suspend fun addPlatePrice(name: String): PlatePrice = client.post {
        apiUrl("plate-price")
        parameters("name" to name)
    }

    suspend fun editPlatePrice(name: String, price: Double): PlatePrice = client.put {
        apiUrl("plate-price/$name")
        parameters("price" to price)
    }

    suspend fun deletePlatePrice(name: String): PlatePrice = client.delete {
        apiUrl("plate-price/$name")
    }

    suspend fun getOffsetPrices(): List<OffsetPrice> = client.get {
        apiUrl("offset-price")
    }

    suspend fun addOffsetPrice(name: String): OffsetPrice = client.post {
        apiUrl("offset-price")
        parameters("name" to name)
    }

    suspend fun editOffsetPrice(name: String, minQty: Int, minPrice: Double, excessPrice: Double): OffsetPrice =
        client.put {
            apiUrl("offset-price/$name")
            parameters(
                "minQty" to minQty,
                "minPrice" to minPrice,
                "excessPrice" to excessPrice
            )
        }

    suspend fun deleteOffsetPrice(name: String): OffsetPrice = client.delete {
        apiUrl("offset-price/$name")
    }

    suspend fun getDigitalPrices(): List<DigitalPrice> = client.get {
        apiUrl("digital-price")
    }

    suspend fun addDigitalPrice(name: String): DigitalPrice = client.post {
        apiUrl("digital-price")
        parameters("name" to name)
    }

    suspend fun editDigitalPrice(name: String, oneSidePrice: Double, twoSidePrice: Double): DigitalPrice = client.put {
        apiUrl("digital-price/$name")
        parameters(
            "oneSidePrice" to oneSidePrice,
            "twoSidePrice" to twoSidePrice
        )
    }

    suspend fun deleteDigitalPrice(name: String): DigitalPrice = client.delete {
        apiUrl("digital-price/$name")
    }

    suspend fun getEmployees(): List<Employee> = client.get {
        apiUrl("employee")
    }

    suspend fun addEmployee(name: String): Employee = client.post {
        apiUrl("employee")
        parameters("name" to name)
    }

    suspend fun editEmployee(name: String, password: String, isAdmin: Boolean): Employee = client.put {
        apiUrl("employee/$name")
        parameters(
            "password" to password,
            "isAdmin" to isAdmin
        )
    }

    suspend fun deleteEmployee(name: String): Employee = client.delete {
        apiUrl("employee/$name")
    }
}