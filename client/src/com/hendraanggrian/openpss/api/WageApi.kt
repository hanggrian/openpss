package com.hendraanggrian.openpss.api

import com.hendraanggrian.openpss.data.Wage
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.http.HttpMethod

interface WageApi : Api {

    suspend fun getWages(): List<Wage> = client.get {
        apiUrl("wages")
    }

    suspend fun addWage(wage: Wage): Wage = client.post {
        apiUrl("wages")
        body = wage
    }

    suspend fun getWage(wageId: Int): Wage? = client.get<Any> {
        apiUrl("wages/$wageId")
    } as? Wage

    suspend fun editWage(wageId: Int, daily: Int, hourlyOvertime: Int): Boolean = client.requestStatus(HttpMethod.Put) {
        apiUrl("wages/$wageId")
        parameters(
            "daily" to daily,
            "hourlyOvertime" to hourlyOvertime
        )
    }
}