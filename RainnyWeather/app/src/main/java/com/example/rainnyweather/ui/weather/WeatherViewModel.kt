package com.example.rainnyweather.ui.weather

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.example.rainnyweather.logic.Repository
import com.example.rainnyweather.logic.model.Location
import kotlin.math.ln

class WeatherViewModel : ViewModel(){
    private val locationLiveData  = MutableLiveData<Location>()

    /* 用来缓存的，是存在变化的，所以用var，主要是避免屏幕旋转造成丢失 */
    var locationLat = ""
    var locationLng = ""
    var place = ""

    val weather = Transformations.switchMap(locationLiveData){
        loc -> Repository.refreshWeather(loc.lng, loc.lat)
    }

    fun refreshWeather( lng : String, lat : String){
        locationLiveData.value = Location(lng, lat)
    }
}