package com.hendraanggrian.openpss.api

import com.google.gson.GsonBuilder
import com.hendraanggrian.openpss.BuildConfig.ARTIFACT
import com.hendraanggrian.openpss.BuildConfig.USER
import com.hendraanggrian.openpss.time.PATTERN_DATETIME_EXTENDED
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET

interface GitHubApi {

    @GET("repos/$USER/$ARTIFACT/releases/latest")
    fun getLatestRelease(): LatestRelease

    companion object {
        private const val END_POINT = "https://api.github.com"

        fun create(): GitHubApi = Retrofit.Builder()
            .client(OkHttpClient.Builder().addInterceptor {
                it.proceed(it.request()
                    .newBuilder()
                    .addHeader("Accept", "application/json")
                    .build())
            }.build())
            .baseUrl(END_POINT)
            .addConverterFactory(GsonConverterFactory.create(GsonBuilder()
                .setDateFormat(PATTERN_DATETIME_EXTENDED)
                .create()))
            .build().create(GitHubApi::class.java)
    }
}