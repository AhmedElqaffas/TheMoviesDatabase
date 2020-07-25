package com.example.moviesretrofit.dataClasses

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import java.io.Serializable

open class MultiMedia(@SerializedName("name", alternate = ["title"]) var title: String,
                      var id: Int,
                      @SerializedName("vote_count") open var totalVotes: Int,
                      @SerializedName("poster_path") open var poster: String,
                      @SerializedName("backdrop_path") open var cover: String,
                      @SerializedName("vote_average") open var rating: Float,
                      @SerializedName("media_type") open var mediaType: String,
                      open var overview: String,
                      open var popularity: Int): Serializable{

    object Constants{
        const val POPULAR = 0
        const val RATED = 1
    }
}

