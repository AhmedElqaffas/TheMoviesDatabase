package com.example.moviesretrofit.dataClasses

import com.google.gson.annotations.SerializedName

data class CreditsResponse(val cast: List<Cast>, val crew: List<Crew>)

open class Person(val name: String, @SerializedName("profile_path") val image: String)

class Cast(/*val name: String,
                @SerializedName("profile_path") val image: String*/): Person("name", "image")

 class Crew(/*val name: String,
                @SerializedName("profile_path") val image: String*/): Person("name", "image")