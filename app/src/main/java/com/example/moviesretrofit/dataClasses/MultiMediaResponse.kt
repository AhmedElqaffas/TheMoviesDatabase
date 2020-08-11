package com.example.moviesretrofit.dataClasses

open class MultiMediaResponse(@Transient open val page: Int,
                              @Transient open val results: List<MultiMedia>,
                              @Transient open val totalPages: Int){


    fun filterPeopleEntriesFromResponse(): List<MultiMedia>{
        val entriesList = mutableListOf<MultiMedia>()
        for(entry in results){
            if(entry.mediaType != "person"){
                entriesList.add(entry)
            }
        }
        return entriesList
    }
}