package com.bisket

import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.bisket.fragments.BusinessCardFragment
import com.bisket.fragments.MainFragment
import com.bisket.fragments.MapFragment


class MainActivity : AppCompatActivity() {

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
        val transaction = supportFragmentManager.beginTransaction()
        transaction
            .replace(R.id.content_layout, fragment)
            .commit()

        transaction.addToBackStack(null) // 이전 화면으로 돌아갈 수 있게 하는 설정
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu1, menu)

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            android.R.id.home -> { // 홈 메뉴 버튼
                replaceFragment(supportFragmentManager.fragmentFactory.instantiate(classLoader, MainFragment::class.qualifiedName!!))
            }
            R.id.map_menu -> { // 지도 메뉴 버튼
                replaceFragment(supportFragmentManager.fragmentFactory.instantiate(classLoader, MapFragment::class.qualifiedName!!))
            }
            R.id.business_card_menu -> { // 명함 메뉴 버튼
                replaceFragment(supportFragmentManager.fragmentFactory.instantiate(classLoader, BusinessCardFragment::class.qualifiedName!!))
            }
        }

        return super.onOptionsItemSelected(item)
    }

}