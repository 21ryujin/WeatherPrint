/**
 * ------------------------------------------------------------
 * WepAPIサービス
 * ------------------------------------------------------------
 */
package com.weatherprint

import com.weatherprint.ConstantParameters.Companion.APP_VERSION
import retrofit2.Call
import retrofit2.http.*

interface WeatherApiService {
    @Headers("User-Agent: WeatherPrint for SUNMI V series/$APP_VERSION (https://github.com/21ryujin/WeatherPrint)")
    @GET("forecast/")

    fun getWeather(
        @Query("city") city : String
    ) : Call<WeatherData>
}
