package com.example.tmdb.dataClasses

import com.google.gson.annotations.SerializedName


class MovieResponse(override val page: Int,
                    override val results: List<Movie>,
                    @SerializedName("total_pages") override val totalPages: Int):
    MultiMediaResponse(page, results, totalPages){
}
