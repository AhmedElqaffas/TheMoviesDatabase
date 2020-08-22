package com.example.tmdb.main.fragments

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
import com.example.tmdb.mediaDetails.MultimediaDetailsActivity
import com.example.tmdb.recyclersAdapters.MultiMediaRecyclerAdapter
import com.example.tmdb.R
import com.example.tmdb.main.FindMultimediaViewModel
import com.example.tmdb.dataClasses.MultiMedia
import com.example.tmdb.login.LoginActivity
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.fragment_find_movie.*
class FindMultiMediaFragment : Fragment(),
    MultiMediaRecyclerAdapter.MultiMediaRecyclerInteraction {

    private var multiMediaRecyclerAdapter =
        MultiMediaRecyclerAdapter(
            MultiMediaRecyclerAdapter.Type.SEARCH,
            this
        )

    private var searchTextChanged = true

    private val findMediaViewModel: FindMultimediaViewModel by viewModels()


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_find_movie, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initializeRecyclerViewAdapter()
        setSearchBarChangeListener()
    }



    private fun initializeRecyclerViewAdapter(){
        multiMediaRecycler.adapter = multiMediaRecyclerAdapter
    }

    private fun makeRequest(){
        val foundMediaLiveData = findMediaViewModel.findMediaByName(searchBar.text.toString(), searchTextChanged)
        createDataObserverIfNotExists(foundMediaLiveData)
    }

    private fun createDataObserverIfNotExists(liveData: LiveData<List<MultiMedia>>){
        if(!liveData.hasActiveObservers()){
            liveData.observe(viewLifecycleOwner, Observer {
                overwriteOrAppendToRecycler(it)
            })
        }
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
        makeRequest()
    }

    private fun getNextPageMovies(){
        searchTextChanged = false
        makeRequest()
    }

    override fun onEndOfMultiMediaPage() {
        getNextPageMovies()
    }


    override fun onItemClicked(multiMedia: MultiMedia) {
        val intent = Intent(activity, MultimediaDetailsActivity::class.java)
        intent.putExtra("media", multiMedia)
        startActivity(intent)
    }
}