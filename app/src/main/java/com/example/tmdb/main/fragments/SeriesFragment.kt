package com.example.tmdb.main.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.tmdb.R
import kotlinx.android.synthetic.main.fragment_movies.*

class SeriesFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_series, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setSwipeRefreshListener()
        if(savedInstanceState == null)
            setSeriesFragments()
    }

    private fun setSeriesFragments() {
        setFavoriteSeriesFragment()
        setPopularSeriesFragment()
        setTopRatedSeriesFragment()
    }

    private fun setFavoriteSeriesFragment() {
        childFragmentManager.beginTransaction().replace(
            R.id.favoriteSeriesContainer, MultiMediaRecyclerFragment(),
            "favoriteSeries").commit()
    }

    private fun setPopularSeriesFragment() {
        childFragmentManager.beginTransaction().replace(
            R.id.popularSeriesContainer, MultiMediaRecyclerFragment(),
            "popularSeries").commit()
    }

    private fun setTopRatedSeriesFragment(){
            childFragmentManager.beginTransaction().replace(
            R.id.topRatedSeriesContainer, MultiMediaRecyclerFragment(),
            "ratedSeries").commit()
    }

    private fun setSwipeRefreshListener(){
        swipeRefresh.setOnRefreshListener {
            // Recreate the fragments to remake the requests
            setPopularSeriesFragment()
            setTopRatedSeriesFragment()
            // End the refreshing
            swipeRefresh.isRefreshing = false
        }
    }
}