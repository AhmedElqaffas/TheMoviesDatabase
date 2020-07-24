package com.example.moviesretrofit.mediaDetails

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.moviesretrofit.dataClasses.*
import com.example.moviesretrofit.networking.MultiMediaAPI
import com.example.moviesretrofit.networking.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

object CastFragmentModel {

    private const val key = "097aa1909532e2d795f4f414cf4bc13f"
    private val multiMediaAPI = RetrofitClient.getRetrofitClient().create(MultiMediaAPI::class.java)

    private lateinit var creditsList: List<Person>
    private val creditsLiveData: MutableLiveData<List<Person>> = MutableLiveData()
    private var currentId = 0

    fun getMultimediaCredits(multimedia: MultiMedia): LiveData<List<Person>>{
        creditsLiveData.value = null
        returnCachedOrNetworkData(multimedia)
        return creditsLiveData
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
        if(multimedia.mediaType == "movie")
            makeMovieRequest(multimedia.id)
        else if (multimedia.mediaType == "tv")
            makeSeriesRequest(multimedia.id)
    }

    private fun makeMovieRequest(id: Int){
        multiMediaAPI.getMovieCast(id, key).apply { enqueueCallback(this, id) }
    }

    private fun makeSeriesRequest(id: Int){
        multiMediaAPI.getSeriesCast(id, key).apply { enqueueCallback(this, id) }
    }

    private fun enqueueCallback(call: Call<CreditsResponse>, id: Int){
        call.enqueue(object: Callback<CreditsResponse>{
            override fun onResponse(call: Call<CreditsResponse>, response: Response<CreditsResponse>) {
                response.body()?.let {
                    creditsLiveData.postValue(it.appendCastAndCrewLists())
                    updateRepository(it, id)
                }
            }

            override fun onFailure(call: Call<CreditsResponse>, t: Throwable) {
                Log.i("CastFragmentModel", "Couldn't load cast")
            }

        })
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

}