package com.example.tmdb.mediaDetails

import android.os.Bundle
import android.util.DisplayMetrics
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationSet
import android.view.animation.ScaleAnimation
import android.view.animation.TranslateAnimation
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.setPadding
import androidx.lifecycle.observe
import com.example.tmdb.R
import com.example.tmdb.dataClasses.Genre
import com.example.tmdb.dataClasses.MultiMedia
import com.example.tmdb.helpers.ImageZooming
import com.example.tmdb.mediaDetails.credits.CreditsFragment
import com.example.tmdb.mediaDetails.infoDialogFragment.InfoDialogFragment
import com.example.tmdb.mediaDetails.similarShows.SimilarShowsFragment
import com.example.tmdb.oftenfragments.BackButtonFragment
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_movie_details.*
import kotlinx.android.synthetic.main.activity_movie_details.swipeRefresh
import java.text.NumberFormat
import kotlin.math.roundToInt


class MultimediaDetailsActivity : AppCompatActivity() {

    private lateinit var multiMedia: MultiMedia
    private val multimediaDetailsViewModel: MultimediaDetailsViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_movie_details)
        getClickedMultimedia()
        setMultimediaDetails()
        createCreditsFragmentIfNoSavedInstance(savedInstanceState)
        getExtraMultimediaDetails()
        setupFavoritesButton()
        setInfoButtonClickListener()
        setSwipeRefreshListener()

    }

    private fun getClickedMultimedia(){
        multiMedia = intent.getSerializableExtra("media") as MultiMedia
    }

    private fun setMultimediaDetails(){
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

    private fun createCreditsFragmentIfNoSavedInstance(savedInstanceState: Bundle?){
        if(savedInstanceState == null){
            createCreditsFragment()
        }
    }

    private fun createCreditsFragment(){
        val castFragmentInstance = CreditsFragment.newInstance(multiMedia)
        supportFragmentManager.beginTransaction().replace(
            R.id.creditsFragmentFrame,
            castFragmentInstance,"creditsFragment")
            .commit()
    }

    private fun getExtraMultimediaDetails(){
        multimediaDetailsViewModel.getMultimediaDetails(multiMedia).observe(this){
            /* make sure the returning item is the one clicked now not the previously clicked item
             That is available in the view model before getMultimediaDetails updates the live data*/
            if(multiMedia.title == it.title){
                multiMedia = it
                showGenres()
                manageFavorite()
            }

        }
    }

    private fun showGenres(){
        // Clear the linear layout, to avoid duplicating data each time the observer callback fires
        genresContainer.removeAllViews()
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

    /**
     * If the movie is in favorites, show a green check mark above the favorite button, and get
     * a list of similar shows. Otherwise hide the check and the similar shows section.
     */
    private fun manageFavorite(){
        if(multiMedia.isFavorite){
            addFavoriteShowDetails()
        }
        else {
            hideFavoriteShowDetails()
        }
    }

    private fun addFavoriteShowDetails(){
        checked.visibility = View.VISIBLE
        showSimilarShows()
    }

    private fun showSimilarShows(){
        separatorBelowCredits.visibility = View.VISIBLE
        similarShowsFragmentFrame.visibility = View.VISIBLE
        similarShowsWord.visibility = View.VISIBLE
        createSimilarShowsFragmentIfNotExists()
    }

    private fun hideFavoriteShowDetails(){
        checked.visibility = View.INVISIBLE
        hideSimilarShows()
    }

    private fun hideSimilarShows(){
        separatorBelowCredits.visibility = View.GONE
        similarShowsFragmentFrame.visibility = View.GONE
        similarShowsWord.visibility = View.GONE
    }

    private fun createSimilarShowsFragmentIfNotExists(){
        if(supportFragmentManager.findFragmentByTag("similarShows") == null){
            createSimilarShowsFragment()
        }
    }

    private fun createSimilarShowsFragment(){
        val similarShowsFragmentInstance = SimilarShowsFragment.newInstance(multiMedia)
        supportFragmentManager.beginTransaction().replace(
            R.id.similarShowsFragmentFrame,
            similarShowsFragmentInstance, "similarShows")
            .commit()
    }

    private fun setupFavoritesButton(){
        favoriteButton.setOnClickListener {
            addOrRemoveFromFavorites()
        }
    }

    /**
     * If the user removed the multimedia from favorites, the green check mark
     * on the button should disappear, and a broken heart animation should happen.
     * Else, show a full heart animation and a green check mark
     */
    private fun addOrRemoveFromFavorites(){
        multimediaDetailsViewModel.toggleFavorites(multiMedia).observe(this){
            if(it == MultimediaDetailsRepository.SUCCESS){
                if(multiMedia.isFavorite){
                    showAddToFavoriteAnimation()
                }
                else{
                    showRemovedFromFavoriteAnimation()
                }
            }
            else if(it == MultimediaDetailsRepository.FAILURE){
                Toast.makeText(this, "Couldn't communicate with cloud server", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun showAddToFavoriteAnimation(){
        fullHeartImage.visibility = View.VISIBLE
        doHeartZoomAnimation()
        fullHeartImage.visibility = View.INVISIBLE
    }

    private fun doHeartZoomAnimation(){
        val animationSet = AnimationSet(true)
        val zoomInAnimation = ScaleAnimation(0f, 10f, 0f, 10f, 35f, 10f)
        zoomInAnimation.duration = 1000
        animationSet.addAnimation(zoomInAnimation)
        fullHeartImage.startAnimation(animationSet)
    }

    /**
     * There are two hidden broken heart pieces. Show the pieces images, move the left heart piece
     * to the left end of the screen, and move the right heart piece to the right end of the screen
     */
    private fun showRemovedFromFavoriteAnimation(){
        leftBrokenHeart.visibility = View.VISIBLE
        rightBrokenHeart.visibility = View.VISIBLE
        doBrokenHeartAnimation()
        leftBrokenHeart.visibility = View.INVISIBLE
        rightBrokenHeart.visibility = View.INVISIBLE
    }

    private fun doBrokenHeartAnimation(){
        // The screenWidth * 2 is a heuristic to make sure the views have completely vanished
        val translationLeft = TranslateAnimation(0f,-getScreenWidth()*2, 0f,0f )
        val translationRight = TranslateAnimation(0f,getScreenWidth()*2, 0f,0f )
        translationLeft.duration = 800
        translationRight.duration = 800
        translationLeft.startOffset = 200
        translationRight.startOffset = 200
        leftBrokenHeart.startAnimation(translationLeft)
        rightBrokenHeart.startAnimation(translationRight)
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

    private fun setSwipeRefreshListener(){
        swipeRefresh.setOnRefreshListener {
            // Set the movie details
            setMultimediaDetails()
            multimediaDetailsViewModel.getMultimediaDetails(multiMedia)
            // Create the fragments again to remake the requests
            createSimilarShowsFragment()
            createCreditsFragment()
            // End the refreshing
            swipeRefresh.isRefreshing = false
        }
    }

    fun zoomImage(view: View){
        ImageZooming.zoomImage(view as ImageView, this)
    }

    private fun dpToPx(dp: Float): Float {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, this.resources.displayMetrics)
    }

    private fun getScreenWidth(): Float{
        val display = windowManager.defaultDisplay
        val outMetrics = DisplayMetrics()
        display.getMetrics(outMetrics)
        val density = resources.displayMetrics.density
        return outMetrics.widthPixels / density
    }
}