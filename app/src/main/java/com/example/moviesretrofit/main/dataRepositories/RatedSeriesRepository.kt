package com.example.moviesretrofit.main.dataRepositories

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.moviesretrofit.dataClasses.*
import com.example.moviesretrofit.networking.MultiMediaAPI
import com.example.moviesretrofit.database.AppDatabase
import com.example.moviesretrofit.networking.RetrofitClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

object RatedSeriesRepository{

    private const val key = "097aa1909532e2d795f4f414cf4bc13f"

    private var multiMediaAPI: MultiMediaAPI = RetrofitClient.getRetrofitClient().create(MultiMediaAPI::class.java)
    private lateinit var database: AppDatabase

    private var ratedSeries = mutableListOf<MultiMedia>()
    private var currentPage = 1
    private var ratedSeriesTotalPages = 0

    private val ratedSeriesResponseLiveData: MutableLiveData<List<MultiMedia>> = MutableLiveData()

    fun createDatabase(context: Context) {
        database = AppDatabase.getDatabase(context)
    }

    fun makeRatedSeriesRequest(firstRequest: Boolean): LiveData<List<MultiMedia>> {
        if(firstRequest) {
            sendCachedOrNetworkData()
        }

        else if(currentPage < ratedSeriesTotalPages){
            returnNetworkData(currentPage + 1)
        }

        return ratedSeriesResponseLiveData
    }

    private fun sendCachedOrNetworkData(){
        if (ratedSeries.isEmpty())
            returnNetworkData(1)
        else
            returnCachedData()
    }

    private fun returnNetworkData(page: Int){
        multiMediaAPI.getHighRatedSeries(key, page)
            .apply {enqueueCallback(this) }
    }

    private fun returnCachedData(){
        ratedSeriesResponseLiveData.value = ratedSeries
    }

    private fun enqueueCallback(call: Call<RatedSeriesResponse>) {
        call.enqueue(object: Callback<RatedSeriesResponse> {

            override fun onResponse(call: Call<RatedSeriesResponse>,
                                    response: Response<RatedSeriesResponse>
            ) {
                response.body()?.let {
                    ratedSeriesResponseLiveData.postValue(it.results)
                    updateRepository(it)
                    updateDatabase(it.results)
                }
            }

            override fun onFailure(call: Call<RatedSeriesResponse>, t: Throwable) {
                Log.e("Series error", "Couldn't get series list from API")
                if(ratedSeries.isEmpty())
                        getRatedSeriesFromDatabase()
            }
        })
    }

    private fun getRatedSeriesFromDatabase(){
        var databaseData: List<Series> = listOf()
        CoroutineScope(IO).launch {
            databaseData = database.getMultimediaDao().getTopRatedSeries()
        }.invokeOnCompletion {
            if(databaseData.isNotEmpty()){
                ratedSeriesResponseLiveData.postValue(databaseData)
                appendResultItemsToList(databaseData)
            }
        }
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
        ratedSeries.addAll(results)
    }

    private fun saveTotalNumberOfPages(totalPages: Int){
        ratedSeriesTotalPages = totalPages
    }

    private fun updateDatabase(series: List<Series>){
        CoroutineScope(IO).launch {
            database.getMultimediaDao().insertSeries(series)
        }

    }

}