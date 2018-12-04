package com.hendraanggrian.openpss.api

import com.hendraanggrian.openpss.db.schemas.Employee
import kotlinx.coroutines.Deferred
import retrofit2.http.POST

interface Api {

    @POST("login")
    fun login(): Deferred<Employee>

    companion object : ApiFactory<Api>(Api::class.java)
}