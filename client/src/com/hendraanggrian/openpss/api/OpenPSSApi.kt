package com.hendraanggrian.openpss.api

import com.hendraanggrian.openpss.content.Page
import com.hendraanggrian.openpss.db.schemas.Customer
import com.hendraanggrian.openpss.db.schemas.DigitalPrice
import com.hendraanggrian.openpss.db.schemas.Employee
import com.hendraanggrian.openpss.db.schemas.Invoice
import com.hendraanggrian.openpss.db.schemas.Log
import com.hendraanggrian.openpss.db.schemas.OffsetPrice
import com.hendraanggrian.openpss.db.schemas.PlatePrice
import com.hendraanggrian.openpss.db.schemas.Recess
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.request
import io.ktor.client.response.HttpResponse
import io.ktor.http.HttpMethod
import org.joda.time.LocalTime

class OpenPSSApi : Api("http://localhost:8080") {

    suspend fun login(name: String, password: String): Employee = client.get {
        apiUrl("login")
        parameters(
            "name" to name,
            "password" to password
        )
    }

    suspend fun isAdmin(name: String): Boolean = client.get<Employee> {
        apiUrl("employees/$name")
    }.isAdmin

    suspend fun getCustomers(search: String, page: Int, count: Int): Page<Customer> = client.get {
        apiUrl("customers")
        parameters(
            "search" to search,
            "page" to page,
            "count" to count
        )
    }

    suspend fun addCustomer(name: String, isCompany: Boolean): Customer = client.post {
        apiUrl("customers")
        json()
        parameters(
            "name" to name,
            "isCompany" to isCompany
        )
    }

    suspend fun editCustomer(name: String, address: String?, note: String?): Boolean = client.request<HttpResponse> {
        apiUrl("customers/$name")
        method = HttpMethod.Put
        parameters(
            "address" to address,
            "note" to note
        )
    }.useStatus()

    suspend fun addContact(name: String, contact: Customer.Contact): Customer.Contact = client.post {
        apiUrl("customers/$name/contacts")
        body = contact
    }

    suspend fun deleteContact(name: String, contact: Customer.Contact): Boolean = client.request<HttpResponse> {
        apiUrl("customers/$name/contacts")
        method = HttpMethod.Delete
        body = contact
    }.useStatus()

    suspend fun getLogs(page: Int, count: Int): Page<Log> = client.get {
        apiUrl("logs")
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
        apiUrl("logs")
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
        apiUrl("plate-prices")
    }

    suspend fun addPlatePrice(name: String): PlatePrice? = client.post {
        apiUrl("plate-prices")
        parameters("name" to name)
    }

    suspend fun editPlatePrice(name: String, price: Double): Boolean = client.request<HttpResponse> {
        apiUrl("plate-prices/$name")
        method = HttpMethod.Put
        parameters("price" to price)
    }.useStatus()

    suspend fun deletePlatePrice(name: String): Boolean = client.request<HttpResponse> {
        apiUrl("plate-prices/$name")
        method = HttpMethod.Delete
    }.useStatus()

    suspend fun getOffsetPrices(): List<OffsetPrice> = client.get {
        apiUrl("offset-prices")
    }

    suspend fun addOffsetPrice(name: String): OffsetPrice? = client.post {
        apiUrl("offset-prices")
        parameters("name" to name)
    }

    suspend fun editOffsetPrice(name: String, minQty: Int, minPrice: Double, excessPrice: Double): Boolean =
        client.request<HttpResponse> {
            apiUrl("offset-prices/$name")
            method = HttpMethod.Put
            parameters(
                "minQty" to minQty,
                "minPrice" to minPrice,
                "excessPrice" to excessPrice
            )
        }.useStatus()

    suspend fun deleteOffsetPrice(name: String): Boolean = client.request<HttpResponse> {
        apiUrl("offset-prices/$name")
        method = HttpMethod.Delete
    }.useStatus()

    suspend fun getDigitalPrices(): List<DigitalPrice> = client.get {
        apiUrl("digital-prices")
    }

    suspend fun addDigitalPrice(name: String): DigitalPrice? = client.post {
        apiUrl("digital-prices")
        parameters("name" to name)
    }

    suspend fun editDigitalPrice(name: String, oneSidePrice: Double, twoSidePrice: Double): Boolean =
        client.request<HttpResponse> {
            apiUrl("digital-prices/$name")
            method = HttpMethod.Put
            parameters(
                "oneSidePrice" to oneSidePrice,
                "twoSidePrice" to twoSidePrice
            )
        }.useStatus()

    suspend fun deleteDigitalPrice(name: String): Boolean = client.request<HttpResponse> {
        apiUrl("digital-prices/$name")
        method = HttpMethod.Delete
    }.useStatus()

    suspend fun getEmployees(): List<Employee> = client.get {
        apiUrl("employees")
    }

    suspend fun addEmployee(name: String): Employee? = client.post {
        apiUrl("employees")
        parameters("name" to name)
    }

    suspend fun editEmployee(name: String, password: String, isAdmin: Boolean): Boolean = client.request<HttpResponse> {
        apiUrl("employees/$name")
        method = HttpMethod.Put
        parameters(
            "password" to password,
            "isAdmin" to isAdmin
        )
    }.useStatus()

    suspend fun deleteEmployee(name: String): Boolean = client.request<HttpResponse> {
        apiUrl("employees/$name")
        method = HttpMethod.Delete
    }.useStatus()

    suspend fun getRecesses(): List<Recess> = client.get {
        apiUrl("recesses")
    }

    suspend fun addRecess(start: LocalTime, end: LocalTime): Recess = client.post {
        apiUrl("recesses")
        parameters(
            "start" to start,
            "end" to end
        )
    }

    suspend fun deleteRecess(start: LocalTime, end: LocalTime): Boolean = client.request<HttpResponse> {
        apiUrl("recesses")
        method = HttpMethod.Delete
        parameters(
            "start" to start,
            "end" to end
        )
    }.useStatus()
}