package com.example.tmdb.dataClasses

import com.example.tmdb.R
import java.io.Serializable

data class Genre(val id: Int, val name: String): Serializable{

    fun getIcon(): Int{
        return when(name){
            "Action", "Action & Adventure" -> R.drawable.ic_action
            "Adventure" -> R.drawable.ic_adventure
            "Animation" -> R.drawable.ic_animation
            "Comedy" -> R.drawable.ic_comedy
            "Crime" -> R.drawable.ic_crime
            "Documentary" -> R.drawable.ic_documentary
            "Drama" -> R.drawable.ic_drama
            "Family" -> R.drawable.ic_family
            "Fantasy" -> R.drawable.ic_fantasy
            "History" -> R.drawable.ic_history
            "Horror" -> R.drawable.ic_horror
            "kids" -> R.drawable.ic_kids
            "Music" -> R.drawable.ic_music
            "Mystery" -> R.drawable.ic_mystery
            "News" -> R.drawable.ic_news
            "Reality" -> R.drawable.ic_reality
            "Romance" -> R.drawable.ic_romance
            "Science Fiction", "Sci-Fi & Fantasy" -> R.drawable.ic_fiction
            "Soap" -> R.drawable.ic_soap
            "Talk" -> R.drawable.ic_talk
            "Thriller" -> R.drawable.ic_thriller
            "War", "War & Politics" -> R.drawable.ic_war
            "Western" -> R.drawable.ic_western
            else -> 0
        }
    }
}