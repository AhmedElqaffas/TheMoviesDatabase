package com.example.tmdb.dataClasses

import androidx.room.*
import com.google.gson.annotations.SerializedName

open class CreditsResponse(var id: Int,
                           var cast: List<Cast>,
                           var crew: List<Crew>,
                           var type: String) {

    fun appendCastAndCrewLists(): List<Person>{
        val combinedList = mutableListOf<Person>()
        combinedList.addAll(cast)
        combinedList.addAll(crew)
        return combinedList.toList()
    }
}


class MoviesCredits: CreditsResponse(0, listOf(), listOf(), "movie")

class SeriesCredits : CreditsResponse(0, listOf(), listOf(), "series")

@Entity(tableName = "persons", primaryKeys = ["name"])
open class Person(var name: String,
                  @SerializedName("profile_path") var image: String?)

class Cast : Person("name", "image")

@Entity(tableName = "crew")
class Crew : Person("name", "image")