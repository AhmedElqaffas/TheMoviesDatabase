package com.example.moviesretrofit.mediaDetails

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.moviesretrofit.dataClasses.MultiMedia
import com.example.moviesretrofit.database.AppDatabase
import com.example.moviesretrofit.networking.MultiMediaAPI
import com.example.moviesretrofit.networking.RetrofitClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

object MultimediaDetailsRepository {

    private lateinit var database: AppDatabase

    private const val key = "097aa1909532e2d795f4f414cf4bc13f"
    private val multiMediaAPI = RetrofitClient.getRetrofitClient().create(MultiMediaAPI::class.java)

    private val multimediaLiveData: MutableLiveData<MultiMedia> = MutableLiveData()

    private var cachedMultimedia: MultiMedia? = null

    fun createDatabase(context: Context){
        database = AppDatabase.getDatabase(context)
    }

    fun getMultimediaDetails(multimedia: MultiMedia): LiveData<MultiMedia>{

        returnCacheOrNetworkData(multimedia)

        return multimediaLiveData
    }

    private fun returnCacheOrNetworkData(multimedia: MultiMedia){
        if(isMultimediaCached(multimedia)){
            sendCachedData()
        }
        else{
            sendNetworkData(multimedia)
        }
    }

    private fun isMultimediaCached(multimedia: MultiMedia): Boolean{
        return multimedia.id == cachedMultimedia?.id && multimedia.mediaType == cachedMultimedia?.mediaType
                && multimedia.extraDetailsObtained
    }

    private fun sendCachedData(){
        multimediaLiveData.value = cachedMultimedia
    }

    private fun sendNetworkData(multimedia: MultiMedia){
        multimedia.makeDetailsRequest(key, multiMediaAPI).apply {
            enqueueCallback(this!!, multimedia)
        }
    }

    private fun enqueueCallback(call: Call<MultiMedia>, multimedia: MultiMedia){
        call.enqueue(object: Callback<MultiMedia> {
            override fun onResponse(call: Call<MultiMedia>, response: Response<MultiMedia>) {
                response.body()?.let {
                    multimedia.copyObtainedDetails(it)
                    // ExtraDetails successfully obtained from API, set boolean to true
                    multimedia.extraDetailsObtained = true
                    //This multimedia may already be in database because it is in favorites
                    // if that is the case, update the isFavorite of this multimedia and post it
                    getExistingMovieIsFavorite(multimedia)
                }
            }

            override fun onFailure(call: Call<MultiMedia>, t: Throwable) {
                Log.i("MultimediaDetails", "Couldn't load details")
                Log.i("MultimediaDetails", t.message)
                returnDatabaseData(multimedia)
            }

        })
    }

    private fun updateRepository(multimedia: MultiMedia){
        cachedMultimedia = multimedia
    }

    // Will replace the old entry with the new detailed entry
    private fun saveInDatabase(multimedia: MultiMedia){
        CoroutineScope(Dispatchers.IO).launch {
            multimedia.saveInDatabase(database)
        }
    }

    private fun returnDatabaseData(multimedia: MultiMedia){
        var databaseData: MultiMedia? = null
        CoroutineScope(Dispatchers.IO).launch {
            multimedia.getFromDatabase(database)?.let {
                databaseData = it
            }
        }.invokeOnCompletion {
            databaseData?.let {
                multimediaLiveData.postValue(it)
                cachedMultimedia = it
            }
        }
    }

    fun toggleFavorites(multimedia: MultiMedia){
        multimedia.isFavorite = !multimedia.isFavorite
        updateRepository(multimedia)
        toggleFavoritesInDatabase(multimedia)
        multimediaLiveData.value = multimedia
    }

    private fun toggleFavoritesInDatabase(multimedia: MultiMedia){
        CoroutineScope(Dispatchers.IO).launch {
            multimedia.updateFavoriteField(database)
        }
    }

    private fun getExistingMovieIsFavorite(multimedia: MultiMedia){
        CoroutineScope(Dispatchers.IO).launch {
            multimedia.getExistingShowIsFavorite(database)
        }.invokeOnCompletion {
            multimediaLiveData.postValue(multimedia)
            updateRepository(multimedia)
            saveInDatabase(multimedia)
        }
    }
}