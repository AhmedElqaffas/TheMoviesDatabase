package com.example.moviesretrofit.mediaDetails

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.moviesretrofit.dataClasses.*
import com.example.moviesretrofit.database.AppDatabase
import com.example.moviesretrofit.networking.MultiMediaAPI
import com.example.moviesretrofit.networking.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.Exception

object CastFragmentModel {

    private lateinit var database: AppDatabase

    private lateinit var creditsList: List<Person>
    private val creditsLiveData: MutableLiveData<List<Person>> = MutableLiveData()
    private var currentId = 0


    fun createDatabase(context: Context) {
        database = AppDatabase.getDatabase(context)
    }

    fun getMultimediaCredits(multimedia: MultiMedia): LiveData<List<Person>>{
        val castedMultimedia = castMultimedia(multimedia)
        creditsLiveData.value = null
        returnCachedOrNetworkData(castedMultimedia)
        return creditsLiveData
    }

    private fun castMultimedia(multimedia: MultiMedia): MultiMedia{
        return when (multimedia.mediaType) {
            "movie" -> Movie(multimedia.title, multimedia.id,
                multimedia.totalVotes, multimedia.poster, multimedia.cover, multimedia.rating,
                multimedia.mediaType, multimedia.overview, multimedia.popularity)

            "tv" -> Series(multimedia.title, multimedia.id,
                multimedia.totalVotes, multimedia.poster, multimedia.cover, multimedia.rating,
                multimedia.mediaType, multimedia.overview, multimedia.popularity)

            else -> throw Exception("Couldn't cast multimedia in cast repo")
        }
    }

    private fun returnCachedOrNetworkData(multimedia: MultiMedia){
        if(multimedia.id == currentId){
            sendCachedData()
        }
        else{
            sendNetworkData(multimedia)
        }
    }

    private fun sendCachedData(){
        creditsLiveData.postValue(creditsList)
    }

    private fun sendNetworkData(multimedia: MultiMedia){
        multimedia.makeCreditsRequest().apply {
            enqueueCallback(this!! , multimedia.id)
        }
    }

    private fun makeMovieRequest(id: Int){

    }

    private fun makeSeriesRequest(id: Int){

    }

    private fun enqueueCallback(call: Call<CreditsResponse>, id: Int){
        call.enqueue(object: Callback<CreditsResponse>{
            override fun onResponse(call: Call<CreditsResponse>, response: Response<CreditsResponse>) {
                response.body()?.let {
                    creditsLiveData.postValue(it.appendCastAndCrewLists())
                    updateRepository(it, id)
                    updateDatabase(it)
                }
            }

            override fun onFailure(call: Call<CreditsResponse>, t: Throwable) {
                Log.i("CastFragmentModel", "Couldn't load cast")
              //  if(creditsList.isEmpty())
                //    returnDatabaseData(id, type)
            }

        })
    }

    private fun returnDatabaseData(id: Int, mediaType: String){
       /* creditsLiveData.postValue(database.getCreditsDao().getCredits(id, mediaType)
            .appendCastAndCrewLists())
        */
    }

    private fun updateRepository(response: CreditsResponse, id: Int){
        updateCurrentId(id)
        updateCachedCredits(response)
    }

    private fun updateCurrentId(id: Int){
        currentId = id
    }

    private fun updateCachedCredits(response: CreditsResponse){
        creditsList = response.appendCastAndCrewLists()
    }

    private fun updateDatabase(credits: CreditsResponse) {
        database.getCreditsDao().insertCredits(credits.appendCastAndCrewLists())
    }

}