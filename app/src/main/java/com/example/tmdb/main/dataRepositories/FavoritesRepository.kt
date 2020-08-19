package com.example.tmdb.main.dataRepositories

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import com.example.tmdb.dataClasses.MultiMedia
import com.example.tmdb.database.AppDatabase
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.IO

object FavoritesRepository {

    private lateinit var database: AppDatabase
    private var favoriteMoviesLiveData: LiveData<List<MultiMedia>> = liveData{}
    private var favoriteSeriesLiveData: LiveData<List<MultiMedia>> = liveData{}


    fun createDatabase(context: Context){
        database = AppDatabase.getDatabase(context)
    }

    fun getFavoriteMovies(): LiveData<List<MultiMedia>> {
        return if(favoriteMoviesLiveData.value != null){
            favoriteMoviesLiveData
        } else{
            runBlocking {
                launch(IO){
                    favoriteMoviesLiveData = database.getMultimediaDao().getFavoriteMovies()
                }
            }
            favoriteMoviesLiveData
        }
    }

    fun getFavoriteSeries(): LiveData<List<MultiMedia>> {
        return if(favoriteSeriesLiveData.value != null){
            favoriteSeriesLiveData
        } else{
            runBlocking {
                launch(IO){
                    favoriteSeriesLiveData = database.getMultimediaDao().getFavoriteSeries()
                }
            }
            favoriteSeriesLiveData
        }
    }
}