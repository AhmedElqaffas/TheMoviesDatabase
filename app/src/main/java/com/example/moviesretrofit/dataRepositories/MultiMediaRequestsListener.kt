package com.example.moviesretrofit.dataRepositories

import com.example.moviesretrofit.models.MultiMedia

interface MultiMediaRequestsListener{
    fun responseLoaded(mediaList: List<MultiMedia>, totalPages: Int)
    fun updateCurrentPage(currentPage: Int)
}