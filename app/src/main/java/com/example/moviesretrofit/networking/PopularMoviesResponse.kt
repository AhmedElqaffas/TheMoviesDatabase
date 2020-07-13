package com.example.moviesretrofit.networking

import com.example.moviesretrofit.Movie
import com.google.gson.annotations.SerializedName

data class PopularMoviesResponse(val page: Int, val results: List<Movie>,
                                 @SerializedName("total_pages") val totalPages: Int)