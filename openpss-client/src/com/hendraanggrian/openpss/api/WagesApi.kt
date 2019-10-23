package com.hendraanggrian.openpss.api

import com.hendraanggrian.openpss.schema.Wage
import com.hendraanggrian.openpss.schema.Wages
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.http.HttpMethod

interface WagesApi : Api {

    suspend fun getWages(): List<Wage> = client.get {
        apiUrl(Wages.schemaName)
    }

    suspend fun addWage(wage: Wage): Wage = client.post {
        apiUrl(Wages.schemaName)
        jsonBody(wage)
    }

    suspend fun getWage(wageId: Int): Wage? = client.get<Wage> {
        apiUrl("${Wages.schemaName}/$wageId")
    }.takeUnless { it == Wage.NOT_FOUND }

    suspend fun editWage(wage: Wage): Boolean = client.requestStatus(HttpMethod.Put) {
        apiUrl("${Wages.schemaName}/${wage.wageId}")
        jsonBody(wage)
    }
}
