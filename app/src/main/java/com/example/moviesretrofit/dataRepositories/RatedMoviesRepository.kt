package com.example.moviesretrofit.dataRepositories

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.moviesretrofit.dataClasses.Movie
import com.example.moviesretrofit.dataClasses.MultiMedia
import com.example.moviesretrofit.networking.MultiMediaAPI
import com.example.moviesretrofit.dataClasses.MultiMediaResponse
import com.example.moviesretrofit.dataClasses.RatedMovieResponse
import com.example.moviesretrofit.database.AppDatabase
import com.example.moviesretrofit.networking.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

object RatedMoviesRepository{

    private const val key = "097aa1909532e2d795f4f414cf4bc13f"

    private var multiMediaAPI: MultiMediaAPI = RetrofitClient.getRetrofitClient().create(MultiMediaAPI::class.java)
    private lateinit var database: AppDatabase

    private var ratedMovies = mutableListOf<MultiMedia>()
    private var currentPage = 1
    private var ratedMoviesTotalPages = 0

    private val ratedMoviesResponseLiveData: MutableLiveData<MultiMediaResponse> = MutableLiveData()

    fun makeRatedMoviesRequest(page: Int): LiveData<MultiMediaResponse>{
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

    fun createDatabase(context: Context) {
        database = AppDatabase.getDatabase(context)
    }

    private fun returnNetworkData(page: Int){
        multiMediaAPI.getHighRatedMovies(key, page)
            .apply { enqueueCallback(this) }
    }

    private fun returnCachedData(){
        ratedMoviesResponseLiveData.value =
            MultiMediaResponse(currentPage, ratedMovies, ratedMoviesTotalPages)
    }

    private fun enqueueCallback(call: Call<RatedMovieResponse>) {
        call.enqueue(object: Callback<RatedMovieResponse> {

            override fun onResponse(call: Call<RatedMovieResponse>,
                                    response: Response<RatedMovieResponse>
            ) {
                response.body()?.let {
                    ratedMoviesResponseLiveData.postValue(
                        MultiMediaResponse(it.page, it.results, it.totalPages)
                    )
                    updateRepository(it)
                    updateDatabase(it.results)
                }
            }

            override fun onFailure(call: Call<RatedMovieResponse>, t: Throwable) {
                Log.e("Movies error", "Couldn't get movies list from API")
                if(getRatedMoviesFromDatabase().isNotEmpty() && ratedMovies.isEmpty())
                    returnDatabaseData()
            }
        })
    }

    private fun getRatedMoviesFromDatabase(): List<Movie> {
        return database.getMultimediaDao().getTopRatedMovies()
    }

    private fun returnDatabaseData(){
        val databaseData = getRatedMoviesFromDatabase()
        ratedMoviesResponseLiveData.postValue(MultiMediaResponse(currentPage,
            databaseData, ratedMoviesTotalPages))

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
        ratedMovies.addAll(results)
    }

    private fun saveTotalNumberOfPages(totalPages: Int){
        ratedMoviesTotalPages = totalPages
    }

    private fun updateDatabase(movies: List<Movie>){
       database.getMultimediaDao().insertMovies(movies)
    }
}