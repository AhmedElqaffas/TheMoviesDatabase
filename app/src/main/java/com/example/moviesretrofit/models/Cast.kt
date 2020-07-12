package com.example.moviesretrofit.models

import com.google.gson.annotations.SerializedName

data class CastResponse(val cast: List<Cast>)

data class Cast(val name: String, @SerializedName("profile_path") val image: String)