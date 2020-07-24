package com.example.moviesretrofit.dataClasses

import com.google.gson.annotations.SerializedName

data class CreditsResponse(val cast: List<Cast>, val crew: List<Crew>) {

    fun appendCastAndCrewLists(): List<Person>{
        val combinedList = mutableListOf<Person>()
        combinedList.addAll(cast)
        combinedList.addAll(crew)
        return combinedList.toList()
    }
}

open class Person(val name: String, @SerializedName("profile_path") val image: String)

class Cast() : Person("name", "image")
class Crew() : Person("name", "image")