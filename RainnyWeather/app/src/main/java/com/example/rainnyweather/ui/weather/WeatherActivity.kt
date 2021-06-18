package com.example.rainnyweather.ui.weather

import android.content.Context
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.rainnyweather.R
import com.example.rainnyweather.logic.model.Weather
import com.example.rainnyweather.logic.model.getSky
import java.text.SimpleDateFormat
import java.util.*


class WeatherActivity : AppCompatActivity() {
    val viewModel by lazy { ViewModelProvider(this).get(WeatherViewModel::class.java) }
    lateinit var swipeRefreshLayout : SwipeRefreshLayout

    @RequiresApi(Build.VERSION_CODES.R)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_weather)
        swipeRefreshLayout = findViewById(R.id.swiperefresh)
        /* 上方状态栏颜色适配，该方法已经被废弃了，需要找替换的！！！！！！ */
        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_STABLE)
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }

        if(viewModel.locationLng.isEmpty()){
            viewModel.locationLng = intent.getStringExtra("location_lng") ?: ""
        }
        if(viewModel.locationLat.isEmpty()){
            viewModel.locationLat = intent.getStringExtra("location_lat") ?: ""
        }
        if(viewModel.place.isEmpty()){
            viewModel.place = intent.getStringExtra("place_name") ?: ""
        }
        viewModel.weather.observe(this, Observer { result ->
            val weather = result.getOrNull()
            if(weather != null){
                showWeatherInfo(weather)
            }
            else{
                Toast.makeText(this, "无法获取天气信息", Toast.LENGTH_LONG).show()
                result.exceptionOrNull()?.printStackTrace()
            }
            swipeRefreshLayout.isRefreshing = false
        })
        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary)
        refresh()
        swipeRefreshLayout.setOnRefreshListener {
            refresh()
        }
        val navigButton : Button = findViewById(R.id.navButton)
        val drawerLayout : DrawerLayout = findViewById(R.id.drawerLayout)
        navigButton.setOnClickListener {
            drawerLayout.openDrawer(GravityCompat.START)
        }
        drawerLayout.addDrawerListener(object : DrawerLayout.DrawerListener{
            override fun onDrawerStateChanged(newState: Int) {}

            override fun onDrawerSlide(drawerView: View, slideOffset: Float) {}

            override fun onDrawerOpened(drawerView: View) {}

            override fun onDrawerClosed(drawerView: View) {
                val manager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                manager.hideSoftInputFromWindow(drawerView.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
            }
        })


    }
    fun refresh(){
        viewModel.refreshWeather(viewModel.locationLng, viewModel.locationLat)
        swipeRefreshLayout.isRefreshing = true
    }
    private fun showWeatherInfo(weather : Weather){
        /* 获得组件——在now.xml中 */
        val placeNameText : TextView = findViewById(R.id.placeName)
        val currentTempText : TextView = findViewById(R.id.currentTemp)
        val currentSkyText : TextView = findViewById(R.id.currentSky)
        val currentAQIText : TextView = findViewById(R.id.currentAQI)
        val nowLayout : RelativeLayout = findViewById(R.id.nowLayout)

        placeNameText.text = viewModel.place            // 输出地址
        val realtime = weather.realtime
        val daily = weather.daily

        /* 填充now.xml中的显示 */
        val currentTempData = "${realtime.temperature.toInt()} °C"
        currentTempText.text = currentTempData
        currentSkyText.text = getSky(realtime.skycon).info
        val curretPM25Text = "空气指数 ${realtime.airQuality.aqi.chn.toInt()}"
        currentAQIText.text = curretPM25Text
        nowLayout.setBackgroundResource(getSky(realtime.skycon).bg)

        /* 获得组件——在forecast.xml中 */
        val forecastLayout : LinearLayout = findViewById(R.id.forecastLayout)

        /* 填充forecast.xml中的显示 */
        forecastLayout.removeAllViews()
        val days = daily.skycon.size            // 统计预测的天数
        for(i in 0 until days){
            val skycon = daily.skycon[i]
            val temperature = daily.temperature[i]
            val view = LayoutInflater.from(this).inflate(R.layout.forecast_item, forecastLayout, false)
            val dataInfo = view.findViewById(R.id.dateInfo) as TextView
            val skyIcon = view.findViewById(R.id.skyIcon) as ImageView
            val skyText = view.findViewById(R.id.skyInfo) as TextView
            val temperatureInfo = view.findViewById(R.id.tempInfo) as TextView
            val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            dataInfo.text = simpleDateFormat.format(skycon.date)
            val sky = getSky(skycon.value)
            skyIcon.setImageResource(sky.icon)
            skyText.text = sky.info
            val tempText = "${temperature.min.toInt()} - ${temperature.max.toInt()} °C"
            temperatureInfo.text = tempText
            forecastLayout.addView(view)
        }

        /* 填充life_index.xml中的显示 */
        val lifeIndex = daily.lifeIndex
        val coldRiskView : TextView = findViewById(R.id.coldRiskText)
        val dressingView : TextView = findViewById(R.id.dressingText)
        val ultravioletView : TextView = findViewById(R.id.ultravioletText)
        val carWashingView : TextView = findViewById(R.id.carWasingText)
        coldRiskView.text = lifeIndex.coldRisk[0].desc
        dressingView.text = lifeIndex.dressing[0].desc
        ultravioletView.text = lifeIndex.ultraviolet[0].desc
        carWashingView.text = lifeIndex.carWashing[0].desc

        val weatherLayout : ScrollView = findViewById(R.id.weatherLayout)
        weatherLayout.visibility = View.VISIBLE
    }
}