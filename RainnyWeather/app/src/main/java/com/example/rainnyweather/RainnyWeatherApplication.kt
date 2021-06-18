package com.example.rainnyweather

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context

/**
 * 一种技巧写法
 * 全局获取context，那么activity在初始化之后，会有一个全局的context
 * 并且将api需要带的 token 也写死在里面了
 */
class RainnyWeatherApplication : Application() {
    companion object{
        @SuppressLint("StaticFieldLeak")
        lateinit var context: Context
    }

    override fun onCreate() {
        super.onCreate()
        context = applicationContext
    }
}