package com.example.tmdb.mediaDetails

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.tmdb.dataClasses.MultiMedia
import com.example.tmdb.database.AppDatabase
import com.example.tmdb.networking.MultiMediaAPI
import com.example.tmdb.networking.RetrofitClient
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
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

    private val firebaseAuth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()

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
                && cachedMultimedia!!.extraDetailsObtained
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

    fun toggleFavorites(multimedia: MultiMedia){
        multimedia.isFavorite = !multimedia.isFavorite
        setUserId(multimedia)
        updateRepository(multimedia)
        toggleFavoritesInDatabase(multimedia)
        firebaseAuth.currentUser?.let { toggleFavoritesInFirestore(multimedia) }
        multimediaLiveData.value = multimedia
    }

    private fun setUserId(multimedia: MultiMedia){
        if(firebaseAuth.currentUser != null){
            multimedia.userId = firebaseAuth.currentUser!!.uid
        }else{
            multimedia.userId = "local"
        }
    }

    private fun toggleFavoritesInDatabase(multimedia: MultiMedia){
        CoroutineScope(IO).launch {
            multimedia.updateFavoriteInDatabase(database)
        }
    }

    private fun toggleFavoritesInFirestore(multimedia: MultiMedia){
        CoroutineScope(IO).launch {
            createOrRemoveFirestoreRecord(multimedia)
        }
    }

    private fun createOrRemoveFirestoreRecord(multimedia: MultiMedia){
        val documentReference = firestore.collection("favorites")
            .document(firebaseAuth.currentUser!!.uid)

        documentReference.get().addOnSuccessListener {
            if(it.data?.get(multimedia.title) != null){
                println(it.data?.get(multimedia.title))
                it.data?.remove(multimedia.title)
            }
            else{
                val multimediaMap: HashMap<String, Any> = hashMapOf()
                multimediaMap[multimedia.title] = multimedia.mediaType
                documentReference.set(multimediaMap)
            }
        }.addOnFailureListener {
            println(it.message+"///////////////")
            Log.i("MultimediaDetails", it.message)
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

    private fun getExistingMovieIsFavorite(multimedia: MultiMedia){

    }
}