package com.example.moviesretrofit

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast

class MovieDetailsActivity : AppCompatActivity() {

    private lateinit var movie: Movie

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_movie_details)

        getClickedMovie()
    }

    private fun getClickedMovie(){
        movie = intent.getSerializableExtra("movie") as Movie
        Toast.makeText(this, movie.title, Toast.LENGTH_SHORT).show()
    }
}