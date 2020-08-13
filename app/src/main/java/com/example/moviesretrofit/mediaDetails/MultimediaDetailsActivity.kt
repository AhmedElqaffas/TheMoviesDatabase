package com.example.moviesretrofit.mediaDetails

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import androidx.activity.viewModels
import androidx.lifecycle.observe
import com.example.moviesretrofit.R
import com.example.moviesretrofit.dataClasses.Movie
import com.example.moviesretrofit.helpers.ImageZooming
import com.example.moviesretrofit.dataClasses.MultiMedia
import com.example.moviesretrofit.mediaDetails.credits.CreditsFragment
import com.example.moviesretrofit.mediaDetails.infoDialogFragment.InfoDialogFragment
import com.example.moviesretrofit.oftenfragments.BackButtonFragment
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_movie_details.*
import java.text.NumberFormat
import kotlin.math.roundToInt

class MultimediaDetailsActivity : AppCompatActivity() {

    private lateinit var multiMedia: MultiMedia
    private val multimediaDetailsViewModel: MultimediaDetailsViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_movie_details)
        getClickedMultimedia()
        setMovieDetails()
        showCastFragmentIfNoSavedInstance(savedInstanceState)
        getMultimediaDetails()
        setInfoButtonClickListener()

    }

    private fun getClickedMultimedia(){
        multiMedia = intent.getSerializableExtra("media") as MultiMedia
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
        ratingProgressBar.progressTintList = multimediaDetailsViewModel.determineProgressBarColor(rating)
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
        val castFragmentInstance = CreditsFragment.newInstance(multiMedia)
        supportFragmentManager.beginTransaction().replace(
            R.id.castFragmentFrame,
            castFragmentInstance,"cast fragment")
            .commit()
    }

    private fun getMultimediaDetails(){
        multimediaDetailsViewModel.getMultimediaDetails(multiMedia).observe(this){
            println("Observed...............")
            multiMedia = it
        }
    }

    private fun setInfoButtonClickListener(){
        moreInfoButton.setOnClickListener {
            showMoreInfoDialogFragment()
        }
    }

    private fun showMoreInfoDialogFragment(){
        val infoDialogFragment = InfoDialogFragment.newInstance(multiMedia)
        infoDialogFragment.show(supportFragmentManager, "informationDialog")
    }

    fun zoomImage(view: View){
        ImageZooming.zoomImage(view as ImageView, this)
    }
}