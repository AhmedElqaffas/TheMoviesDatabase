package com.example.moviesretrofit.dataClasses

import com.google.gson.annotations.SerializedName

data class MultiMediaResponse(val page: Int, val results: List<MultiMedia>,
                              @SerializedName("total_pages") val totalPages: Int){

    fun filterPeopleEntriesFromResponse(): MultiMediaResponse{
        val entriesList = mutableListOf<MultiMedia>()
        for(entry in results){
            if(entry.mediaType != "person"){
                entriesList.add(entry)
            }
        }
        return MultiMediaResponse(page, entriesList, totalPages)
    }
}