package com.example.moviesretrofit.networking

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {

    private var retrofitClient: Retrofit? = null

    fun getRetrofitClient(): Retrofit{

        if(retrofitClient == null){
            retrofitClient = Retrofit
                .Builder()
                .baseUrl("https://api.themoviedb.org")
                .addConverterFactory(GsonConverterFactory.create()) // ba2olo eny hasta5dem GsonConverter 3ashan a7awel el JSON l class
                .client(OkHttpClient.Builder().build()) // 7aga sabta
                .build()
            return retrofitClient!!
        }
        else{
            return retrofitClient!!
        }
    }
}