package com.example.moviesretrofit.dataClasses

import com.google.gson.annotations.SerializedName

class PopularSeriesResponse(override val page: Int,
                            override val results: List<Series>,
                            @SerializedName("total_pages") override val totalPages: Int)
    : MultiMediaResponse(page, results, totalPages)

class RatedSeriesResponse(override val page: Int,
                          override val results: List<Series>,
                          @SerializedName("total_pages") override val totalPages: Int)
    : MultiMediaResponse(page, results, totalPages)