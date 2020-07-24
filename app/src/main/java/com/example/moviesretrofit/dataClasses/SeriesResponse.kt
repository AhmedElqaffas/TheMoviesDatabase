package com.example.moviesretrofit.dataClasses

class PopularSeriesResponse(val type: Int = MultiMedia.Constants.POPULAR)
    : MultiMediaResponse(0, listOf(), 0)

class RatedSeriesResponse(val type: Int = MultiMedia.Constants.RATED)
    : MultiMediaResponse(0, listOf(), 0)