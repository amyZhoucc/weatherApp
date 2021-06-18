package com.example.rainnyweather.ui.place

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.rainnyweather.MainActivity
import com.example.rainnyweather.R
import com.example.rainnyweather.databinding.ActivityMainBinding
import com.example.rainnyweather.databinding.FragmentPlaceBinding
import com.example.rainnyweather.logic.model.Place
import com.example.rainnyweather.ui.weather.WeatherActivity

/**
 * 将具体页面逻辑放在 Fragment 中
 * 首先需要绑定recyclerview，其次去监听输入框的变化情况
 */
class PlaceFragment : Fragment() {
    /** 使用了lazy函数这种懒加载技术来获取PlaceViewModel的实例
     * 允许我们在整个类中随时使用viewModel这个变量，而完全不用关心它何时初始化、是否为空等前提条件 **/
    val viewModel by lazy { ViewModelProvider(this).get(PlaceViewModel::class.java) }

    private lateinit var adapter: PlaceAdapter

    /* binding插件，主要用来直接获取页面组件的对象 */
    private var _binding :  FragmentPlaceBinding?= null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentPlaceBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        /* recyclerview的套路写法 */
        val layoutManager = LinearLayoutManager(activity)
        binding.recyclerView.layoutManager = layoutManager

        adapter = PlaceAdapter(viewModel.placeList, this)
        binding.recyclerView.adapter = adapter

        /* 如果当前页面已经嵌入了mainActivity中，且如果之前有存储过，那么就直接跳转 */
        if(activity is MainActivity && viewModel.isSavedPlace()){
            val place = viewModel.getSavedPlace()
            val intent = Intent(context, WeatherActivity::class.java).apply {
                putExtra("location_lng", place.location.lng)
                putExtra("location_lat", place.location.lat)
                putExtra("place_name", place.name)
            }
            startActivity(intent)
            activity?.finish()
            return
        }
        /* 监听 输入框（searchPlaceEdit）的变化情况 */
        binding.searchPlaceEdit.addTextChangedListener { editable ->
            val content = editable.toString()
            /* 如果输入框有内容，更新livedata中的值，那么会对应发起网络请求 */
            if(content.isNotEmpty()){
                viewModel.searchPlaces(content)
            }
            else{       /* 如果输入框没有内容，清除之前的内容 */
                binding.recyclerView.visibility = View.GONE
                binding.bgImageView.visibility = View.VISIBLE
                viewModel.placeList.clear()
                adapter.notifyDataSetChanged()
            }
        }

        /**
         * 逻辑：如果输入框存在变化，那么会发起网络请求，那么当得到响应时 placeLiveData 是会发生变化的
         * 对PlaceViewModel中的placeLiveData对象进行观察，当有任何数据变化时，就会回调到传入的Observer接口实现中
         */
        viewModel.placeLiveData.observe(viewLifecycleOwner, Observer { result ->
            val places = result.getOrNull()
            if (places != null){
                binding.recyclerView.visibility = View.VISIBLE
                binding.bgImageView.visibility = View.GONE
                viewModel.placeList.clear()
                viewModel.placeList.addAll(places)
                adapter.notifyDataSetChanged()
                Log.d("mainActivity", viewModel.placeList.size.toString())
            }
            else{
                Toast.makeText(activity, "没有查询到该地点",Toast.LENGTH_LONG).show()
                result.exceptionOrNull()?.printStackTrace()
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}