package com.example.moviesretrofit.dataRepositories

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.moviesretrofit.dataClasses.MultiMedia
import com.example.moviesretrofit.networking.MultiMediaAPI
import com.example.moviesretrofit.dataClasses.MultiMediaResponse
import com.example.moviesretrofit.dataClasses.RatedSeriesResponse
import com.example.moviesretrofit.dataClasses.Series
import com.example.moviesretrofit.database.AppDatabase
import com.example.moviesretrofit.networking.RetrofitClient
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

    private val ratedSeriesResponseLiveData: MutableLiveData<MultiMediaResponse> = MutableLiveData()

    fun createDatabase(context: Context) {
        database = AppDatabase.getDatabase(context)
    }

    fun makeRatedSeriesRequest(page: Int): LiveData<MultiMediaResponse>{
        if(page == 1) {
            sendCachedOrNetworkData()
        }

        else{
            returnNetworkData(page)
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
        ratedSeriesResponseLiveData.value =
            MultiMediaResponse(currentPage, ratedSeries, ratedSeriesTotalPages)
    }

    private fun enqueueCallback(call: Call<RatedSeriesResponse>) {
        call.enqueue(object: Callback<RatedSeriesResponse> {

            override fun onResponse(call: Call<RatedSeriesResponse>,
                                    response: Response<RatedSeriesResponse>
            ) {
                response.body()?.let {
                    ratedSeriesResponseLiveData.postValue(
                        MultiMediaResponse(it.page, it.results, it.totalPages)
                    )
                    updateRepository(it)
                    updateDatabase(it.results)
                }
            }

            override fun onFailure(call: Call<RatedSeriesResponse>, t: Throwable) {
                Log.e("Series error", "Couldn't get series list from API")
                if(getRatedSeriesFromDatabase().isNotEmpty() && ratedSeries.isEmpty())
                    returnDatabaseData()
            }
        })
    }

    private fun getRatedSeriesFromDatabase(): List<Series> {
        return database.getMultimediaDao().getTopRatedSeries()
    }

    private fun returnDatabaseData(){
        val databaseData = getRatedSeriesFromDatabase()
        ratedSeriesResponseLiveData.postValue(
            MultiMediaResponse(
                currentPage, databaseData, ratedSeriesTotalPages)
        )

        appendResultItemsToList(databaseData)
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
        database.getMultimediaDao().insertSeries(series)
    }

}