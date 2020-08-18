package com.example.moviesretrofit.main

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import androidx.viewpager2.widget.ViewPager2
import com.example.moviesretrofit.*
import com.example.moviesretrofit.main.fragments.FindMultiMediaFragment
import com.example.moviesretrofit.main.fragments.MoviesFragment
import com.example.moviesretrofit.main.fragments.SeriesFragment
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(){


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setNavigationComponentAdapter()
    }

    private fun setNavigationComponentAdapter() {
        val navigationController: NavController = findNavController(R.id.fragmentsContainer)
        bottomNavigationView.setupWithNavController(navigationController)
    }



}