package com.example.moviesretrofit.dataClasses

import com.google.gson.annotations.SerializedName

data class CreditsResponse(val cast: List<Person>, val crew: List<Person>)

data class Person(val name: String, @SerializedName("profile_path") val image: String)

/*data class Cast(override val name: String,
                @SerializedName("profile_path") override val image: String): Person(name, image)

data class Crew(override val name: String,
                @SerializedName("profile_path") override val image: String): Person(name, image)*/