package com.hendraanggrian.openpss.api

import com.hendraanggrian.openpss.db.schemas.Customer
import com.hendraanggrian.openpss.db.schemas.Employee
import com.hendraanggrian.openpss.db.schemas.Log
import kotlinx.coroutines.Deferred
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface Api {

    @POST("login")
    @FormUrlEncoded
    fun login(
        @Field("name") name: String,
        @Field("password") password: String
    ): Deferred<Employee>

    @GET("logs")
    fun getLogs(
        @Query("page") page: Int,
        @Query("count") count: Int
    ): Deferred<Page<Log>>

    @GET("customers")
    fun getCustomers(
        @Query("search") search: String,
        @Query("page") page: Int,
        @Query("count") count: Int
    ): Deferred<Page<Customer>>

    companion object : ApiFactory<Api>(Api::class.java)
}