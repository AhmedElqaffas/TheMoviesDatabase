package com.example.moviesretrofit.main.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.moviesretrofit.R

class SeriesFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_series, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if(savedInstanceState == null)
            setSeriesFragments()
    }

    private fun setSeriesFragments() {
        setFavoriteSeriesFragment()
        setPopularSeriesFragment()
        setTopRatedSeries()
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

    private fun setTopRatedSeries(){
childFragmentManager.beginTransaction().replace(
            R.id.topRatedSeriesContainer, MultiMediaRecyclerFragment(),
            "ratedSeries").commit()
    }
}