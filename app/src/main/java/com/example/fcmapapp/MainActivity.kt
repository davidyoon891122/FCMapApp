package com.example.fcmapapp

import android.os.Bundle
import android.util.Log
import android.widget.SearchView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView.OnQueryTextListener
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.fcmapapp.databinding.ActivityMainBinding
import com.naver.maps.geometry.LatLng
import com.naver.maps.geometry.Tm128
import com.naver.maps.map.CameraAnimation
import com.naver.maps.map.CameraUpdate
import com.naver.maps.map.NaverMap
import com.naver.maps.map.OnMapReadyCallback
import com.naver.maps.map.overlay.Marker
import com.naver.maps.map.overlay.OverlayImage
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class MainActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var binding: ActivityMainBinding
    private lateinit var naverMap: NaverMap
    private var isMapInit = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.mapView.onCreate(savedInstanceState)

        binding.mapView.getMapAsync(this)

        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return if (query?.isNotEmpty() == true) {
                    SearchRepository.getGoodRestaurants(query).enqueue(object : Callback<SearchResult> {
                        override fun onResponse(p0: Call<SearchResult>, p1: Response<SearchResult>) {
                            val searchItemList = p1.body()?.items.orEmpty()

                            if (searchItemList.isEmpty()) {
                                Toast.makeText(this@MainActivity, "검색 결과가 없습니다.", Toast.LENGTH_SHORT).show()
                                return
                            } else if (isMapInit.not()) {
                                Toast.makeText(
                                    this@MainActivity,
                                    "오류가 발생했습니다.",
                                    Toast.LENGTH_SHORT
                                ).show()
                                return
                            }

                            Log.e("MainActivity", searchItemList.toString())
                            val markers = searchItemList.map {
                                Marker().apply {
                                    position = LatLng((it.mapy.toDouble() / 10000000), (it.mapx.toDouble() / 10000000))
                                    captionText = it.title
                                    map = naverMap
                                }
                            }


                            Log.e("MainActivity", markers.first().position.toString())



                            val cameraUpdate = CameraUpdate.scrollTo(markers.first().position)
                                .animate(CameraAnimation.Easing)

                            naverMap.moveCamera(cameraUpdate)

                        }

                        override fun onFailure(p0: Call<SearchResult>, p1: Throwable) {
                            p1.printStackTrace()
                        }
                    })
                   false
                } else {
                    true
                }
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return true
            }
        })

    }

    override fun onStart() {
        super.onStart()
        binding.mapView.onStart()
    }

    override fun onResume() {
        super.onResume()
        binding.mapView.onResume()
    }

    override fun onPause() {
        super.onPause()
        binding.mapView.onPause()
    }

    override fun onStop() {
        super.onStop()
        binding.mapView.onStop()
    }

    override fun onDestroy() {
        super.onDestroy()

        binding.mapView.onDestroy()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        binding.mapView.onSaveInstanceState(outState)
    }

    override fun onLowMemory() {
        super.onLowMemory()

        binding.mapView.onLowMemory()
    }

    override fun onMapReady(map: NaverMap) {
        naverMap = map
        isMapInit = true
    }
}