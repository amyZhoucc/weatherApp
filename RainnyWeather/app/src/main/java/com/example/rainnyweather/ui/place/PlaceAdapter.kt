package com.example.rainnyweather.ui.place

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.example.rainnyweather.R
import com.example.rainnyweather.logic.model.Place
import com.example.rainnyweather.ui.weather.WeatherActivity

/**
 * 这个是ui层的代码
 * 主要是用到了 recyclerview，所以需要创建对应的adapte，都是套路写法：创建内部类、重写3个方法
 * @param placeList: List<Place>
 * @param fragment: Fragment
 */
class PlaceAdapter(private val placeList: List<Place>, private val fragment : PlaceFragment) : RecyclerView.Adapter<PlaceAdapter.ViewHolder>() {
    /**
     * 创建内部类，主要继承自 RecyclerView.ViewHolder，对应的是recycerview中需要展示的页面
     */
    inner class ViewHolder(view : View) : RecyclerView.ViewHolder(view){
        val placeName : TextView = view.findViewById(R.id.placeName)
        val placeAddress : TextView = view.findViewById(R.id.placeAddress)
    }

    /**
     * recyclerview中需要对应重写的方法1，绑定对应的item的页面
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.place_item, parent, false)
        val holder = ViewHolder(view)
        holder.itemView.setOnClickListener{
            val position = holder.absoluteAdapterPosition
            val place = placeList[position]
            val activity = fragment.activity
            if(activity is WeatherActivity){
                val drawerLayout : DrawerLayout = activity.findViewById(R.id.drawerLayout)
                drawerLayout.closeDrawers()
                activity.viewModel.locationLng = place.location.lng
                activity.viewModel.locationLat = place.location.lat
                activity.viewModel.place = place.name
                activity.refresh()
            }
            else{
                val intent = Intent(parent.context, WeatherActivity::class.java).apply{
                    putExtra("location_lng", place.location.lng)
                    putExtra("location_lat", place.location.lat)
                    putExtra("place_name", place.name)
                }
                fragment.startActivity(intent)
                fragment.activity?.finish()
            }
            fragment.viewModel.savePlace(place)         // 保存该点击的地址
        }

        return holder
    }

    /**
     * recyclerview中需要对应重写的方法2，设定具体的显示内容
     */
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val place = placeList[position]
        holder.placeName.text = place.name
        holder.placeAddress.text = place.address
    }

    /**
     * recyclerview中需要对应重写的方法3，统计item个数
     */
    override fun getItemCount() = placeList.size
}