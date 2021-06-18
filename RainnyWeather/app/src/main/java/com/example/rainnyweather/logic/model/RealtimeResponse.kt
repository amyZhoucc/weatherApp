package com.example.rainnyweather.logic.model

import com.google.gson.annotations.SerializedName

/**
 *  model文件，主要是根据返回值来创建对应的数据模型，对应的api是 https://api.caiyunapp.com/v2.5/{token}/lng,lat/realtime.json
 */
/**
 * 完全按照json格式来构建的
 * @param
 */
data class RealtimeResponse(val status : String, val result: Result){
    data class Result(val realtime : Realtime)
    data class Realtime(val skycon : String, val temperature : Float, @SerializedName("air_quality") val airQuality: AirQuality )
    data class AirQuality(val aqi : AQI)
    data class AQI(val chn : Float)
}
