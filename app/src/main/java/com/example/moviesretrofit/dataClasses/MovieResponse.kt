package com.example.moviesretrofit.dataClasses

import com.google.gson.annotations.SerializedName


class PopularMovieResponse(override val page: Int,
                           override val results: List<Movie>,
                           @SerializedName("total_pages") override val totalPages: Int):
    MultiMediaResponse(page, results, totalPages){
}

class RatedMovieResponse(override val page: Int,
                         override val results: List<Movie>,
                         @SerializedName("total_pages") override val totalPages: Int):
    MultiMediaResponse(page, results, totalPages)