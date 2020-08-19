package com.example.tmdb.dataClasses

import com.google.gson.annotations.SerializedName

class HybridResponse(override val page: Int,
                     override val results: List<MultiMedia>,
                     @SerializedName("total_pages") override val totalPages: Int)
    : MultiMediaResponse(page, results, totalPages){

}

