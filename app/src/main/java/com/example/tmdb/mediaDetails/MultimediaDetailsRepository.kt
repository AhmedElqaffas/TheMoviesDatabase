package com.example.tmdb.mediaDetails

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.liveData
import com.example.tmdb.dataClasses.MultiMedia
import com.example.tmdb.database.AppDatabase
import com.example.tmdb.main.dataRepositories.FavoritesRepository
import com.example.tmdb.networking.MultiMediaAPI
import com.example.tmdb.networking.RetrofitClient
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


object MultimediaDetailsRepository: MultiMedia.FirebaseCallback {

    const val FAILURE = 0
    const val SUCCESS = 1
    const val INCOMPLETE = 2

    private const val key = "097aa1909532e2d795f4f414cf4bc13f"
    private val multiMediaAPI = RetrofitClient.getRetrofitClient().create(MultiMediaAPI::class.java)

    private lateinit var database: AppDatabase

    private val multimediaLiveData: MutableLiveData<MultiMedia> = MutableLiveData()
    private val toggleFavoritesLiveData: MutableLiveData<Int> = MutableLiveData()

    private var cachedMultimedia: MultiMedia? = null
    private lateinit var userId: String

    private val firebaseAuth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()

    fun createDatabase(context: Context){
        database = AppDatabase.getDatabase(context)
    }

    fun getMultimediaDetails(multimedia: MultiMedia, userId: String): LiveData<MultiMedia>{

        returnCacheOrNetworkData(multimedia, userId)

        return multimediaLiveData
    }

    private fun returnCacheOrNetworkData(multimedia: MultiMedia, userId: String){
        if(isMultimediaCached(multimedia, userId)){
            sendCachedData()
        }
        else{
            this.userId = userId
            sendNetworkData(multimedia)
        }
    }

    private fun isMultimediaCached(multimedia: MultiMedia, userId: String): Boolean{
        return multimedia.id == cachedMultimedia?.id && multimedia.mediaType == cachedMultimedia?.mediaType
                && cachedMultimedia!!.extraDetailsObtained && this.userId == userId
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
        call.enqueue(object : Callback<MultiMedia> {
            override fun onResponse(call: Call<MultiMedia>, response: Response<MultiMedia>) {
                response.body()?.let {
                    multimedia.copyObtainedDetails(it)
                    // ExtraDetails successfully obtained from API, set boolean to true
                    multimedia.extraDetailsObtained = true
                    /*This multimedia may already be in database because it is in favorites
                     if that is the case, we need to get the fields that are not obtained from
                     The API but added by the program during execution, like isFavorite and userId*/
                    getExistingFields(multimedia)
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
        CoroutineScope(IO).launch {
            multimedia.saveInDatabase(database)
        }
    }

    private fun returnDatabaseData(multimedia: MultiMedia){
        var databaseData: MultiMedia? = null
        CoroutineScope(IO).launch {
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

    fun toggleFavorites(multimedia: MultiMedia): LiveData<Int>{
        toggleFavoritesLiveData.value = INCOMPLETE

        when {
            userId != "local" -> {
                toggleFavoritesInFirestore(multimedia)
            }
            userId == "local" -> {
                commitFavoritesChangeLocally(multimedia)
                toggleFavoritesLiveData.value = SUCCESS
            }
            else -> {
                toggleFavoritesLiveData.value = FAILURE
                return toggleFavoritesLiveData
            }
        }
        return toggleFavoritesLiveData
    }

    /**
     * When createOrRemoveFirestoreRecord result arrives, onFirebaseRequestEnded callback method
     * will be called to resume committing changes locally (room and repo cache)
     */
    private fun toggleFavoritesInFirestore(multimedia: MultiMedia){
        CoroutineScope(IO).launch {
            multimedia.createOrRemoveFirestoreRecord(firestore, userId, this@MultimediaDetailsRepository)
        }
    }

    private fun commitFavoritesChangeLocally(multimedia: MultiMedia){
        multimedia.isFavorite = !multimedia.isFavorite
        multimedia.userId = userId
        multimediaLiveData.value = multimedia
        updateRepository(multimedia)
        toggleFavoritesInDatabase(multimedia)
    }

    private fun toggleFavoritesInDatabase(multimedia: MultiMedia){
        CoroutineScope(IO).launch {
            multimedia.updateFavoriteInDatabase(database)
        }
    }

    private fun getExistingFields(multimedia: MultiMedia){
        CoroutineScope(IO).launch {
            multimedia.getExistingShowFields(database)
        }.invokeOnCompletion {
            multimediaLiveData.postValue(multimedia)
            updateRepository(multimedia)
            saveInDatabase(multimedia)
        }
    }

    override fun onFirebaseRequestEnded(success: Boolean, multimedia: MultiMedia) {
        // If firestore was updated successfully, then update room database and repo cache
        if(success){
            commitFavoritesChangeLocally(multimedia)
            toggleFavoritesLiveData.postValue(SUCCESS)
        }
        else{
            toggleFavoritesLiveData.postValue(FAILURE)
        }

    }
}