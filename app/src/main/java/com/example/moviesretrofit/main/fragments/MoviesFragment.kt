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
        childFragmentManager.beginTransaction().replace(
            R.id.popularMoviesContainer, MultiMediaRecyclerFragment(),
            "popularMovies").commit()
    }

    private fun setTopRatedMovies(){
        childFragmentManager.beginTransaction().replace(
            R.id.topRatedMoviesContainer, MultiMediaRecyclerFragment(),
            "ratedMovies").commit()
    }
}