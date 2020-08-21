package com.example.tmdb.main.dataRepositories

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.tmdb.dataClasses.*
import com.example.tmdb.networking.MultiMediaAPI
import com.example.tmdb.database.AppDatabase
import com.example.tmdb.networking.RetrofitClient
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

    private lateinit var userId: String


    fun createDatabase(context: Context) {
        database = AppDatabase.getDatabase(context)
    }

    fun makeRatedSeriesRequest(firstRequest: Boolean, userId: String): LiveData<List<MultiMedia>> {
        if(firstRequest) {
            sendCachedOrNetworkData(userId)
        }

        else if(currentPage < ratedSeriesTotalPages){
            returnNetworkData(currentPage + 1)
        }

        return ratedSeriesResponseLiveData
    }

    private fun sendCachedOrNetworkData(userId: String) {
        if(ratedSeries.isNotEmpty() && this.userId == userId){
            returnCachedData()
        }

        else{
            this.userId = userId
            returnNetworkData(1)
        }

    }

    private fun returnNetworkData(page: Int){
        multiMediaAPI.getHighRatedSeries(key, page)
            .apply {enqueueCallback(this) }
    }

    private fun returnCachedData(){
        ratedSeriesResponseLiveData.value = ratedSeries
    }

    private fun enqueueCallback(call: Call<SeriesResponse>) {
        call.enqueue(object: Callback<SeriesResponse> {

            override fun onResponse(call: Call<SeriesResponse>,
                                    response: Response<SeriesResponse>
            ) {
                response.body()?.let {
                    ratedSeriesResponseLiveData.postValue(it.results)
                    updateRepository(it)
                    updateDatabase(it.results)
                }
            }

            override fun onFailure(call: Call<SeriesResponse>, t: Throwable) {
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