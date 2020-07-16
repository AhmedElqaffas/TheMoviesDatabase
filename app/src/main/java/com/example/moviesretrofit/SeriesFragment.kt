package com.example.moviesretrofit

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

class SeriesFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_series, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setSeriesFragments()
    }

    private fun setSeriesFragments() {
        setPopularSeriesFragment()
        setTopRatedSeries()
    }

    private fun setPopularSeriesFragment() {
        val popularMoviesFragmentInstance = MultiMediaRecyclerFragment.newInstance(1, MultiMediaRecyclerFragment.SERIES)
        childFragmentManager.beginTransaction().replace(R.id.popularSeriesContainer, popularMoviesFragmentInstance,
            "popular series").commit()
    }

    private fun setTopRatedSeries(){
        val topRatedMoviesFragmentInstance = MultiMediaRecyclerFragment.newInstance(2, MultiMediaRecyclerFragment.SERIES)
        childFragmentManager.beginTransaction().replace(R.id.topRatedSeriesContainer, topRatedMoviesFragmentInstance,
            "top rated series").commit()
    }
}