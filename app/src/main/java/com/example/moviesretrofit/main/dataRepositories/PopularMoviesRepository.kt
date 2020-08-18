package com.example.moviesretrofit.main.dataRepositories

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.moviesretrofit.dataClasses.Movie
import com.example.moviesretrofit.dataClasses.MultiMedia
import com.example.moviesretrofit.networking.MultiMediaAPI
import com.example.moviesretrofit.dataClasses.MultiMediaResponse
import com.example.moviesretrofit.dataClasses.MovieResponse
import com.example.moviesretrofit.database.AppDatabase
import com.example.moviesretrofit.networking.RetrofitClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

object PopularMoviesRepository{

    private const val key = "097aa1909532e2d795f4f414cf4bc13f"

    private var multiMediaAPI: MultiMediaAPI = RetrofitClient.getRetrofitClient().create(MultiMediaAPI::class.java)
    private lateinit var database: AppDatabase

    private val popularMovies = mutableListOf<MultiMedia>()
    private var currentPage = 1
    private var popularMoviesTotalPages = 0

    private var popularMoviesResponseLiveData: MutableLiveData<List<MultiMedia>> = MutableLiveData()

    // mutex to synchronize the deletion of cached data and insertion of new data
    // (deletion job must finish first)
    private val mutex = Mutex()

    fun createDatabase(context: Context) {
        database = AppDatabase.getDatabase(context)
    }

    fun makePopularMoviesRequest(firstRequest: Boolean): LiveData<List<MultiMedia>> {
        if(firstRequest) {
            sendCachedOrNetworkData()
        }

        else if(currentPage < popularMoviesTotalPages){
            returnNetworkData(currentPage + 1)
        }

        return popularMoviesResponseLiveData
    }

    private fun sendCachedOrNetworkData(){
        if(popularMovies.isNotEmpty())
            returnCachedData()

        else if (popularMovies.isEmpty()) {
            returnNetworkData(1)
        }
    }

    private fun returnNetworkData(page: Int){
        multiMediaAPI.getPopularMovies(key, page)
            .apply { enqueueCallback(this) }
    }

    private fun returnCachedData(){
        popularMoviesResponseLiveData.value = popularMovies
    }

    private fun enqueueCallback(call: Call<MovieResponse>) {
        call.enqueue(object: Callback<MovieResponse> {

            override fun onResponse(call: Call<MovieResponse>,
                                    response: Response<MovieResponse>
            ) {
                response.body()?.let {
                    if(userConnectedToTheInternet(it.page)){
                        deletePopularMoviesCachedData()
                    }
                    popularMoviesResponseLiveData.postValue(it.results)
                    updateRepository(it)
                    updateDatabase(it.results)
                }
            }

            override fun onFailure(call: Call<MovieResponse>, t: Throwable) {
                Log.e("Movies error", "Couldn't get movies list from api")
                if(popularMovies.isEmpty())
                    getPopularMoviesFromDatabase()
            }
        })
    }

    private fun getPopularMoviesFromDatabase(){
        var databaseData: List<Movie> = listOf()
        CoroutineScope(IO).launch {
            databaseData = database.getMultimediaDao().getPopularMovies()
        }.invokeOnCompletion {
            if(databaseData.isNotEmpty()){
                popularMoviesResponseLiveData.postValue(databaseData)
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
    private fun deletePopularMoviesCachedData(){
        deleteRepositoryData()
        deleteDatabaseData()
    }

    private fun deleteRepositoryData(){
        popularMovies.clear()
    }

    private fun deleteDatabaseData(){
        CoroutineScope(IO).launch {
            mutex.withLock {
                database.getMultimediaDao().deleteCachedMovies()
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
        popularMovies.addAll(results)
    }

    private fun saveTotalNumberOfPages(totalPages: Int){
        popularMoviesTotalPages = totalPages
    }

    private fun updateDatabase(movies: List<Movie>){
        CoroutineScope(IO).launch {
            mutex.withLock {
                database.getMultimediaDao().insertMovies(movies)
            }
        }
    }
}