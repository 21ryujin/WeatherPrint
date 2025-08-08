/**
 * ------------------------------------------------------------
 * WepAPIサービス
 * ------------------------------------------------------------
 */
package com.weatherprint

import retrofit2.Call
import retrofit2.http.*

interface WeatherApiService {
    @GET("forecast/")

    fun getWeather(
        @Query("city") city : String
    ) : Call<WeatherData>
}
