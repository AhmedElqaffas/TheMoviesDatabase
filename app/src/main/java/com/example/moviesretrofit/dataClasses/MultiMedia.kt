package com.example.moviesretrofit.dataClasses

import androidx.room.Entity
import com.google.gson.annotations.SerializedName
import java.io.Serializable

open class MultiMedia(@SerializedName("name", alternate = ["title"]) val title: String,
                      val id: Int,
                      @SerializedName("vote_count") open val totalVotes: Int,
                      @SerializedName("poster_path") open val poster: String,
                      @SerializedName("backdrop_path") open val cover: String,
                      @SerializedName("vote_average") open val rating: Float,
                      @SerializedName("media_type") open var mediaType: String,
                      open val overview: String): Serializable{

    object Constants{
        const val POPULAR = 0
        const val RATED = 1
    }
}

