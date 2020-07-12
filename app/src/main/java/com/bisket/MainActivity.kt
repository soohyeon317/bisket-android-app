package com.bisket

import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.annotation.UiThread
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.bisket.fragments.BusinessCardFragment
import com.bisket.fragments.MainFragment
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.NaverMap
import com.naver.maps.map.OnMapReadyCallback
import com.naver.maps.map.overlay.Marker


class MainActivity : AppCompatActivity(), OnMapReadyCallback {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        /**
         * ActionBar 설정
         */
        val actionBar = supportActionBar!!
        actionBar.setTitle(R.string.app_name)
        actionBar.setDisplayHomeAsUpEnabled(true)
        actionBar.setBackgroundDrawable(ColorDrawable(0xFF8B0000.toInt()))

        /**
         * 화면 전환 Fragment 선언 및 초기 화면 설정
         */
        supportFragmentManager.beginTransaction()
            .add(R.id.content_layout, MainFragment())
            .commit()
    }

    /**
     * Fragment 전환 메소드
     */
    fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.content_layout, fragment)
            .commit()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu1, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            android.R.id.home -> {
                replaceFragment(supportFragmentManager.fragmentFactory.instantiate(classLoader, MainFragment::class.qualifiedName!!))
            }
            R.id.map_menu -> {
                val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as com.naver.maps.map.MapFragment?
                    ?: com.naver.maps.map.MapFragment.newInstance()
                replaceFragment(mapFragment)
                mapFragment.getMapAsync(this)
            }
            R.id.business_card_menu -> {
                replaceFragment(supportFragmentManager.fragmentFactory.instantiate(classLoader, BusinessCardFragment::class.qualifiedName!!))
            }
        }

        return super.onOptionsItemSelected(item)
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
         * 마커 추가
         */
        val marker = Marker()
        marker.position = LatLng(37.5670135, 126.9783740)
        marker.map = naverMap

        /**
         * 클릭 시, 마커 이동
         */
        naverMap.setOnMapClickListener { point, coord ->
//            Toast.makeText(this, "${coord.latitude}, ${coord.longitude}", Toast.LENGTH_SHORT).show()
            marker.position = LatLng(coord.latitude, coord.longitude)
        }

    }
}