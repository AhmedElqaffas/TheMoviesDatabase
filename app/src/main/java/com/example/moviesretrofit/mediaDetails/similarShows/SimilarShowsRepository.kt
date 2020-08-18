package com.example.moviesretrofit.mediaDetails.similarShows

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.example.moviesretrofit.dataClasses.MultiMedia
import com.example.moviesretrofit.dataClasses.MultiMediaResponse
import com.example.moviesretrofit.networking.MultiMediaAPI
import com.example.moviesretrofit.networking.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

object SimilarShowsRepository{

    private const val key = "097aa1909532e2d795f4f414cf4bc13f"
    private val multiMediaAPI = RetrofitClient.getRetrofitClient().create(MultiMediaAPI::class.java)

    private var cachedList: List<MultiMedia> = listOf()
    private val similarShowsLiveData: MutableLiveData<List<MultiMedia>> = MutableLiveData()
    private var currentId = 0

    fun getSimilarShows(multiMedia: MultiMedia): MutableLiveData<List<MultiMedia>>{

        if(cachedList.isNotEmpty() && currentId == multiMedia.id){
            similarShowsLiveData.value = cachedList
        }

        else{
            makeSimilarShowsRequest(multiMedia)
            currentId = multiMedia.id
        }

        return similarShowsLiveData
    }

    private fun makeSimilarShowsRequest(multiMedia: MultiMedia){
        multiMedia.makeSimilarShowsRequest(key, multiMediaAPI)?.apply {
            enqueueCallback(this)
        }
    }

    private fun enqueueCallback(call: Call<MultiMediaResponse>){
        call.enqueue(object: Callback<MultiMediaResponse>{

            override fun onResponse(call: Call<MultiMediaResponse>, response: Response<MultiMediaResponse>) {
                similarShowsLiveData.postValue(response.body()?.results)
                cachedList = response.body()?.results!!
            }

            override fun onFailure(call: Call<MultiMediaResponse>, t: Throwable) {
                Log.i("SimilarShowsRepository", "Couldn't get similar shows")
            }

        })

    }
}