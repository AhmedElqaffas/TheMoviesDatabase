package com.example.moviesretrofit.networking

import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.GET
import retrofit2.http.Query

interface MoviesAPI {

    @GET("/3/movie/popular")
    fun getPopularMovies(@Query("api_key") key: String,
                         @Query("page") page: Int): Call<PopularMoviesResponse>

}