package com.hendraanggrian.openpss.ui.main.help

import com.google.common.util.concurrent.ListenableFuture
import com.hendraanggrian.openpss.BuildConfig.ARTIFACT
import com.hendraanggrian.openpss.BuildConfig.USER
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.guava.GuavaCallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET

/** As seen in `https://developer.github.com/v3/`. */
interface GitHubApi {

    @GET("repos/$USER/$ARTIFACT/releases/latest")
    fun getLatestRelease(): ListenableFuture<GitHubRelease>

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
            .addCallAdapterFactory(GuavaCallAdapterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(GitHubApi::class.java)
    }
}