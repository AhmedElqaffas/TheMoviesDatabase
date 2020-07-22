package com.example.moviesretrofit.main.fragments

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import com.example.moviesretrofit.mediaDetails.MultiMediaDetailsActivity
import com.example.moviesretrofit.recyclersAdapters.MultiMediaRecyclerAdapter
import com.example.moviesretrofit.R
import com.example.moviesretrofit.main.FindMultiMediaViewModel
import com.example.moviesretrofit.dataClasses.MultiMedia
import com.example.moviesretrofit.dataClasses.MultiMediaRepositoryResponse
import kotlinx.android.synthetic.main.fragment_find_movie.*
class FindMultiMediaFragment : Fragment(),
    MultiMediaRecyclerAdapter.MultiMediaRecyclerInteraction {

    private var page = 1
    private var totalPages = 0

    private var multiMediaRecyclerAdapter =
        MultiMediaRecyclerAdapter(
            MultiMediaRecyclerAdapter.Type.SEARCH,
            this
        )

    private lateinit var inflated: View
    private var searchTextChanged = true

    private val findMediaViewModel: FindMultiMediaViewModel by viewModels()


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        inflated = inflater.inflate(R.layout.fragment_find_movie, container, false)
        return inflated
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        initializeRecyclerViewAdapter()
        makeMoviesRequest()
        setSearchBarChangeListener()
    }

    private fun initializeRecyclerViewAdapter(){
        multiMediaRecycler.adapter = multiMediaRecyclerAdapter
    }

    private fun makeMoviesRequest(){
        val foundMediaLiveData = findMediaViewModel.findMediaByName(page, searchBar.text.toString(), searchTextChanged)
        createDataObserverIfNotExists(foundMediaLiveData)
    }

    private fun createDataObserverIfNotExists(liveData: LiveData<MultiMediaRepositoryResponse>){
        if(!liveData.hasActiveObservers()){
            liveData.observe(viewLifecycleOwner, Observer {
                extractObservedItems(it)
            })
        }
    }

    private fun extractObservedItems(response: MultiMediaRepositoryResponse){
        val itemsList = removePeopleEntriesFromResponse(response.multimediaList)
        overwriteOrAppendToRecycler(itemsList)
        page = response.currentPage
        totalPages = response.totalPages
    }

    private fun removePeopleEntriesFromResponse(foundMediaList: List<MultiMedia>): MutableList<MultiMedia>{
        val entriesList = mutableListOf<MultiMedia>()
        for(entry in foundMediaList){
            if(entry.mediaType != "person"){
                entriesList.add(entry)
            }
        }
        return entriesList
    }

    private fun overwriteOrAppendToRecycler(mediaList: List<MultiMedia>){
        if(searchTextChanged) {
            multiMediaRecyclerAdapter.overwriteList(mediaList)
        }
        else{
            multiMediaRecyclerAdapter.appendToList(mediaList)
        }
    }

    private fun setSearchBarChangeListener(){
        searchBar.addTextChangedListener( object : TextWatcher{
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                returnRecyclerViewToTop()
                resetSearchData()
            }

        })
    }

    private fun returnRecyclerViewToTop(){
        multiMediaRecycler.scrollToPosition(0)
    }

    private fun resetSearchData(){
        searchTextChanged = true
        page = 1
        makeMoviesRequest()
    }

    private fun getNextPageMovies(){
        if(page < totalPages) {
            page++
            searchTextChanged = false
            makeMoviesRequest()
        }
    }

    override fun onEndOfMultiMediaPage() {
        getNextPageMovies()
    }


    override fun onItemClicked(multiMedia: MultiMedia) {
        val intent = Intent(activity, MultiMediaDetailsActivity::class.java)
        intent.putExtra("media", multiMedia)
        if(multiMedia.mediaType == "movie")
            intent.putExtra("Media Type",
                MultiMediaRecyclerFragment.MOVIE
            )
        else
            intent.putExtra("Media Type",
                MultiMediaRecyclerFragment.SERIES
            )
        startActivity(intent)
    }
}