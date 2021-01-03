package com.arinal.data

import com.arinal.data.model.WatchListModel
import retrofit2.http.GET
import retrofit2.http.Query

interface Api {

    @GET("top/totaltoptiervolfull")
    suspend fun getWatchlist(
        @Query("page") page: Int,
        @Query("limit") limit: Int = 50,
        @Query("tsym") tsym: String = "IDR",
        @Query("api_key") apiKey: String = BuildConfig.API_KEY,
    ): WatchListModel

}
