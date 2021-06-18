package com.example.rainnyweather.ui.place

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.example.rainnyweather.logic.Repository
import com.example.rainnyweather.logic.model.Place

/**
 * 每当searchPlaces()函数被调用时，switchMap()方法所对应的转换函数就会执行
 * 然后在转换函数中，我们只需要调用仓库层中定义的searchPlaces()方法就可以发起网络请求，
 * 同时将仓库层返回的LiveData对象转换成一个可供Activity观察的LiveData对象。
 */
class PlaceViewModel :ViewModel() {
    private val searchLiveData = MutableLiveData<String>()

    /* placeList的集合，主要是用来缓存 */
    val placeList = ArrayList<Place>()

    /* Transformations的switchMap()方法来观察这个对象，
       参数：原始数据类型，要返回的数据类型——是从repository中返回的livedata类型，里面包含了返回的数据
    */
    val placeLiveData = Transformations.switchMap(searchLiveData){
        query -> Repository.searchPlaces(query)
    }

    /**
     * 每当searchPlaces()函数被调用时，switchMap()方法所对应的转换函数就会执行
     * @param query: String
     */
    fun searchPlaces(query: String){            // 这个是暴露出来的方法
        searchLiveData.value = query
    }

    /**
     * 持久化存储，只是封装一层，不改变内容，由本类去调用下层的repository的方法
     */
    fun savePlace(place: Place) = Repository.savePlace(place)
    fun getSavedPlace() = Repository.getSavedPlace()
    fun isSavedPlace() = Repository.isPlaceSaved()
}