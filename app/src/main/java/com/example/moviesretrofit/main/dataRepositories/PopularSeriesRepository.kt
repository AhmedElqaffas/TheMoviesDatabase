package com.example.moviesretrofit.main.dataRepositories

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.moviesretrofit.dataClasses.*
import com.example.moviesretrofit.networking.MultiMediaAPI
import com.example.moviesretrofit.database.AppDatabase
import com.example.moviesretrofit.networking.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

object PopularSeriesRepository{

    private const val key = "097aa1909532e2d795f4f414cf4bc13f"

    private var multiMediaAPI: MultiMediaAPI = RetrofitClient.getRetrofitClient().create(MultiMediaAPI::class.java)
    private lateinit var database: AppDatabase

    private var popularSeries = mutableListOf<MultiMedia>()
    private var currentPage = 1
    private var popularSeriesTotalPages = 0

    private val popularSeriesResponseLiveData: MutableLiveData<List<MultiMedia>> = MutableLiveData()


    fun createDatabase(context: Context) {
        database = AppDatabase.getDatabase(context)
    }

    fun makePopularSeriesRequest(firstRequest: Boolean): LiveData<List<MultiMedia>> {
        if(firstRequest) {
            sendCachedOrNetworkData()
        }

        else if(currentPage < popularSeriesTotalPages){
            returnNetworkData(currentPage + 1)
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
        popularSeriesResponseLiveData.value = popularSeries
    }

    private fun enqueueCallback(call: Call<PopularSeriesResponse>) {
        call.enqueue(object: Callback<PopularSeriesResponse> {

            override fun onResponse(call: Call<PopularSeriesResponse>,
                                    response: Response<PopularSeriesResponse>
            ) {
                response.body()?.let {
                    popularSeriesResponseLiveData.postValue(it.results)
                    updateRepository(it)
                    updateDatabase(it.results)
                }
            }

            override fun onFailure(call: Call<PopularSeriesResponse>, t: Throwable) {
                Log.e("Series error", "Couldn't get series list")
                if(getPopularSeriesFromDatabase().isNotEmpty() && popularSeries.isEmpty())
                    returnDatabaseData()
            }
        })
    }

    private fun getPopularSeriesFromDatabase(): List<Series> {
        return database.getMultimediaDao().getPopularSeries()
    }

    private fun returnDatabaseData(){
        val databaseData = getPopularSeriesFromDatabase()
        popularSeriesResponseLiveData.postValue(popularSeries)

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
        popularSeries.addAll(results)
    }

    private fun saveTotalNumberOfPages(totalPages: Int){
        popularSeriesTotalPages = totalPages
    }

    private fun updateDatabase(series: List<Series>){
        database.getMultimediaDao().insertSeries(series)
    }
}