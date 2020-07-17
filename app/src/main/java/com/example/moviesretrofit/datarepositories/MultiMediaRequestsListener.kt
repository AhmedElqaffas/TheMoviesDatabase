package com.example.moviesretrofit.datarepositories

import com.example.moviesretrofit.MultiMedia

interface MultiMediaRequestsListener{
    fun responseLoaded(mediaList: List<MultiMedia>, totalPages: Int)
    fun updateCurrentPage(currentPage: Int)
}