package com.example.moviesretrofit.dataClasses

import androidx.room.Entity

@Entity(tableName = "movies", primaryKeys = ["id"])
class Movie: MultiMedia("",0,0,"","",0f, "movie", "",0) {
}