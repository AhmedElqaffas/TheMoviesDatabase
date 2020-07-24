package com.example.moviesretrofit.dataClasses

import com.google.gson.annotations.SerializedName

open class MultiMediaResponse(@Transient open val page: Int,
                              @Transient open val results: List<MultiMedia>,
                              @Transient open val totalPages: Int){

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