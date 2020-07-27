package com.example.moviesretrofit.main.dataRepositories

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.moviesretrofit.dataClasses.Movie
import com.example.moviesretrofit.dataClasses.MultiMedia
import com.example.moviesretrofit.networking.MultiMediaAPI
import com.example.moviesretrofit.dataClasses.MultiMediaResponse
import com.example.moviesretrofit.dataClasses.PopularMovieResponse
import com.example.moviesretrofit.database.AppDatabase
import com.example.moviesretrofit.networking.RetrofitClient
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

    private val popularMoviesResponseLiveData: MutableLiveData<List<MultiMedia>> = MutableLiveData()

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

        else if (popularMovies.isEmpty())
            returnNetworkData(1)
    }

    private fun returnNetworkData(page: Int){
        multiMediaAPI.getPopularMovies(key, page)
            .apply { enqueueCallback(this) }
    }

    private fun returnCachedData(){
        popularMoviesResponseLiveData.value = popularMovies
    }

    private fun enqueueCallback(call: Call<PopularMovieResponse>) {
        call.enqueue(object: Callback<PopularMovieResponse> {

            override fun onResponse(call: Call<PopularMovieResponse>,
                                    response: Response<PopularMovieResponse>
            ) {
                response.body()?.let {
                    popularMoviesResponseLiveData.postValue(it.results)
                    updateRepository(it)
                    updateDatabase(it.results)
                }
            }

            override fun onFailure(call: Call<PopularMovieResponse>, t: Throwable) {
                Log.e("Movies error", "Couldn't get movies list from api")
                if(getPopularMoviesFromDatabase().isNotEmpty() && popularMovies.isEmpty())
                    returnDatabaseData()
            }
        })
    }

    private fun getPopularMoviesFromDatabase(): List<Movie> {
        return database.getMultimediaDao().getPopularMovies()
    }

    private fun returnDatabaseData(){
        val databaseData = getPopularMoviesFromDatabase()
        popularMoviesResponseLiveData.postValue(databaseData)
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
        popularMovies.addAll(results)
    }

    private fun saveTotalNumberOfPages(totalPages: Int){
        popularMoviesTotalPages = totalPages
    }

    private fun updateDatabase(movies: List<Movie>){
        database.getMultimediaDao().insertMovies(movies)
    }
}