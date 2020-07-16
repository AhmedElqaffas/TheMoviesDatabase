package com.example.moviesretrofit

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import kotlinx.android.synthetic.main.activity_main.*


/**
 * THIS MAY BE A LITTLE ADVANCED. A MORE SIMPLE EXAMPLE TO STUDY IS AT THIS PROJECT FIRST COMMIT
 * NAMED: "Initial app". REFER TO IT IF YOU HAVE DIFFICULTY STUDYING THIS EXAMPLE.
 * STAY STRONG, FUTURE ME.
 * SINCERELY, PAST YOU
 */
class MainActivity : AppCompatActivity(){


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setViewPagerAdapter()
        linkViewPagerAndBottomNavigation()
    }


    private fun setViewPagerAdapter() {
        val fragmentsList = listOf(MoviesFragment(),
            SeriesFragment(),
            FindMultiMediaFragment() as Fragment)
        viewPager.adapter = ViewPagerAdapter(fragmentsList,supportFragmentManager, lifecycle)
    }

    private fun linkViewPagerAndBottomNavigation() {
        setViewPagerChangeListener()
        setupBottomNavigationChangeListener()
    }

    private fun setViewPagerChangeListener() {
        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                bottomNavigationView.menu.getItem(position).isChecked = true
            }
        })
    }

    private fun setupBottomNavigationChangeListener() {
        bottomNavigationView.setOnNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.movies ->
                    viewPager.setCurrentItem(0, true)
                R.id.series ->
                    viewPager.setCurrentItem(1, true)
                R.id.search ->
                    viewPager.setCurrentItem(2,true)
            }
            true
        }
    }
}