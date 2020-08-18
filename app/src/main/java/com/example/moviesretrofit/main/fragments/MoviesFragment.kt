package com.example.moviesretrofit.main.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.moviesretrofit.R
import kotlinx.android.synthetic.main.fragment_movies.*

class MoviesFragment : Fragment(){

    private lateinit var inflated: View

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        inflated = inflater.inflate(R.layout.fragment_movies, container, false)
        return inflated
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setSwipeRefreshListener()
        if(savedInstanceState == null)
            setMoviesFragments()
    }

    private fun setMoviesFragments() {
        setFavoriteMoviesFragment()
        setPopularMoviesFragment()
        setTopRatedMoviesFragment()
    }

    private fun setFavoriteMoviesFragment(){
        childFragmentManager.beginTransaction()
            .replace(R.id.favoriteMoviesContainer, MultiMediaRecyclerFragment(),
            "favoriteMovies").commit()
    }

    private fun setPopularMoviesFragment() {
        childFragmentManager.beginTransaction().replace(
            R.id.popularMoviesContainer, MultiMediaRecyclerFragment(),
            "popularMovies").commit()
    }

    private fun setTopRatedMoviesFragment(){
        childFragmentManager.beginTransaction().replace(
            R.id.topRatedMoviesContainer, MultiMediaRecyclerFragment(),
            "ratedMovies").commit()
    }

    private fun setSwipeRefreshListener(){
        swipeRefresh.setOnRefreshListener {
            // Recreate the fragments to remake the requests
            setPopularMoviesFragment()
            setTopRatedMoviesFragment()
            // End the refreshing
            swipeRefresh.isRefreshing = false
        }
    }
}