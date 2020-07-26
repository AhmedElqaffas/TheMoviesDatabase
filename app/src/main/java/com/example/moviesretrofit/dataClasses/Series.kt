package com.example.moviesretrofit.dataClasses

import androidx.room.Entity
import com.example.moviesretrofit.mediaDetails.MultimediaRetrofitRequester
import retrofit2.Call

@Entity(tableName = "series", primaryKeys = ["id"])
class Series() : MultiMedia("",0,0,"","",0f, "tv", "",0f) {


    constructor(title: String, id: Int, totalVotes: Int, poster: String?, cover: String?,
                rating: Float, mediaType: String, overview: String?, popularity: Float): this(){
        this.title = title
        this.id = id
        this.totalVotes = totalVotes
        this.poster = poster
        this.cover = cover
        this.rating = rating
        this.mediaType = mediaType
        this.overview = overview
        this.popularity = popularity
    }


    override fun makeCreditsRequest(): Call<CreditsResponse> {
        return MultimediaRetrofitRequester.makeSeriesCreditsRequest(this) as Call<CreditsResponse>
    }

}