package com.example.moviesretrofit.dataRepositories

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.moviesretrofit.dataClasses.MovieResponse
import com.example.moviesretrofit.dataClasses.MultiMedia
import com.example.moviesretrofit.networking.MultiMediaAPI
import com.example.moviesretrofit.dataClasses.MultiMediaResponse
import com.example.moviesretrofit.networking.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

object PopularMoviesRepository{

    private const val key = "097aa1909532e2d795f4f414cf4bc13f"

    private var multiMediaAPI: MultiMediaAPI = RetrofitClient.getRetrofitClient().create(MultiMediaAPI::class.java)

    private val popularMovies = mutableListOf<MultiMedia>()
    private var currentPage = 1
    private var popularMoviesTotalPages = 0

    private val popularMoviesResponseLiveData: MutableLiveData<MultiMediaResponse> = MutableLiveData()

    fun makePopularMoviesRequest(page: Int): LiveData<MultiMediaResponse> {
        if(page == 1) {
            sendCachedOrNetworkData()
        }

        else{
            returnNetworkData(page)
        }

        return popularMoviesResponseLiveData
    }

    private fun sendCachedOrNetworkData(){
        if (popularMovies.isEmpty())
            returnNetworkData(1)
        else
            returnCachedData()
    }

    private fun returnNetworkData(page: Int){
        multiMediaAPI.getPopularMovies(key, page)
            .apply { enqueueCallback(this) }
    }

    private fun returnCachedData(){
        popularMoviesResponseLiveData.value =
            MultiMediaResponse(currentPage, popularMovies, popularMoviesTotalPages)
    }

    private fun enqueueCallback(call: Call<MovieResponse>) {
        call.enqueue(object: Callback<MovieResponse> {

            override fun onResponse(call: Call<MovieResponse>,
                                    response: Response<MovieResponse>
            ) {
                response.body()?.let {
                    popularMoviesResponseLiveData.postValue(
                        MultiMediaResponse(it.page, it.results, it.totalPages)
                    )
                    updateRepository(it)
                }
            }

            override fun onFailure(call: Call<MovieResponse>, t: Throwable) {
                Log.e("Movies error", "Couldn't get movies list")
            }
        })
    }

    private fun updateRepository(response: MultiMediaResponse){
        updateCurrentPage(response.page)
        appendResultItemsToList(response.results)
        saveTotalNumberOfPages(response.totalPages)
    }

    private fun updateCurrentPage(page: Int){
        currentPage = page
    }

    private fun appendResultItemsToList(results: List<MultiMedia>) {
        popularMovies.addAll(results)
    }

    private fun saveTotalNumberOfPages(totalPages: Int){
        popularMoviesTotalPages = totalPages
    }
}