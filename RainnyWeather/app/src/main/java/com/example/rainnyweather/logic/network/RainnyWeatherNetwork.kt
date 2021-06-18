package com.example.rainnyweather.logic.network

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.RuntimeException
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

/**
 * 网络层最上层接，单例类
 * 当外部调用RainnyWeatherNetwork.searchPlaces()函数时，Retrofit就会立即发起网络请求，同时当前的协程也会被阻塞住。
 * 直到服务器响应请求之后，await()函数会将解析出来的数据模型对象取出并返回，
 * 同时恢复当前协程的执行，searchPlaces()函数在得到await()函数的返回值后会将该数据再返回到上一层
 */
object RainnyWeatherNetwork {
    /* 创建了一个PlaceService接口的动态代理对象——可以随便使用placeService中的方法了——searchPlaces方法 */
    private val placeSerivce = ServiceCreator.create<PlaceSerivce>()

    /* 效果同上 */
    private val weatherService = ServiceCreator.create<WeatherService>()

    /* 需要传递的参数是query，之后就会请求网络了（由于要调用下面的await函数，所以必须要将其设置为挂起函数（不然就是创建协程作用域））*/
    suspend fun searchPlaces(query : String) = placeSerivce.searchPlaces(query).await()

    suspend fun getDailyWeather(lng : String, lat : String) = weatherService.getDailyWeather(lng, lat).await()
    suspend fun getRealtimeWeather(lng : String, lat : String) = weatherService.getRealtimeWeather(lng, lat).await()

    /* 设置为挂起函数，返回的数据是T类型
    * 重写里面的回调函数 onResponse, onFailure */
    private suspend fun <T> Call<T>.await() : T {
        return suspendCoroutine { continuation -> enqueue(object : Callback<T>{
            override fun onResponse(call: Call<T>, response: Response<T>) {
                val body = response.body()
                if(body != null) continuation.resume(body)
                else continuation.resumeWithException(RuntimeException("response body is null"))
            }

            override fun onFailure(call: Call<T>, t: Throwable) {
                continuation.resumeWithException(t)
            }
        }) }
    }
}