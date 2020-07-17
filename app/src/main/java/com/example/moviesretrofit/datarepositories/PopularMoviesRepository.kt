package com.example.moviesretrofit.datarepositories

import android.util.Log
import com.example.moviesretrofit.MultiMedia
import com.example.moviesretrofit.networking.MultiMediaAPI
import com.example.moviesretrofit.networking.MultiMediaResponse
import com.example.moviesretrofit.networking.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

object PopularMoviesRepository{

    private const val key = "097aa1909532e2d795f4f414cf4bc13f"

    private var multiMediaAPI: MultiMediaAPI = RetrofitClient.getRetrofitClient().create(MultiMediaAPI::class.java)
    lateinit var popularMoviesRequestsListener: MultiMediaRequestsListener

    private val popularMovies = mutableListOf<MultiMedia>()
    private var currentPage = 1
    private var popularMoviesTotalPages = 0

    fun makePopularMoviesRequest(page: Int){
        if(page == 1) {
            sendCachedOrNetworkData()
            popularMoviesRequestsListener.updateCurrentPage(currentPage)
        }

        else{
            updateCurrentPage(page)
            sendNetworkData(page)
        }
    }

    private fun sendCachedOrNetworkData(){
        if (popularMovies.isEmpty())
            sendNetworkData(1)
        else
            sendCachedData()
    }

    private fun sendNetworkData(page: Int){
        multiMediaAPI.getPopularMovies(key, page)
            .apply { enqueueCallback(this) }
    }

    private fun sendCachedData(){
        popularMoviesRequestsListener.responseLoaded(popularMovies, popularMoviesTotalPages)
    }

    private fun updateCurrentPage(page: Int){
        currentPage = page
    }


    private fun enqueueCallback(call: Call<MultiMediaResponse>) {
        call.enqueue(object: Callback<MultiMediaResponse> {

            override fun onResponse(call: Call<MultiMediaResponse>,
                                    response: Response<MultiMediaResponse>
            ) {
                response.body()?.let {
                    popularMoviesRequestsListener.responseLoaded(it.results, it.totalPages)
                    updateRepository(it)
                }
            }

            override fun onFailure(call: Call<MultiMediaResponse>, t: Throwable) {
                Log.e("Movies error", "Couldn't get movies list")
            }
        })
    }

    private fun updateRepository(response: MultiMediaResponse){
        appendResultItemsToList(response.results)
        saveTotalNumberOfPages(response.totalPages)
    }

    private fun appendResultItemsToList(results: List<MultiMedia>) {
        popularMovies.addAll(results)
    }

    private fun saveTotalNumberOfPages(totalPages: Int){
        popularMoviesTotalPages = totalPages
    }

}