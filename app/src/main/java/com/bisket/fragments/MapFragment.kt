package com.bisket.fragments

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.UiThread
import androidx.fragment.app.Fragment
import com.bisket.R
import com.bisket.naveropenapi.NaverOpenApiClient
import com.bisket.naveropenapi.NaverOpenApiService
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.MapView
import com.naver.maps.map.NaverMap
import com.naver.maps.map.OnMapReadyCallback
import com.naver.maps.map.overlay.Marker
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit

class MapFragment : Fragment(), OnMapReadyCallback {
    lateinit var clientId: String
    lateinit var clentSecret: String
    lateinit var retrofit: Retrofit
    lateinit var naverOpenApiService: NaverOpenApiService

    override fun onCreateView( inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_map, null)
        val mapFragment = view.findViewById<MapView>(R.id.map)
        mapFragment.getMapAsync(this)

        clientId = getString(R.string.naver_open_api_client_id)
        clentSecret = getString(R.string.naver_open_api_client_secret)
        retrofit = NaverOpenApiClient.getInstnace()
        naverOpenApiService = retrofit.create(NaverOpenApiService::class.java)

        return view
    }

    @UiThread
    override fun onMapReady(naverMap: NaverMap) {
        /**
         * UI 설정
         */
        val uiSettings = naverMap.uiSettings
        uiSettings.isCompassEnabled = true
        uiSettings.isLocationButtonEnabled = true

        /**
         * 롱클릭 이벤트 구현
         */
        val marker = Marker() // 마커 선언
        naverMap.setOnMapLongClickListener { point, coord ->
//            Toast.makeText(this, "${coord.latitude}, ${coord.longitude}", Toast.LENGTH_SHORT).show()
            /*
             * 마커 표시
             */
            marker.position = LatLng(coord.latitude, coord.longitude)
            marker.map = naverMap

            Runnable {
                naverOpenApiService.getAreaAddressNameList(clientId, clentSecret, "${coord.longitude},${coord.latitude}")
                    .enqueue(
                        object: Callback<ResponseBody> {
                            override fun onResponse(
                                call: Call<ResponseBody>,
                                response: Response<ResponseBody>
                            ) {
                                val results = response.body()!!
                                Log.d(TAG, results.string())
                                // TODO: 결과값에 대한 클래스 정의 및 적절한 포맷으로 주소값(addr) 출력하기
                            }

                            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                                t.message?.let {
                                    Log.d(TAG, it)
                                }
                            }
                        }
                    )
            }.run()
        }

    }

}