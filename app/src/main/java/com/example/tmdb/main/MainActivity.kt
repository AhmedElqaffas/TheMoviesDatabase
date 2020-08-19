package com.example.tmdb.main

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.example.tmdb.*
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