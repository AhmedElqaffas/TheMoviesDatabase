package com.example.moviesretrofit.networking

import com.example.moviesretrofit.Movie

data class PopularMoviesResponse(val page: Int, val results: List<Movie>)