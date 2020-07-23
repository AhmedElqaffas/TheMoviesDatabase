package com.example.moviesretrofit.mediaDetails

import android.content.res.ColorStateList
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import com.example.moviesretrofit.R
import com.example.moviesretrofit.helpers.ImageZooming
import com.example.moviesretrofit.dataClasses.MultiMedia
import com.example.moviesretrofit.oftenfragments.BackButtonFragment
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_movie_details.*
import java.text.NumberFormat
import kotlin.math.roundToInt

class MultiMediaDetailsActivity : AppCompatActivity() {

    private lateinit var multiMedia: MultiMedia
    private var multiMediaType: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_movie_details)
        getClickedMovie()
        setMovieDetails()
        showCastFragmentIfNoSavedInstance(savedInstanceState)

    }

    private fun getClickedMovie(){
        multiMedia = intent.getSerializableExtra("media") as MultiMedia
        multiMediaType = intent.getIntExtra("Media Type",1)
    }

    private fun setMovieDetails(){
        showTopFragment()
        setMediaCover()
        setMediaPoster()
        setMediaRating()
        setMediaVoteCount()
        setMediaName()
        setMediaOverview()
    }

    private fun showTopFragment(){
        supportFragmentManager.beginTransaction()
            .replace(R.id.topButtonsContainer, BackButtonFragment(), "topFragment")
            .commit()
    }

    private fun setMediaCover(){
        Picasso.get()
            .load("https://image.tmdb.org/t/p/original${multiMedia.cover}")
            .into(movieCover)
    }

    private fun setMediaPoster(){
        Picasso.get()
            .load("https://image.tmdb.org/t/p/original${multiMedia.poster}")
            .placeholder(R.drawable.loading_movie_image)
            .into(moviePoster)

    }

    private fun setMediaRating(){
        movieRating.text = multiMedia.rating.toString()
        setProgressBarBasedOnRating((multiMedia.rating * 10f).roundToInt())
    }

    private fun setMediaVoteCount(){
        voteCount.text = "${addSeparatorsToNumber(multiMedia.totalVotes)} Votes"
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

    private fun setMediaName(){
        movieName.text = multiMedia.title
    }

    private fun setMediaOverview(){
        movieOverview.text = multiMedia.overview
    }

    private fun showCastFragmentIfNoSavedInstance(savedInstanceState: Bundle?){
        if(savedInstanceState == null)
            showCastFragment()
    }

    private fun showCastFragment(){
        val castFragmentInstance =
            CastFragment.newInstance(
                multiMedia,
                multiMediaType
            )
        supportFragmentManager.beginTransaction().replace(
            R.id.castFragmentFrame,
            castFragmentInstance,"cast fragment")
            .commit()
    }

    fun zoomImage(view: View){
        ImageZooming.zoomImage(view as ImageView, this)
    }
}