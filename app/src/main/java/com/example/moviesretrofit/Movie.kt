package com.example.moviesretrofit

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class Movie(val title: String, @SerializedName("poster_path") val poster: String): Serializable