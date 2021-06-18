package com.example.rainnyweather.logic.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * 创建一个Retrofit构建器
 * 创建过程中需要带的是：baseUrl、addConverterFactory
 * 将baseUrl作为固定字段带入
 */
object ServiceCreator {
    /* 固定化写法 */
    private const val BASE_URL = "https://api.caiyunapp.com/"
    private val retrofit = Retrofit.Builder().baseUrl(BASE_URL).addConverterFactory(
        GsonConverterFactory.create()
    ).build()

    /**
     * 效果：可以直接使用serviceClass中的方法了，这边就是PlaceSerivce，而serviceClass就是对应的传参
     * 更具体的就是：下面传递的泛型
     */
    fun<T>create(serviceClass : Class<T>) :T = retrofit.create(serviceClass)

    /**
     * 内联方法，可以直接将泛型转换成对应传入的具体类型
     */
    inline fun <reified T>create() : T = create(T::class.java)
}