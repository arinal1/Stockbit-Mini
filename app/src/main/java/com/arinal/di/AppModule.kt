package com.arinal.di

import com.arinal.data.Api
import com.arinal.data.BuildConfig
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

val appModule = module {
    factory { provideHttpLogging() }
    single { provideOkHttpClientBuilder(get()) }
    single { provideApi(get()) }
}

fun provideHttpLogging() = HttpLoggingInterceptor().apply {
    apply { level = HttpLoggingInterceptor.Level.BODY }
}

fun provideOkHttpClientBuilder(loggingInterceptor: HttpLoggingInterceptor) = OkHttpClient().newBuilder()
    .addInterceptor(loggingInterceptor)
    .build()

fun provideApi(okHttp: OkHttpClient): Api = Retrofit.Builder()
    .addConverterFactory(GsonConverterFactory.create())
    .client(okHttp)
    .baseUrl(BuildConfig.BASE_URL)
    .build()
    .create(Api::class.java)
