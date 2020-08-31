package com.example.tmdb.dataClasses

open class MultiMediaResponse(@Transient open val page: Int,
                              @Transient open val results: List<MultiMedia>,
                              @Transient open val totalPages: Int){


    fun filterPeopleEntriesFromResponse(): List<MultiMedia>{
        return results.filter { it.mediaType == "movie" || it.mediaType == "tv" }
    }
}