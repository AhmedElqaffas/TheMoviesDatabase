package com.example.moviesretrofit.dataClasses

import com.google.gson.annotations.SerializedName

class HybridResponse(val type: Int = MultiMedia.Constants.POPULAR,
                     override val page: Int,
                     override val results: List<MultiMedia>,
                     @SerializedName("total_pages") override val totalPages: Int)
    : MultiMediaResponse(page, results, totalPages)