package com.example.data.remote

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit

object ApiClient {
    private const val BASE_URL = "https://app.cashpay-all.com/"

    @Volatile
    var sessionToken: String? = null

    @Volatile
    var tokenProvider: (() -> String?)? = null

    private val moshi = Moshi.Builder()
        .addLast(KotlinJsonAdapterFactory())
        .build()

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val authInterceptor = Interceptor { chain ->
        val original = chain.request()
        val builder = original.newBuilder()

        val token = sessionToken ?: tokenProvider?.invoke()
        builder.addHeader("Accept", "application/json")
        builder.addHeader("User-Agent", "CashPay-Android/2.0")
        if (!token.isNullOrBlank()) {
            if (sessionToken == null) {
                sessionToken = token
            }
            builder.addHeader("Cookie", "cashpay-token=$token; token=$token")
            builder.addHeader("cashpay-token", token)
            builder.addHeader("Authorization", "Bearer $token")
        }

        val response = chain.proceed(builder.build())

        // Capture cookie if the server sends Set-Cookie
        val cookies = response.headers("Set-Cookie")
        for (cookie in cookies) {
            if (cookie.contains("cashpay-token=")) {
                val extracted = cookie.substringAfter("cashpay-token=").substringBefore(";")
                if (extracted.isNotBlank()) {
                    sessionToken = extracted
                }
            } else if (cookie.contains("token=")) {
                val extracted = cookie.substringAfter("token=").substringBefore(";")
                if (extracted.isNotBlank() && sessionToken == null) {
                    sessionToken = extracted
                }
            }
        }

        response
    }

    val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(authInterceptor)
        .addInterceptor(loggingInterceptor)
        .connectTimeout(45, TimeUnit.SECONDS)
        .readTimeout(45, TimeUnit.SECONDS)
        .writeTimeout(45, TimeUnit.SECONDS)
        .retryOnConnectionFailure(true)
        .build()

    val apiService: CashPayApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
            .create(CashPayApiService::class.java)
    }
}
