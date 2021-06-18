package com.example.rainnyweather.logic.dao

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import com.example.rainnyweather.RainnyWeatherApplication
import com.example.rainnyweather.logic.model.Place
import com.google.gson.Gson

/**
 * 持久化技术，使用SharedPreferences存储
 * 存储查询过的地址，类型为Place，包含地址名和具体的经纬度
 */
object PlaceDao {
    /**
     * 获取SharedPreferences对象，获得该上下文
     */
    private fun sharedPreferences() = RainnyWeatherApplication.context.getSharedPreferences("rainny_weather", Context.MODE_PRIVATE)

    /**
     * 保存place数据，直接转换成gson格式的字符串
     * @param place: 要存储的数据
     */
    fun savePlace(place : Place){
        sharedPreferences().edit {
            putString("place", Gson().toJson(place))
        }
    }

    /**
     * 获取存储的数据，如果没有就返回""
     */
    fun getSavedPlace() : Place{
        val placeJson = sharedPreferences().getString("place", "")
        return Gson().fromJson(placeJson, Place::class.java)
    }

    /**
     * 判断是否存在持久化的数据
     */
    fun isPlaceSaved() = sharedPreferences().contains("place")
}