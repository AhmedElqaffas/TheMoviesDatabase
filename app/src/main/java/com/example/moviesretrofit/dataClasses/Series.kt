package com.example.moviesretrofit.dataClasses

import androidx.room.Entity

@Entity(tableName = "series", primaryKeys = ["id"])
class Series() : MultiMedia("",0,0,"","",0f, "tv", "",0) {
}