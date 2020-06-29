package com.bisket

import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        supportActionBar!!.setTitle("비스켓")
        supportActionBar!!.setBackgroundDrawable(ColorDrawable(0xFF8B0000.toInt()))
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu1, menu)

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.map_menu -> {
                // TODO
            }
            R.id.business_card_menu -> {
                // TODO
            }
        }


        return super.onOptionsItemSelected(item)
    }
}