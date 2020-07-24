package com.example.moviesretrofit.dataRepositories

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.moviesretrofit.dataClasses.MultiMedia
import com.example.moviesretrofit.networking.MultiMediaAPI
import com.example.moviesretrofit.dataClasses.MultiMediaResponse
import com.example.moviesretrofit.dataClasses.SeriesResponse
import com.example.moviesretrofit.networking.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

object PopularSeriesRepository{

    private const val key = "097aa1909532e2d795f4f414cf4bc13f"

    private var multiMediaAPI: MultiMediaAPI = RetrofitClient.getRetrofitClient().create(MultiMediaAPI::class.java)

    private var popularSeries = mutableListOf<MultiMedia>()
    private var currentPage = 1
    private var popularSeriesTotalPages = 0

    private val popularSeriesResponseLiveData: MutableLiveData<MultiMediaResponse> = MutableLiveData()

    fun makePopularSeriesRequest(page: Int): LiveData<MultiMediaResponse> {
        if(page == 1) {
            sendCachedOrNetworkData()
        }

        else{
            returnNetworkData(page)
        }

        return popularSeriesResponseLiveData
    }

    private fun sendCachedOrNetworkData(){
        if (popularSeries.isEmpty())
            returnNetworkData(1)
        else
            returnCachedData()
    }

    private fun returnNetworkData(page: Int){
        multiMediaAPI.getPopularSeries(key, page)
            .apply {enqueueCallback(this) }
    }

    private fun returnCachedData() {
        popularSeriesResponseLiveData.value =
            MultiMediaResponse(currentPage, popularSeries, popularSeriesTotalPages)
    }

    private fun enqueueCallback(call: Call<SeriesResponse>) {
        call.enqueue(object: Callback<SeriesResponse> {

            override fun onResponse(call: Call<SeriesResponse>,
                                    response: Response<SeriesResponse>
            ) {
                response.body()?.let {
                    popularSeriesResponseLiveData.postValue(
                        MultiMediaResponse(it.page, it.results, it.totalPages)
                    )
                    updateRepository(it)
                }
            }

            override fun onFailure(call: Call<SeriesResponse>, t: Throwable) {
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
        popularSeries.addAll(results)
    }

    private fun saveTotalNumberOfPages(totalPages: Int){
        popularSeriesTotalPages = totalPages
    }

}