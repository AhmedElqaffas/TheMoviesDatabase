package com.example.moviesretrofit.main.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.moviesretrofit.R

class MoviesFragment : Fragment(){

    private lateinit var inflated: View

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        inflated = inflater.inflate(R.layout.fragment_movies, container, false)
        return inflated
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if(savedInstanceState == null)
            setMoviesFragments()
    }

    private fun setMoviesFragments() {
        setPopularMoviesFragment()
        setTopRatedMovies()
    }

    private fun setPopularMoviesFragment() {
        val popularMoviesFragmentInstance =
            MultiMediaRecyclerFragment.newInstance(
                1,
                MultiMediaRecyclerFragment.MOVIE
            )
        childFragmentManager.beginTransaction().replace(
            R.id.popularMoviesContainer, popularMoviesFragmentInstance,
            "popular movies").commit()
    }

    private fun setTopRatedMovies(){
        val topRatedMoviesFragmentInstance =
            MultiMediaRecyclerFragment.newInstance(
                2,
                MultiMediaRecyclerFragment.MOVIE
            )
        childFragmentManager.beginTransaction().replace(
            R.id.topRatedMoviesContainer, topRatedMoviesFragmentInstance,
            "top rated movies").commit()
    }
}