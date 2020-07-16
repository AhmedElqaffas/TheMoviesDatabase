package com.example.moviesretrofit.networking

import com.example.moviesretrofit.CastResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface MultiMediaAPI {

    @GET("/3/movie/popular")
    fun getPopularMovies(@Query("api_key") key: String,
                         @Query("page") page: Int): Call<MultiMediaResponse>

    @GET("/3/movie/top_rated")
    fun getHighRatedMovies(@Query("api_key") key: String,
                           @Query("page") page: Int): Call<MultiMediaResponse>

    @GET("/3/search/multi")
    fun findMediaByName(@Query("api_key") key: String,
                        @Query("page") page: Int,
                        @Query("query") name: String,
                        @Query("language") language: String = "en-US",
                        @Query("include_adult") adult: Boolean = true): Call<MultiMediaResponse>

    @GET("/3/movie/{movie_id}/credits")
    fun getMovieCast(@Path("movie_id") id: Int,
                     @Query("api_key") key: String): Call<CastResponse>

    @GET("/3/tv/popular")
    fun getPopularSeries(@Query("api_key") key: String,
                         @Query("page") page: Int): Call<MultiMediaResponse>

    @GET("/3/tv/top_rated")
    fun getHighRatedSeries(@Query("api_key") key: String,
                           @Query("page") page: Int): Call<MultiMediaResponse>

    @GET("/3/tv/{tv_id}/credits")
    fun getSeriesCast(@Path("tv_id") id: Int,
                     @Query("api_key") key: String): Call<CastResponse>
}