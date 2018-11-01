package com.example.tyler.movies

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.tyler.movies.overview.OverviewFragment

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    override fun onStart() {
        super.onStart()
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, OverviewFragment.newInstance(), OverviewFragment.TAG)
            .addToBackStack(OverviewFragment.TAG)
            .commit()
    }
}
