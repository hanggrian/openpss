package com.hendraanggrian.openpss.api

import com.jakewharton.retrofit2.adapter.kotlin.coroutines.experimental.CoroutineCallAdapterFactory
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

open class ApiFactory<T>(cls: Class<T>, endPoint: String = "localhost:8080") {

    val api = Retrofit.Builder()
        .client(OkHttpClient.Builder().addInterceptor {
            it.proceed(
                it.request()
                    .newBuilder()
                    .addHeader("Accept", "application/json")
                    .build()
            )
        }.build())
        .baseUrl(endPoint)
        .addCallAdapterFactory(CoroutineCallAdapterFactory())
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(cls)
}