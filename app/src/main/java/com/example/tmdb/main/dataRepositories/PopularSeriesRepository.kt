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
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
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

    // mutex to synchronize the deletion of cached data and insertion of new data
    // (deletion job must finish first)
    private val mutex = Mutex()

    private lateinit var userId: String

    fun createDatabase(context: Context) {
        database = AppDatabase.getDatabase(context)
    }

    fun makePopularSeriesRequest(firstRequest: Boolean, userId: String): LiveData<List<MultiMedia>> {
        this.userId = userId
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

    private fun enqueueCallback(call: Call<SeriesResponse>) {
        call.enqueue(object: Callback<SeriesResponse> {

            override fun onResponse(call: Call<SeriesResponse>,
                                    response: Response<SeriesResponse>
            ) {
                response.body()?.let {
                    if(userConnectedToTheInternet(it.page)){
                        deletePopularSeriesCachedData()
                    }
                    popularSeriesResponseLiveData.postValue(it.results)
                    updateRepository(it)
                    updateDatabase(it.results)
                }
            }

            override fun onFailure(call: Call<SeriesResponse>, t: Throwable) {
                Log.e("Series error", "Couldn't get series list")
               if(popularSeries.isEmpty())
                    getPopularSeriesFromDatabase()
            }
        })
    }

    private fun getPopularSeriesFromDatabase(){
        var databaseData: List<Series> = listOf()
        CoroutineScope(IO).launch {
            databaseData = database.getMultimediaDao().getPopularSeries()
        }.invokeOnCompletion {
            if(databaseData.isNotEmpty()){
                popularSeriesResponseLiveData.postValue(databaseData)
                appendResultItemsToList(databaseData)
            }
        }
    }

    /**
     * If the received response is page one, that means that the user currently has
     * internet connection, then we can know delete the cache
     * Only the response received from page one is used, because we don't want
     * each page to delete the content of the previous pages
     */
    private fun userConnectedToTheInternet(page: Int): Boolean{
        return page == 1
    }
    private fun deletePopularSeriesCachedData(){
        deleteRepositoryData()
        deleteDatabaseData()
    }

    private fun deleteRepositoryData(){
        popularSeries.clear()
    }

    private fun deleteDatabaseData(){
        CoroutineScope(IO).launch {
            mutex.withLock {
                database.getMultimediaDao().deleteCachedSeries(userId)
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
        popularSeries.addAll(results)
    }

    private fun saveTotalNumberOfPages(totalPages: Int){
        popularSeriesTotalPages = totalPages
    }

    private fun updateDatabase(series: List<Series>){
        CoroutineScope(IO).launch {
            mutex.withLock{
                database.getMultimediaDao().insertSeries(series)
            }
        }

    }
}