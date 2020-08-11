package com.example.moviesretrofit.dataClasses

import com.google.gson.annotations.SerializedName


class PopularMovieResponse(val type: Int = MultiMedia.Constants.POPULAR,
                           override val page: Int,
                           override val results: List<Movie>,
                           @SerializedName("total_pages") override val totalPages: Int):
    MultiMediaResponse(page, results, totalPages){
}

class RatedMovieResponse(val type: Int = MultiMedia.Constants.RATED,
                         override val page: Int,
                         override val results: List<Movie>,
                         @SerializedName("total_pages") override val totalPages: Int):
    MultiMediaResponse(page, results, totalPages)