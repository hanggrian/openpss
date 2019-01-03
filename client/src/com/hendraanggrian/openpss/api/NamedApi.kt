package com.hendraanggrian.openpss.api

import com.hendraanggrian.openpss.data.DigitalPrice
import com.hendraanggrian.openpss.data.Employee
import com.hendraanggrian.openpss.data.OffsetPrice
import com.hendraanggrian.openpss.data.PlatePrice
import com.hendraanggrian.openpss.nosql.StringId
import com.hendraanggrian.openpss.schema.DigitalPrices
import com.hendraanggrian.openpss.schema.Employees
import com.hendraanggrian.openpss.schema.OffsetPrices
import com.hendraanggrian.openpss.schema.PlatePrices
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.http.HttpMethod

interface NamedApi : Api {

    suspend fun getPlatePrices(): List<PlatePrice> = client.get {
        apiUrl(PlatePrices.schemaName)
    }

    suspend fun addPlatePrice(price: PlatePrice): PlatePrice? = client.post {
        apiUrl(PlatePrices.schemaName)
        jsonBody(price)
    }

    suspend fun getPlatePrice(id: StringId<*>): PlatePrice = client.get {
        apiUrl("${PlatePrices.schemaName}/$id")
    }

    suspend fun editPlatePrice(price: PlatePrice): Boolean = client.requestStatus(HttpMethod.Put) {
        apiUrl("${PlatePrices.schemaName}/${price.id}")
        jsonBody(price)
    }

    suspend fun deletePlatePrice(id: StringId<*>): Boolean = client.requestStatus(HttpMethod.Delete) {
        apiUrl("${PlatePrices.schemaName}/$id")
    }

    suspend fun getOffsetPrices(): List<OffsetPrice> = client.get {
        apiUrl(OffsetPrices.schemaName)
    }

    suspend fun addOffsetPrice(price: OffsetPrice): OffsetPrice? = client.post {
        apiUrl(OffsetPrices.schemaName)
        jsonBody(price)
    }

    suspend fun getOffsetPrice(id: StringId<*>): OffsetPrice = client.get {
        apiUrl("${OffsetPrices.schemaName}/$id")
    }

    suspend fun editOffsetPrice(price: OffsetPrice): Boolean = client.requestStatus(HttpMethod.Put) {
        apiUrl("${OffsetPrices.schemaName}/${price.id}")
        jsonBody(price)
    }

    suspend fun deleteOffsetPrice(id: StringId<*>): Boolean = client.requestStatus(HttpMethod.Delete) {
        apiUrl("${OffsetPrices.schemaName}/$id")
    }

    suspend fun getDigitalPrices(): List<DigitalPrice> = client.get {
        apiUrl(DigitalPrices.schemaName)
    }

    suspend fun addDigitalPrice(price: DigitalPrice): DigitalPrice? = client.post {
        apiUrl(DigitalPrices.schemaName)
        jsonBody(price)
    }

    suspend fun getDigitalPrice(id: StringId<*>): DigitalPrice = client.get {
        apiUrl("${DigitalPrices.schemaName}/$id")
    }

    suspend fun editDigitalPrice(price: DigitalPrice): Boolean =
        client.requestStatus(HttpMethod.Put) {
            apiUrl("${DigitalPrices.schemaName}/${price.id}")
            jsonBody(price)
        }

    suspend fun deleteDigitalPrice(id: StringId<*>): Boolean = client.requestStatus(HttpMethod.Delete) {
        apiUrl("${DigitalPrices.schemaName}/$id")
    }

    suspend fun getEmployees(): List<Employee> = client.get {
        apiUrl(Employees.schemaName)
    }

    suspend fun addEmployee(employee: Employee): Employee? = client.post {
        apiUrl(Employees.schemaName)
        jsonBody(employee)
    }

    suspend fun getEmployee(id: StringId<*>): Employee = client.get {
        apiUrl("${Employees.schemaName}/$id")
    }

    suspend fun editEmployee(edit: Employee, login: CharSequence): Boolean =
        client.requestStatus(HttpMethod.Put) {
            apiUrl("${Employees.schemaName}/${edit.id}")
            jsonBody(edit)
            parameters("login" to login)
        }

    suspend fun deleteEmployee(login: Employee, id: StringId<*>): Boolean = client.requestStatus(HttpMethod.Delete) {
        apiUrl("${Employees.schemaName}/$id")
        parameters("login" to login.name)
    }
}