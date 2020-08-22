package com.example.tmdb.main

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.tmdb.R
import com.example.tmdb.main.fragments.FindMultiMediaFragment
import com.example.tmdb.main.fragments.MoviesFragment
import com.example.tmdb.main.fragments.SeriesFragment
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
            val existingMoviesFragment = supportFragmentManager.findFragmentByTag("movies")
            val existingSeriesFragment = supportFragmentManager.findFragmentByTag("series")
            val existingSearchFragment = supportFragmentManager.findFragmentByTag("search")
            when(it.itemId){

                R.id.movies -> {
                    showFragment(existingMoviesFragment, listOf(existingSeriesFragment, existingSearchFragment))
                }
                R.id.series ->{
                    showFragment(existingSeriesFragment, listOf(existingMoviesFragment, existingSearchFragment))
                }

                R.id.search ->{
                    showFragment(existingSearchFragment, listOf(existingMoviesFragment, existingSeriesFragment))
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

}