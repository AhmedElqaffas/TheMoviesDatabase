package com.example.moviesretrofit

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.moviesretrofit.datarepositories.*
import kotlinx.android.synthetic.main.fragment_media_recycler.*

class MultiMediaRecyclerFragment : Fragment(),
    MultiMediaRecyclerAdapter.MultiMediaRecyclerInteraction,
    MultiMediaRequestsListener {


    private var page = 1
    private var totalPages = 0

    private var multiMediaRecyclerAdapter = MultiMediaRecyclerAdapter(
        MultiMediaRecyclerAdapter.Type.BROWSE, this)

    private lateinit var inflated: View

    companion object {

        const val MOVIE = 1
        const val SERIES = 2

        fun newInstance(getMoviesBasedOn: Int, mediaType: Int) = MultiMediaRecyclerFragment().apply {
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
        PopularMoviesRepository.popularMoviesRequestsListener = this
        PopularMoviesRepository.makePopularMoviesRequest(page)
    }

    private fun makeRatedMoviesRequest() {
        RatedMoviesRepository.ratedMoviesRequestsListener = this
        RatedMoviesRepository.makeRatedMoviesRequest(page)
    }

    private fun makePopularSeriesRequest() {
        PopularSeriesRepository.popularSeriesRequestsListener = this
        PopularSeriesRepository.makePopularSeriesRequest(page)
    }

    private fun makeRatedSeriesRequest() {
        RatedSeriesRepository.ratedSeriesRequestsListener = this
        RatedSeriesRepository.makeRatedSeriesRequest(page)
    }

    private fun addNewPageItemsToRecyclerView(mediaList: MutableList<MultiMedia>) {
        multiMediaRecyclerAdapter.appendToList(mediaList)
    }

    private fun hideShimmerEffect(){
        multiMediaShimmerContainer?.stopShimmer()
        multiMediaShimmerContainer?.visibility = View.GONE
    }

    override fun onEndOfMultiMediaPage() {
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
            intent.putExtra("Media Type", MOVIE )
        else
            intent.putExtra("Media Type", SERIES )
        startActivity(intent)
    }

    override fun responseLoaded(mediaList: List<MultiMedia>, totalPages: Int) {
        this.totalPages = totalPages

        addNewPageItemsToRecyclerView(mediaList.toMutableList())
        hideShimmerEffect()
    }

    override fun updateCurrentPage(currentPage: Int){
        this.page = currentPage
    }
}