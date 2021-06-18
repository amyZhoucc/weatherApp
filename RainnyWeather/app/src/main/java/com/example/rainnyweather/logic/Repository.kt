package com.example.rainnyweather.logic

import androidx.lifecycle.liveData
import com.example.rainnyweather.logic.dao.PlaceDao
import com.example.rainnyweather.logic.model.Place
import com.example.rainnyweather.logic.model.PlaceResponse
import com.example.rainnyweather.logic.model.Weather
import com.example.rainnyweather.logic.network.RainnyWeatherNetwork
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import okhttp3.Dispatcher
import retrofit2.awaitResponse
import java.lang.Exception
import java.lang.RuntimeException

/**
 * 作为仓库层的统一封装入口
 * liveData()函数，可以自动构建并返回一个LiveData对象
 * 一般在仓库层中定义的方法，为了能将异步获取的数据以响应式编程的方式通知给上一层，通常会返回一个LiveData对象
 * liveData函数会在代码块中提供一个挂起函数的上下文
 */
object Repository {
    /**
     * 线程参数类型指定成了Dispatchers.IO，这样代码块中的所有代码就都运行在子线程中了
     * 因为网络请求啥的都不能在主线程中进行，所以在这边进行主子线程转换
     * @param query：请求的地址
     */
    fun searchPlaces(query: String) = liveData(Dispatchers.IO) {
        val result = try {
            /** 调用searchPlaces方法，如果成功，就能进入到此，
             * 返回的数据是PlaceResponse类型的，否则就会跑出异常，被下面的所接受 */
            val placeResponse = RainnyWeatherNetwork.searchPlaces(query)
            if(placeResponse.status == "ok"){
                val places = placeResponse.places
                Result.success(places)          // 用Result进行包装
            }
            else{
                Result.failure(RuntimeException("response status is ${placeResponse.status}"))      // 用Result进行包装

            }
        }catch (e : Exception){
            Result.failure<List<Place>>(e)      // 用Result进行包装
        }
        emit(result)       // 将包装好的结果发射出去，类似于liveData中的setValue方法来通知数据变化
    }

    /* 逻辑同上
    * 获取实时天气信息和获取未来天气信息这两个请求是没有先后顺序的，因此让它们并发执行可以提升程序的运行效率——使用 async 函数
    * 而 async 只能在协程的作用域内才行，所以创建协程作用域 */
    fun refreshWeather(lng : String, lat : String) = liveData(Dispatchers.IO) {
        val result = try {
            coroutineScope {
                val deferredRealtime = async {
                    RainnyWeatherNetwork.getRealtimeWeather(lng, lat)
                }
                val deferredDaily = async {
                    RainnyWeatherNetwork.getDailyWeather(lng, lat)
                }
                val realtimeResponse = deferredRealtime.await()
                val dailyResponse = deferredDaily.await()
                if(realtimeResponse.status == "ok" && dailyResponse.status == "ok"){
                    val weather = Weather(dailyResponse.result.daily, realtimeResponse.result.realtime)
                    Result.success(weather)
                }
                else{
                    Result.failure(RuntimeException("realtime response status is ${realtimeResponse.status}" + "daily response status is ${dailyResponse.status}"))
                }
            }
        }catch (e : Exception){
            Result.failure<Weather>(e)
        }
        emit(result)
    }

    /**
     * 对持久化数据的封装，由repository统一处理下层接口
     */
    fun savePlace(place: Place) = PlaceDao.savePlace(place)
    fun getSavedPlace() = PlaceDao.getSavedPlace()
    fun isPlaceSaved() = PlaceDao.isPlaceSaved()

}