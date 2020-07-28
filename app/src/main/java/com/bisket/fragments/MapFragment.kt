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
import com.naver.maps.map.LocationTrackingMode
import com.naver.maps.map.MapView
import com.naver.maps.map.NaverMap
import com.naver.maps.map.OnMapReadyCallback
import com.naver.maps.map.overlay.Marker
import com.naver.maps.map.util.FusedLocationSource
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit

class MapFragment : Fragment(), OnMapReadyCallback {

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1000
    }

    private lateinit var clientId: String
    private lateinit var clentSecret: String
    private lateinit var retrofit: Retrofit
    private lateinit var naverOpenApiService: NaverOpenApiService
    private lateinit var locationSource: FusedLocationSource
    private lateinit var naverMap: NaverMap

    override fun onCreateView( inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_map, null)
        val mapFragment = view.findViewById<MapView>(R.id.map)
        mapFragment.getMapAsync(this)

        clientId = getString(R.string.naver_open_api_client_id)
        clentSecret = getString(R.string.naver_open_api_client_secret)
        retrofit = NaverOpenApiClient.getInstnace()
        naverOpenApiService = retrofit.create(NaverOpenApiService::class.java)

        /*
        FusedLocationSource는 런타임 권한 처리를 위해 액티비티 또는 프래그먼트를 필요로 합니다.
        생성자에 액티비티나 프래그먼트 객체를 전달하고 권한 요청 코드를 지정해야 합니다.
         */
        locationSource = FusedLocationSource(this, LOCATION_PERMISSION_REQUEST_CODE)

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
         * 위치 추적 기능 설정
         */
        this.naverMap = naverMap
        naverMap.locationSource = locationSource
        naverMap.locationTrackingMode = LocationTrackingMode.Follow

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

    /**
     * onRequestPermissionResult()의 결과를 FusedLocationSource의 onRequestPermissionsResult()에 전달해야 합니다.
     */
    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>,
                                            grantResults: IntArray) {
        if (locationSource.onRequestPermissionsResult(requestCode, permissions,
                grantResults)) {
            if (!locationSource.isActivated) { // 권한 거부됨
                naverMap.locationTrackingMode = LocationTrackingMode.None
            }
            return
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

}