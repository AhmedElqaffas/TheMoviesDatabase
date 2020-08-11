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

class MultiMediaRecyclerFragment : Fragment(),
    MultiMediaRecyclerAdapter.MultiMediaRecyclerInteraction{

    private var multiMediaRecyclerAdapter =
        MultiMediaRecyclerAdapter(
            MultiMediaRecyclerAdapter.Type.BROWSE,
            this
        )

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
        multiMediaRecycler.adapter = multiMediaRecyclerAdapter
    }


    private fun makeRequest(firstRequest: Boolean){
        when(this.tag) {
            "popularMovies" -> makePopularMoviesRequest(firstRequest)
            "ratedMovies" -> makeRatedMoviesRequest(firstRequest)
            "popularSeries" -> makePopularSeriesRequest(firstRequest)
            "ratedSeries" -> makeRatedSeriesRequest(firstRequest)
        }
    }

    private fun makePopularMoviesRequest(firstRequest: Boolean) {
        val popularMoviesLiveData = mainViewModel.getPopularMovies(firstRequest)
        createDataObserverIfNotExists(popularMoviesLiveData)
    }

    private fun makeRatedMoviesRequest(firstRequest: Boolean) {
        val ratedMoviesLiveData = mainViewModel.getRatedMovies(firstRequest)
        createDataObserverIfNotExists(ratedMoviesLiveData)
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