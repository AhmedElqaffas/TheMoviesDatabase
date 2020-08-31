package com.example.tmdb.mediaDetails.credits

import com.example.tmdb.dataClasses.*
import com.example.tmdb.networking.MultiMediaAPI
import com.example.tmdb.networking.RetrofitClient
import retrofit2.Call

object CreditsRetrofitRequester {
    private const val key = "097aa1909532e2d795f4f414cf4bc13f"
    private val multiMediaAPI = RetrofitClient.getRetrofitClient().create(MultiMediaAPI::class.java)

    fun makeMovieCreditsRequest(movie: Movie): Call<MoviesCredits> {
        return multiMediaAPI.getMovieCast(movie.id, key)
    }

    fun makeSeriesCreditsRequest(series: Series): Call<SeriesCredits> {
        return multiMediaAPI.getSeriesCast(series.id, key)
    }

}
