/**
 * ------------------------------------------------------------
 * WepAPIリクエスト
 * ------------------------------------------------------------
 */
package com.weatherprint

import com.google.gson.GsonBuilder
import com.weatherprint.ConstantParameters.Companion.BASE_URL
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RetrofitClient {

    // APIインターフェイスをインスタンス化
    private var service: WeatherApiService

    // JSON変換の定義とリクエストの構築
    init {
        val gsonFactory = GsonConverterFactory.create(GsonBuilder().serializeNulls().create())

        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(gsonFactory)
            .build()

        service = retrofit.create(WeatherApiService::class.java)
    }

    // APIコール
    fun getService(): WeatherApiService {
        return service
    }

}