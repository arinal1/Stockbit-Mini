package com.arinal.data.model

import com.google.gson.annotations.SerializedName

data class WatchListModel(
    @SerializedName("Message")
    val message: String = "",
    @SerializedName("MetaData")
    val metaData: MetaData = MetaData(),
    @SerializedName("Data")
    val data: List<Data> = listOf()
) {
    data class MetaData(
        @SerializedName("Count")
        val count: Int = 0
    )

    data class Data(
        @SerializedName("CoinInfo")
        val coinInfo: CoinInfo = CoinInfo(),
        @SerializedName("DISPLAY")
        val display: Display = Display()
    ) {
        data class CoinInfo(
            @SerializedName("Id")
            val id: String = "",
            @SerializedName("Name")
            val name: String = "",
            @SerializedName("FullName")
            val fullName: String = "",
        )

        data class Display(
            @SerializedName("IDR")
            val idr: IDR = IDR()
        ) {
            data class IDR(
                @SerializedName("PRICE")
                val price: String = "",
                @SerializedName("CHANGE24HOUR")
                val change: String = ""
            )
        }
    }
}
