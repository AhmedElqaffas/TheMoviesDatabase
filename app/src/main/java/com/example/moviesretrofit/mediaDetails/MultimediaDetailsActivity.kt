package com.example.moviesretrofit.mediaDetails

import android.os.Bundle
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.setPadding
import androidx.lifecycle.observe
import com.example.moviesretrofit.R
import com.example.moviesretrofit.dataClasses.Genre
import com.example.moviesretrofit.dataClasses.MultiMedia
import com.example.moviesretrofit.helpers.ImageZooming
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
            /* make sure the returning item is the one clicked now not the previously clicked item
             That is available in the view model before getMultimediaDetails updates the live data*/
            if(multiMedia.title == it.title){
                multiMedia = it
                showGenres()
            }

        }
    }

    private fun showGenres(){
        multiMedia.genres?.forEach{
            val genreTextView = TextView(this)
            setTextViewConstraints(genreTextView)
            customizeTextViewLook(genreTextView, it)
            genresContainer.addView(genreTextView)
        }
    }

    private fun setTextViewConstraints(genreTextView: TextView){
        val params = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        )

        params.marginStart = dpToPx(5f).toInt()
        genreTextView.layoutParams = params
    }

    private fun customizeTextViewLook(genreTextView: TextView, genre: Genre){
        genreTextView.text = genre.name
        genreTextView.textSize = 18f
        genreTextView.setPadding(dpToPx(6f).toInt())
        genreTextView.background = resources.getDrawable(R.drawable.genre_bg)
        genreTextView.setCompoundDrawablesWithIntrinsicBounds(genre.getIcon(), 0, 0, 0)
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

    private fun dpToPx(dp: Float): Float {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, this.resources.displayMetrics)
    }
}