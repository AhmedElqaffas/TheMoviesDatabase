package com.example.moviesretrofit.dataRepositories

import com.example.moviesretrofit.dataClasses.MultiMedia

interface MultiMediaRequestsListener{
    fun responseLoaded(mediaList: List<MultiMedia>, totalPages: Int)
    fun updateCurrentPage(currentPage: Int)
}