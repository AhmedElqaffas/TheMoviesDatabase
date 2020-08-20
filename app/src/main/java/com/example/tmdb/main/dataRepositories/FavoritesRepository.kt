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

    private lateinit var userId: String

    fun createDatabase(context: Context){
        database = AppDatabase.getDatabase(context)
    }

    fun getFavoriteMovies(userId: String): LiveData<List<MultiMedia>> {

        if(!areDataCached()){
            this.userId = userId
            return getDatabaseData(userId)
        }

        return favoriteMoviesLiveData
    }

    private fun areDataCached(): Boolean {
        return favoriteMoviesLiveData.value != null && this.userId == userId
    }

    private fun getDatabaseData(userId: String): LiveData<List<MultiMedia>>{
        runBlocking {
            launch(IO){
                favoriteMoviesLiveData = database.getMultimediaDao().getFavoriteMovies(userId)
            }
        }
        return favoriteMoviesLiveData
    }

    fun getFavoriteSeries(userId: String): LiveData<List<MultiMedia>> {
        return if(favoriteSeriesLiveData.value != null && this.userId == userId){
            favoriteSeriesLiveData
        } else{
            this.userId = userId
            runBlocking {
                launch(IO){
                    favoriteSeriesLiveData = database.getMultimediaDao().getFavoriteSeries(userId)
                }
            }
            favoriteSeriesLiveData
        }
    }

}