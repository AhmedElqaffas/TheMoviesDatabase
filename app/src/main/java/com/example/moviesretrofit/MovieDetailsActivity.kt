package com.example.moviesretrofit

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_movie_details.*
import kotlin.math.roundToInt

class MovieDetailsActivity : AppCompatActivity() {

    private lateinit var movie: Movie

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_movie_details)
        getClickedMovie()
        setMovieDetails()
    }

    private fun getClickedMovie(){
        movie = intent.getSerializableExtra("movie") as Movie
    }

    private fun setMovieDetails(){
        setMovieCover()
        setMoviePoster()
        setMovieRating()
        setMovieName()
        setMovieOverview()
    }

    private fun setMovieCover(){
        Picasso.get()
            .load("https://image.tmdb.org/t/p/original${movie.cover}")
            .fit()
            .into(movieCover)
    }

    private fun setMoviePoster(){
        Picasso.get()
            .load("https://image.tmdb.org/t/p/w200${movie.poster}")
            .fit()
            .into(moviePoster)
    }

    private fun setMovieRating(){
        movieRating.text = movie.rating.toString()
        ratingProgressBar.progress = (movie.rating * 10f).roundToInt()
    }

    private fun setMovieName(){
        movieName.text = movie.title
    }

    private fun setMovieOverview(){
        movieOverview.text = movie.overview
    }
}