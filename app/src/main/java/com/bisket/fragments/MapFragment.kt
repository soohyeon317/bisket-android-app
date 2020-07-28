package com.bisket.fragments

import android.content.ContentValues.TAG
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.UiThread
import androidx.fragment.app.Fragment
import com.bisket.R
import com.bisket.dto.ReverseGeoCode.ReverseGeoCodeResponse
import com.bisket.dto.ReverseGeoCode.ReverseGeoCodeResultName
import com.bisket.naveropenapi.NaverOpenApiClient
import com.bisket.naveropenapi.NaverOpenApiService
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.MapView
import com.naver.maps.map.NaverMap
import com.naver.maps.map.OnMapReadyCallback
import com.naver.maps.map.overlay.Marker
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
                        object: Callback<ReverseGeoCodeResponse> {
                            override fun onResponse(
                                call: Call<ReverseGeoCodeResponse>,
                                response: Response<ReverseGeoCodeResponse>
                            ) {
                                val results = response.body()!!.results
                                if (results != null) {
//                                    Log.d(TAG, results.toString())
                                    var partialLandLotAddress = "" // 업체 목록 검색 시에 사용할 지번 주소값
                                    var fullLandLotAddress = "" // 슬라이딩업패널에 뿌릴 지번 주소값
                                    var partialRoadAddress = "" // 업체 목록 검색 시에 사용할 도로명 주소값
                                    var fullRoadAddress = "" // 슬라이딩업패널에 뿌릴 도로명 주소값
                                    for (result in results) {
                                        when(result.name) {
                                            ReverseGeoCodeResultName.addr -> { // 지번 주소 형식인 경우
                                                partialLandLotAddress =
                                                    "${result.region?.area1?.name} ${result.region?.area2?.name} ${result.region?.area3?.name} ${result.region?.area4?.name}".trim()
                                                fullLandLotAddress = TextUtils.concat(fullLandLotAddress, partialLandLotAddress).toString().trim()
                                                if (result.land != null) {
                                                    val land = result.land!!
                                                    if (!TextUtils.isEmpty(land.number1)) {
                                                        fullLandLotAddress = TextUtils.concat(fullLandLotAddress, " ${land.number1!!}").toString().trim()
                                                        if (!TextUtils.isEmpty(land.number2)) {
                                                            fullLandLotAddress = TextUtils.concat(fullLandLotAddress, "-${land.number2!!}").toString().trim()
                                                        }
                                                    }
                                                }
                                            }
                                            ReverseGeoCodeResultName.roadaddr -> { // 도로명 주소 형식인 경우
                                                partialRoadAddress =
                                                    "${result.region?.area1?.name} ${result.region?.area2?.name} ${result.region?.area3?.name} ${result.region?.area4?.name}".trim()
                                                fullRoadAddress = TextUtils.concat(fullRoadAddress, partialRoadAddress).toString().trim()
                                                if (result.land != null) {
                                                    val land = result.land!!
                                                    if (!TextUtils.isEmpty(land.name)) {
                                                        fullRoadAddress = TextUtils.concat(fullRoadAddress, " ${land.name!!}").toString().trim()
                                                        if (!TextUtils.isEmpty(land.number1)) {
                                                            fullRoadAddress = TextUtils.concat(fullRoadAddress, " ${land.number1!!}").toString().trim()
                                                            if (!TextUtils.isEmpty(land.number2)) {
                                                                fullRoadAddress = TextUtils.concat(fullRoadAddress, "-${land.number2!!}").toString().trim()
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                            else -> {}
                                        }
                                    }
                                    val slidingUpPannelTextView1 = view!!.findViewById<TextView>(R.id.sliding_up_pannel_text_view_1)
                                    val slidingUpPannelTextView2 = view!!.findViewById<TextView>(R.id.sliding_up_pannel_text_view_2)
                                    slidingUpPannelTextView1.text = fullLandLotAddress.trim()
                                    slidingUpPannelTextView2.text = fullRoadAddress.trim()
                                }

                            }

                            override fun onFailure(call: Call<ReverseGeoCodeResponse>, t: Throwable) {
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