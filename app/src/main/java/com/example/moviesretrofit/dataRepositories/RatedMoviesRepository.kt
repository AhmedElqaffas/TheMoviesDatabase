package com.example.moviesretrofit.dataRepositories

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.moviesretrofit.dataClasses.MultiMedia
import com.example.moviesretrofit.dataClasses.MultiMediaRepositoryResponse
import com.example.moviesretrofit.networking.MultiMediaAPI
import com.example.moviesretrofit.dataClasses.MultiMediaResponse
import com.example.moviesretrofit.networking.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

object RatedMoviesRepository{

    private const val key = "097aa1909532e2d795f4f414cf4bc13f"

    private var multiMediaAPI: MultiMediaAPI = RetrofitClient.getRetrofitClient().create(MultiMediaAPI::class.java)

    private var ratedMovies = mutableListOf<MultiMedia>()
    private var currentPage = 1
    private var ratedMoviesTotalPages = 0

    private val ratedMoviesResponseLiveData: MutableLiveData<MultiMediaRepositoryResponse> = MutableLiveData()

    fun makeRatedMoviesRequest(page: Int): LiveData<MultiMediaRepositoryResponse>{
        if(page == 1) {
            sendCachedOrNetworkData()
        }

        else{
            returnNetworkData(page)
        }

        return ratedMoviesResponseLiveData
    }

    private fun sendCachedOrNetworkData(){
        if (ratedMovies.isEmpty())
            returnNetworkData(1)
        else
            returnCachedData()
    }

    private fun returnNetworkData(page: Int){
        multiMediaAPI.getHighRatedMovies(key, page)
            .apply { enqueueCallback(this) }
    }

    private fun returnCachedData(){
        ratedMoviesResponseLiveData.postValue(
            MultiMediaRepositoryResponse(ratedMovies, currentPage, ratedMoviesTotalPages)
        )
    }

    private fun enqueueCallback(call: Call<MultiMediaResponse>) {
        call.enqueue(object: Callback<MultiMediaResponse> {

            override fun onResponse(call: Call<MultiMediaResponse>,
                                    response: Response<MultiMediaResponse>
            ) {
                response.body()?.let {
                    ratedMoviesResponseLiveData.postValue(
                        MultiMediaRepositoryResponse(it.results, it.page, it.totalPages)
                    )
                    updateRepository(it)
                }
            }

            override fun onFailure(call: Call<MultiMediaResponse>, t: Throwable) {
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
        ratedMovies.addAll(results)
    }

    private fun saveTotalNumberOfPages(totalPages: Int){
        ratedMoviesTotalPages = totalPages
    }
}