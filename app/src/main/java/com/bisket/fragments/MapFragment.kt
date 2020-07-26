package com.bisket.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.UiThread
import androidx.fragment.app.Fragment
import com.bisket.R
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.MapView
import com.naver.maps.map.NaverMap
import com.naver.maps.map.OnMapReadyCallback
import com.naver.maps.map.overlay.Marker

class MapFragment : Fragment(), OnMapReadyCallback {

    override fun onCreateView( inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_map, null)
        val mapFragment = view.findViewById<MapView>(R.id.map)
        mapFragment.getMapAsync(this)

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
        }

    }

}