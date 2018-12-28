package com.hendraanggrian.openpss.api

import com.hendraanggrian.openpss.data.Wage
import com.hendraanggrian.openpss.schema.Wages
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.http.HttpMethod

interface WageApi : Api {

    suspend fun getWages(): List<Wage> = client.get {
        apiUrl("$Wages")
    }

    suspend fun addWage(wage: Wage): Wage = client.post {
        apiUrl("$Wages")
        body = wage
    }

    suspend fun getWage(wageId: Int): Wage? = client.get<Any> {
        apiUrl("$Wages/$wageId")
    } as? Wage

    suspend fun editWage(wageId: Int, daily: Int, hourlyOvertime: Int): Boolean = client.requestStatus(HttpMethod.Put) {
        apiUrl("$Wages/$wageId")
        parameters(
            "daily" to daily,
            "hourlyOvertime" to hourlyOvertime
        )
    }
}