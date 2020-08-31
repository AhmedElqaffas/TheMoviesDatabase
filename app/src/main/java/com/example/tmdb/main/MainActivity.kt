package com.example.tmdb.main

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.tmdb.R
import com.example.tmdb.login.LoginActivity
import com.example.tmdb.main.fragments.FindMultiMediaFragment
import com.example.tmdb.main.fragments.MoviesFragment
import com.example.tmdb.main.fragments.SeriesFragment
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity(){

    private lateinit var moviesFragment: MoviesFragment
    private lateinit var seriesFragment: SeriesFragment
    private lateinit var findMultimediaFragment: FindMultiMediaFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if(savedInstanceState == null){
            initializeFragments()
        }
        setBottomNavigationListener()
    }

    private fun initializeFragments(){
        moviesFragment = MoviesFragment()
        seriesFragment = SeriesFragment()
        findMultimediaFragment = FindMultiMediaFragment()

        supportFragmentManager.beginTransaction()
            .add(R.id.fragmentsContainer,moviesFragment,"movies")
            .add(R.id.fragmentsContainer,seriesFragment,"series")
            .add(R.id.fragmentsContainer,findMultimediaFragment,"search")
            .hide(findMultimediaFragment).hide(seriesFragment)
            .commit()
    }


    private fun setBottomNavigationListener(){
        bottomNavigationView.setOnNavigationItemSelectedListener {
            moviesFragment = supportFragmentManager.findFragmentByTag("movies") as MoviesFragment
            seriesFragment = supportFragmentManager.findFragmentByTag("series") as SeriesFragment
            findMultimediaFragment = supportFragmentManager.findFragmentByTag("search") as FindMultiMediaFragment
            when(it.itemId){

                R.id.movies -> {
                    showFragment(moviesFragment, listOf(seriesFragment, findMultimediaFragment))
                }
                R.id.series ->{
                    showFragment(seriesFragment, listOf(moviesFragment, findMultimediaFragment))
                }

                R.id.search ->{
                    showFragment(findMultimediaFragment, listOf(moviesFragment, seriesFragment))
                }

                R.id.signOut -> {
                    showSignOutDialog()
                }
            }
            return@setOnNavigationItemSelectedListener true
        }
    }

    private fun showFragment(fragmentToShow: Fragment?, fragmentsToHide: List<Fragment?>){
            val fragmentTransaction = supportFragmentManager.beginTransaction()
            // Show the required fragment
            fragmentTransaction.apply {
                show(fragmentToShow!!)
            }
                .also{
                    // Hide any other found fragment
                    fragmentsToHide.forEach{
                        it?.let{ fragmentTransaction.hide(it)}
                    }
                }
            fragmentTransaction.commit()
    }

    private fun showSignOutDialog(){
        AlertDialog.Builder(this)
            .setTitle("Sign out")
            .setMessage("Are you sure you want to sign out?")
            .setPositiveButton("yes") { _, _ ->
                signOut()
            }
            .setNegativeButton("No"){dialog, _ ->
                dialog.cancel()
                returnToPreviouslyShownScreen()
            }
            .show()
    }

    private fun signOut(){
        FirebaseAuth.getInstance().signOut()
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }

    private fun returnToPreviouslyShownScreen(){
        when {
            moviesFragment.isVisible -> {
                bottomNavigationView.selectedItemId = R.id.movies
            }
            seriesFragment.isVisible -> {
                bottomNavigationView.selectedItemId = R.id.series
            }
            else -> {
                bottomNavigationView.selectedItemId = R.id.search
            }
        }
    }
}