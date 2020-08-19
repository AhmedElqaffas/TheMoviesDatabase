package com.example.tmdb.main

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.example.tmdb.R
import com.google.firebase.firestore.FirebaseFirestore
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