package com.example.moviesretrofit.main.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import com.example.moviesretrofit.mediaDetails.MultimediaDetailsActivity
import com.example.moviesretrofit.recyclersAdapters.MultiMediaRecyclerAdapter
import com.example.moviesretrofit.R
import com.example.moviesretrofit.main.MainViewModel
import com.example.moviesretrofit.dataClasses.MultiMedia
import kotlinx.android.synthetic.main.fragment_media_recycler.*
import kotlinx.android.synthetic.main.fragment_movies.*
import kotlinx.android.synthetic.main.fragment_series.*

class MultiMediaRecyclerFragment : Fragment(),
    MultiMediaRecyclerAdapter.MultiMediaRecyclerInteraction{

    private lateinit var multiMediaRecyclerAdapter: MultiMediaRecyclerAdapter


    private val mainViewModel: MainViewModel by viewModels()

    private lateinit var inflated: View


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        inflated = inflater.inflate(R.layout.fragment_media_recycler, container, false)
        return inflated
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        initializeRecyclerViewAdapter()
        makeRequest(true)
    }

    private fun initializeRecyclerViewAdapter(){
        val recyclerType = when(tag){
            "favoriteMovies", "favoriteSeries" -> MultiMediaRecyclerAdapter.Type.FAVORITES
            else -> MultiMediaRecyclerAdapter.Type.BROWSE
        }
        multiMediaRecyclerAdapter = MultiMediaRecyclerAdapter(recyclerType, this)
        multiMediaRecycler.adapter = multiMediaRecyclerAdapter
    }


    private fun makeRequest(firstRequest: Boolean){
        when(this.tag) {
            "favoriteMovies" -> makeFavoriteMoviesRequest()
            "popularMovies" -> makePopularMoviesRequest(firstRequest)
            "ratedMovies" -> makeRatedMoviesRequest(firstRequest)
            "favoriteSeries" -> makeFavoriteSeriesRequest()
            "popularSeries" -> makePopularSeriesRequest(firstRequest)
            "ratedSeries" -> makeRatedSeriesRequest(firstRequest)
        }
    }

    private fun makeFavoriteMoviesRequest(){
        mainViewModel.getFavoriteMovies().observe(viewLifecycleOwner, Observer {
            if(it.isNotEmpty()){
                showFavoritesSection()
                multiMediaRecyclerAdapter.overwriteList(it)
                hideShimmerEffect()
            }
            else{
                hideFavoritesSection()
            }
        })
    }

    private fun makePopularMoviesRequest(firstRequest: Boolean) {
        val popularMoviesLiveData = mainViewModel.getPopularMovies(firstRequest)
        createDataObserverIfNotExists(popularMoviesLiveData)
    }

    private fun makeRatedMoviesRequest(firstRequest: Boolean) {
        val ratedMoviesLiveData = mainViewModel.getRatedMovies(firstRequest)
        createDataObserverIfNotExists(ratedMoviesLiveData)
    }


    private fun makeFavoriteSeriesRequest(){
        mainViewModel.getFavoriteSeries().observe(viewLifecycleOwner, Observer {
            if(it.isNotEmpty()){
                showFavoritesSection()
                multiMediaRecyclerAdapter.overwriteList(it)
                hideShimmerEffect()
            }
            else{
                hideFavoritesSection()
            }
        })
    }

    private fun showFavoritesSection(){
        if(tag == "favoriteMovies"){
            parentFragment?.favoriteMoviesText?.visibility = View.VISIBLE
            parentFragment?.favoriteMoviesContainer?.visibility = View.VISIBLE
        }
        else{
            parentFragment?.favoriteSeriesText?.visibility = View.VISIBLE
            parentFragment?.favoriteSeriesContainer?.visibility = View.VISIBLE
        }
    }

    private fun hideFavoritesSection(){
        if(tag == "favoriteMovies"){
            parentFragment?.favoriteMoviesContainer?.visibility = View.GONE
            parentFragment?.favoriteMoviesText?.visibility = View.GONE
        }
        else{
            parentFragment?.favoriteSeriesContainer?.visibility = View.GONE
            parentFragment?.favoriteSeriesText?.visibility = View.GONE
        }
    }

    private fun makePopularSeriesRequest(firstRequest: Boolean) {
        val popularSeriesLiveData = mainViewModel.getPopularSeries(firstRequest)
        createDataObserverIfNotExists(popularSeriesLiveData)
    }

    private fun makeRatedSeriesRequest(firstRequest: Boolean) {
        val ratedSeriesLiveData = mainViewModel.getRatedSeries(firstRequest)
        createDataObserverIfNotExists(ratedSeriesLiveData)
    }

    private fun createDataObserverIfNotExists(liveData: LiveData<List<MultiMedia>>){
        if(!liveData.hasActiveObservers()){
            liveData.observe(viewLifecycleOwner, Observer {
                addNewPageItemsToRecyclerView(it)
                hideShimmerEffect()
            })
        }
    }

    private fun addNewPageItemsToRecyclerView(mediaList: List<MultiMedia>) {
        multiMediaRecyclerAdapter.appendToList(mediaList)
    }

    private fun hideShimmerEffect(){
        multiMediaShimmerContainer?.stopShimmer()
        multiMediaShimmerContainer?.visibility = View.GONE
    }

    override fun onEndOfMultiMediaPage(){
        getNextPageMovies()
    }

    private fun getNextPageMovies(){
            makeRequest(false)
    }

    override fun onItemClicked(multiMedia: MultiMedia) {
        val intent = Intent(activity, MultimediaDetailsActivity::class.java)
        intent.putExtra("media", multiMedia)
        startActivity(intent)
    }
}