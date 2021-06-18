package com.example.rainnyweather.logic.model

import com.google.gson.annotations.SerializedName

/**
 *  model文件，主要是根据返回值来创建对应的数据模型，对应的api是 baseurl + place?query=beijing
 */
/**
 * 最上层的格式，是按照api的返回值的json中的格式，status、places字段
 * @param status：状态
 * @param places：列表
 */
data class PlaceResponse(val status : String, val places : List<Place>)

/**
 *  api返回值的place字段
 *  @SerializedName，是在GSON库中的，用来将json字段和kotlin字段建立映射
 *  @param name :地址名字
 *  @param location：具体的坐标
 *  @param address：地址（更为具体的名字），json中该字段不是很符合kotlin的命名规范，所以用 @SerializedName 建立映射，formatted_address -- address
 */
data class Place(val name : String, val location : Location, @SerializedName("formatted_address") val address: String)

/**
 * 坐标，包含经度、纬度
 * @param lng：经度
 * @param lat：纬度
 */
data class Location(val lng : String, val lat : String)