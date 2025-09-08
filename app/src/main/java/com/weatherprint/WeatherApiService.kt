/**
 * ------------------------------------------------------------
 * WepAPIサービス
 * ------------------------------------------------------------
 */
package com.weatherprint

import com.weatherprint.ConstantParameters.Companion.APP_VERSION
import com.weatherprint.ConstantParameters.Companion.APP_NAME
import retrofit2.Call
import retrofit2.http.*

interface WeatherApiService {
    @Headers("User-Agent: $APP_NAME/$APP_VERSION (https://github.com/21ryujin/WeatherPrint)")
    @GET("forecast/")

    fun getWeather(
        @Query("city") city : String
    ) : Call<WeatherData>
}
