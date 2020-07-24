package com.example.moviesretrofit.dataClasses

class PopularMovieResponse(val type: Int = MultiMedia.Constants.POPULAR):
    MultiMediaResponse(0, listOf(), 0)

class RatedMovieResponse(val type: Int = MultiMedia.Constants.RATED):
    MultiMediaResponse(0, listOf(), 0)