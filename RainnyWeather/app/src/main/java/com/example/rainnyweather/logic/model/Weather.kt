package com.example.rainnyweather.logic.model

data class Weather(val daily: DailyResponse.Daily, val realtime: RealtimeResponse.Realtime)
