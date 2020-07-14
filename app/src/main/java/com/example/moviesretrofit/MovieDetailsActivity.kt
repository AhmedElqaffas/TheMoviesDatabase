package com.example.moviesretrofit

import android.content.res.ColorStateList
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_movie_details.*
import java.text.NumberFormat
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
        setMovieVoteCount()
        setMovieName()
        setMovieOverview()
        showCastFragment()
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
        setProgressBarBasedOnRating((movie.rating * 10f).roundToInt())
    }

    private fun setMovieVoteCount(){
        voteCount.text = "${addSeparatorsToNumber(movie.totalVotes)} Votes"
    }

    private fun addSeparatorsToNumber(number: Int): String{
        return NumberFormat.getNumberInstance().format(number)
    }

    private fun setProgressBarBasedOnRating(rating: Int) {
        ratingProgressBar.progress = rating
        when(rating){
            in 0 until 60 ->
                ratingProgressBar.progressTintList = ColorStateList.valueOf(Color.parseColor("#FF0000"))
            in 60 until 75 ->
                ratingProgressBar.progressTintList = ColorStateList.valueOf(Color.parseColor("#FF9800"))
            else ->
                ratingProgressBar.progressTintList = ColorStateList.valueOf(Color.parseColor("#00FF00"))
        }
    }

    private fun setMovieName(){
        movieName.text = movie.title
    }

    private fun setMovieOverview(){
        movieOverview.text = movie.overview
    }

    private fun showCastFragment(){
        val castFragmentInstance = CastFragment.newInstance(movie)
        supportFragmentManager.beginTransaction().replace(R.id.castFragmentFrame,
            castFragmentInstance,"cast fragment")
            .commit()
    }
}