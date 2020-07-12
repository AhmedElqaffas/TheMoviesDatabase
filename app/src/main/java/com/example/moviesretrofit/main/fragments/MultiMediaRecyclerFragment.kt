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
import com.example.moviesretrofit.mediaDetails.MultiMediaDetailsActivity
import com.example.moviesretrofit.recyclersAdapters.MultiMediaRecyclerAdapter
import com.example.moviesretrofit.R
import com.example.moviesretrofit.main.MainViewModel
import com.example.moviesretrofit.models.MultiMedia
import com.example.moviesretrofit.models.MultiMediaRepositoryResponse
import kotlinx.android.synthetic.main.fragment_media_recycler.*

class MultiMediaRecyclerFragment : Fragment(),
    MultiMediaRecyclerAdapter.MultiMediaRecyclerInteraction{

    private var page = 1
    private var totalPages = 0

    private var multiMediaRecyclerAdapter =
        MultiMediaRecyclerAdapter(
            MultiMediaRecyclerAdapter.Type.BROWSE,
            this
        )

    private val mainViewModel: MainViewModel by viewModels()

    private lateinit var inflated: View

    companion object {

        const val MOVIE = 1
        const val SERIES = 2

        fun newInstance(getMoviesBasedOn: Int, mediaType: Int) = MultiMediaRecyclerFragment()
            .apply {
            arguments = Bundle().apply {
                putInt("Sort Type", getMoviesBasedOn)
                putInt("Media Type", mediaType)
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        inflated = inflater.inflate(R.layout.fragment_media_recycler, container, false)
        return inflated
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        initializeRecyclerViewAdapter()
        makeRequest()
    }

    private fun initializeRecyclerViewAdapter(){
        multiMediaRecycler.adapter = multiMediaRecyclerAdapter
    }


    private fun makeRequest(){
        if( arguments?.getInt("Sort Type") == 1){
            if(arguments?.getInt("Media Type") == MOVIE)
                makePopularMoviesRequest()
            else
                makePopularSeriesRequest()
        }
        else{
            if(arguments?.getInt("Media Type") == MOVIE)
               makeRatedMoviesRequest()
            else
                makeRatedSeriesRequest()
        }
    }

    private fun makePopularMoviesRequest() {
        val popularMoviesLiveData = mainViewModel.getPopularMovies(page)
        createDataObserverIfNotExists(popularMoviesLiveData)
    }

    private fun makeRatedMoviesRequest() {
        val ratedMoviesLiveData = mainViewModel.getRatedMovies(page)
        createDataObserverIfNotExists(ratedMoviesLiveData)
    }

    private fun makePopularSeriesRequest() {
        val popularSeriesLiveData = mainViewModel.getPopularSeries(page)
        createDataObserverIfNotExists(popularSeriesLiveData)
    }

    private fun makeRatedSeriesRequest() {
        val ratedSeriesLiveData = mainViewModel.getRatedSeries(page)
        createDataObserverIfNotExists(ratedSeriesLiveData)
    }

    private fun createDataObserverIfNotExists(liveData: LiveData<MultiMediaRepositoryResponse>){
        if(!liveData.hasActiveObservers()){
            liveData.observe(viewLifecycleOwner, Observer {
                extractObservedItems(it)
                hideShimmerEffect()
            })
        }
    }

    private fun extractObservedItems(response: MultiMediaRepositoryResponse){
        addNewPageItemsToRecyclerView(response.multimediaList.toMutableList())
        page = response.currentPage
        totalPages = response.totalPages
    }

    private fun addNewPageItemsToRecyclerView(mediaList: MutableList<MultiMedia>) {
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
        if(page < totalPages){
            page++
            makeRequest()
        }
    }

    override fun onItemClicked(multiMedia: MultiMedia) {
        val intent = Intent(activity, MultiMediaDetailsActivity::class.java)
        intent.putExtra("media", multiMedia)
        if(arguments?.getInt("Media Type") == MOVIE)
            intent.putExtra("Media Type",
                MOVIE
            )
        else
            intent.putExtra("Media Type",
                SERIES
            )
        startActivity(intent)
    }
}