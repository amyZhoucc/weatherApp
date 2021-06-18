package com.example.rainnyweather.logic.network

import com.example.rainnyweather.RainnyWeatherApplication
import com.example.rainnyweather.logic.model.PlaceResponse
import retrofit2.Call  // 注意导包的问题，需要选择的是这个，而不是telecom
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * 本文件是network层，主要是设定服务：抽象接口——地址请求
 * 主要用到的是retrofit
 * 是：获取地址服务：PlaceService
 */
/**
 * 完全按照获取经纬度的url来写的
 * url：https://api.caiyunapp.com/v2/place?query=beijing&token={token}&lang=zh_CN
 * query字段是需要指定的，其他的都直接写死
 */
interface PlaceSerivce {
    /**
     * @param query: String，要查询的地址
     * @GET，retrofit库的，能够自动将传递的键值对插入到url中
     * retrofit能够自动将json转换成response格式（Response在model-PlaceResponse中定义）
     */
    @GET("v2/place?token=${RainnyWeatherApplication.TOKEN}&&lang=zh_CN")
    fun searchPlaces(@Query("query") query : String) : Call<PlaceResponse>
}