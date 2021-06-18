package com.example.rainnyweather.logic.network

import com.example.rainnyweather.RainnyWeatherApplication
import com.example.rainnyweather.logic.model.DailyResponse
import com.example.rainnyweather.logic.model.RealtimeResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface WeatherService {
    @GET("v2.5/${RainnyWeatherApplication.TOKEN}/{lng},{lat}/realtime.json")
    fun getRealtimeWeather(@Path("lng")lng : String, @Path("lat")lat : String) : Call<RealtimeResponse>

    @GET("v2.5/${RainnyWeatherApplication.TOKEN}/{lng},{lat}/daily.json")
    fun getDailyWeather(@Path("lng")lng: String, @Path("lat")lat: String) : Call<DailyResponse>
}