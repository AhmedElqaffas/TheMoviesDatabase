package com.example.tmdb.main.dataRepositories

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import com.example.tmdb.dataClasses.Movie
import com.example.tmdb.dataClasses.MultiMedia
import com.example.tmdb.dataClasses.Series
import com.example.tmdb.database.AppDatabase
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.IO

object FavoritesRepository {

    const val MOVIE = 1
    const val SERIES = 2

    private lateinit var database: AppDatabase
    private var favoriteMoviesLiveData: LiveData<List<MultiMedia>> = liveData{}
    private var favoriteSeriesLiveData: LiveData<List<MultiMedia>> = liveData{}

    private var firestore = FirebaseFirestore.getInstance()
    private lateinit var userId: String


    fun createDatabase(context: Context){
        database = AppDatabase.getDatabase(context)
    }

    fun getFavoriteShows(multimediaType: Int, userId: String): LiveData<List<MultiMedia>> {

        if(!areFavoritesCached(userId, multimediaType)){
            this.userId = userId
            return getDatabaseData(multimediaType, userId)
        }

        return if(multimediaType == MOVIE){
            favoriteMoviesLiveData
        } else{
            favoriteSeriesLiveData
        }

    }

    private fun areFavoritesCached(userId: String, multimediaType: Int): Boolean {
        return isFavoritesListNotEmpty(multimediaType) && isUserTheSame(userId)
    }

    private fun isFavoritesListNotEmpty(multimediaType: Int): Boolean{
        return if(multimediaType == MOVIE){
            favoriteMoviesLiveData.value != null
        }else{
            favoriteSeriesLiveData.value != null
        }
    }

    private fun isUserTheSame(userId: String): Boolean{
        return this.userId == userId
    }

    private fun getDatabaseData(multimediaType: Int, userId: String): LiveData<List<MultiMedia>>{
        return if(isUserConnectedToFirebase(userId)){
            getFavoritesFromFirestore(multimediaType, userId)
        } else{
            getFavoritesFromLocalDatabase(multimediaType)
        }
    }

    private fun isUserConnectedToFirebase(userId: String): Boolean{
        return userId != "local"
    }

    /**
     * This method fetches the data from firestore and inserts it in the database, when the database
     * changes, it will notify the observer since the room query returns live data
     */
    private fun getFavoritesFromFirestore(multimediaType: Int, userId: String): LiveData<List<MultiMedia>>{
        val linkerCollectionName = if(multimediaType == MOVIE){
            "movies_linker"
        } else{
            "series_linker"
        }

        CoroutineScope(IO).launch {
            firestore.collection(linkerCollectionName).document(userId).get()
                .addOnSuccessListener {
                    handleSuccessfulFirebaseCommunication(multimediaType, it)
                }.addOnFailureListener {
                    Log.i("FavoritesRepository",
                        "couldn't load linker document ${it.message}")
                }
        }

        return getFavoritesFromLocalDatabase(multimediaType)
    }

    private fun handleSuccessfulFirebaseCommunication(multimediaType: Int, documentSnapshot: DocumentSnapshot){
        if(multimediaType == MOVIE) {
            documentSnapshot.data?.forEach { linkerEntry ->
                getMovieFromMoviesCollection(linkerEntry.key)
            }
        }else{
            documentSnapshot.data?.forEach { linkerEntry ->
                getSeriesFromSeriesCollection(linkerEntry.key)
            }
        }
    }

    private fun getMovieFromMoviesCollection(movieDocumentId: String){
        firestore.collection("movies").document(movieDocumentId).get()
            .addOnSuccessListener {
                insertFirebaseMovieToDatabase(it)
            }.addOnFailureListener {
                Log.i("FavoritesRepository", "couldn't load movie document ${it.message}")
            }
    }

    private fun getSeriesFromSeriesCollection(seriesDocumentId: String){
        firestore.collection("series").document(seriesDocumentId).get()
            .addOnSuccessListener {
                insertFirebaseSeriesToDatabase(it)
            }.addOnFailureListener {
                Log.i("FavoritesRepository", "couldn't load series document ${it.message}")
            }
    }

    private fun insertFirebaseMovieToDatabase(documentSnapshot: DocumentSnapshot) {
        CoroutineScope(IO).launch {
            documentSnapshot.data?.let {
                database.getMultimediaDao().insertSingleMovie(formMovieFromFirestoreDocument(it))
            }
        }
    }

    private fun insertFirebaseSeriesToDatabase(documentSnapshot: DocumentSnapshot) {
        CoroutineScope(IO).launch {
            documentSnapshot.data?.let {
                database.getMultimediaDao().insertSingleSeries(formSeriesFromFirestoreDocument(it))
            }
        }
    }

    private fun formMovieFromFirestoreDocument(data: MutableMap<String, Any>): Movie {
        return Movie(data["title"].toString(), Integer.parseInt(data["id"] .toString()),
            Integer.parseInt(data["totalVotes"].toString()), data["poster"].toString(), data["cover"].toString(),
            (data["rating"] as Double).toFloat(), data["releaseDate"].toString(), "movie",
            data["overview"].toString(), (data["popularity"] as Double).toFloat(), null, null, listOf(),
            isFavorite = true, extraDetailsObtained = false, userId = userId)
    }

    private fun formSeriesFromFirestoreDocument(data: MutableMap<String, Any>): Series {
        return Series(data["title"].toString(), Integer.parseInt(data["id"] .toString()),
            Integer.parseInt(data["totalVotes"].toString()), data["poster"].toString(), data["cover"].toString(),
            (data["rating"] as Double).toFloat(), data["releaseDate"].toString(), "tv",
            data["overview"].toString(), (data["popularity"] as Double).toFloat(), null,
            data["lastAirDate"].toString(),data["inProduction"] as Boolean, listOf(),
            isFavorite = true, extraDetailsObtained = false, userId = userId)
    }

    private fun getFavoritesFromLocalDatabase(multimediaType: Int): LiveData<List<MultiMedia>>{
        return if(multimediaType == MOVIE){
            getFavoriteMoviesFromLocalDatabase()
        } else{
            getFavoriteSeriesFromLocalDatabase()
        }

    }

    private fun getFavoriteMoviesFromLocalDatabase(): LiveData<List<MultiMedia>>{
        runBlocking {
            launch(IO){
                favoriteMoviesLiveData = database.getMultimediaDao().getFavoriteMovies(userId)
            }
        }
        return favoriteMoviesLiveData
    }

    private fun getFavoriteSeriesFromLocalDatabase(): LiveData<List<MultiMedia>>{
        runBlocking {
            launch(IO){
                favoriteSeriesLiveData = database.getMultimediaDao().getFavoriteSeries(userId)
            }
        }
        return favoriteSeriesLiveData
    }

}