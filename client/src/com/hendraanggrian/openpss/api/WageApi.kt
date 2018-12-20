package com.hendraanggrian.openpss.api

import com.hendraanggrian.openpss.db.schemas.Wage
import io.ktor.client.request.get
import io.ktor.client.request.post

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

    suspend fun editWage(wageId: Int, daily: Int, hourlyOvertime: Int): Boolean = client.requestStatus {
        apiUrl("wages/$wageId")
        parameters(
            "daily" to daily,
            "hourlyOvertime" to hourlyOvertime
        )
    }
}