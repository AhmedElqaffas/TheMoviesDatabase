package com.example.moviesretrofit.dataClasses

import com.google.gson.annotations.SerializedName

data class MultiMediaResponse(val page: Int, val results: List<MultiMedia>,
                              @SerializedName("total_pages") val totalPages: Int)