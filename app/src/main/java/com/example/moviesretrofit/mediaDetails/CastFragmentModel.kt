package com.example.moviesretrofit.mediaDetails

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.moviesretrofit.dataClasses.CreditsResponse
import com.example.moviesretrofit.networking.MultiMediaAPI
import com.example.moviesretrofit.networking.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

object CastFragmentModel {

    private const val key = "097aa1909532e2d795f4f414cf4bc13f"
    private val multiMediaAPI = RetrofitClient.getRetrofitClient().create(MultiMediaAPI::class.java)

    private val creditsLiveData: MutableLiveData<CreditsResponse> = MutableLiveData()
    private var currentId = 0
    private lateinit var credits: CreditsResponse

    fun getMultimediaCredits(id: Int, multimediaType: Int): LiveData<CreditsResponse>{
        returnCachedOrNetworkData(id, multimediaType)

        return creditsLiveData
    }

    private fun returnCachedOrNetworkData(id: Int, multimediaType: Int){
        if(id == currentId){
            sendCachedData()
        }
        else{
            updateCurrentId(id)
            sendNetworkData(id, multimediaType)
        }
    }

    private fun updateCurrentId(id: Int){
        currentId = id
    }

    private fun sendCachedData(){
        creditsLiveData.postValue(credits)
    }

    private fun sendNetworkData(id: Int, multimediaType: Int){
        if(multimediaType == CastFragment.MOVIE)
            makeMovieRequest(id)
        else if (multimediaType == CastFragment.SERIES)
            makeSeriesRequest(id)
    }

    private fun makeMovieRequest(id: Int){
        multiMediaAPI.getMovieCast(id, key).apply { enqueueCallback(this) }
    }

    private fun makeSeriesRequest(id: Int){
        multiMediaAPI.getSeriesCast(id, key).apply { enqueueCallback(this) }
    }

    private fun enqueueCallback(call: Call<CreditsResponse>){
        call.enqueue(object: Callback<CreditsResponse>{
            override fun onResponse(call: Call<CreditsResponse>, response: Response<CreditsResponse>) {
                response.body()?.let {
                    creditsLiveData.postValue(it)
                    updateRepository(it)
                }
            }

            override fun onFailure(call: Call<CreditsResponse>, t: Throwable) {
                Log.i("CastFragmentModel", "Couldn't load cast")
            }

        })
    }

    private fun updateRepository(response: CreditsResponse){
        updateCachedCredits(response)
    }

    private fun updateCachedCredits(response: CreditsResponse){
        credits = response
    }

}