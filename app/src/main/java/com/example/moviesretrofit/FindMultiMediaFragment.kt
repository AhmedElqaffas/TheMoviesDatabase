package com.example.moviesretrofit

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.moviesretrofit.networking.MultiMediaAPI
import com.example.moviesretrofit.networking.MultiMediaResponse
import com.example.moviesretrofit.networking.RetrofitClient
import kotlinx.android.synthetic.main.fragment_find_movie.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
class FindMultiMediaFragment : Fragment(),MultiMediaRecyclerAdapter.MultiMediaRecyclerInteraction {

    private val key = "097aa1909532e2d795f4f414cf4bc13f"
    private var page = 1
    private var totalPages = 0

    private lateinit var multiMediaAPI: MultiMediaAPI
    private var mediaList = mutableListOf<MultiMedia>()
    private var multiMediaRecyclerAdapter = MultiMediaRecyclerAdapter(mediaList,
        MultiMediaRecyclerAdapter.Type.SEARCH , this)

    private lateinit var inflated: View
    private var searchTextChanged = true

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        inflated = inflater.inflate(R.layout.fragment_find_movie, container, false)
        return inflated
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        initializeRecyclerViewAdapter()
        getInstanceOfRetrofitInterface()
        makeMoviesRequest()
        setSearchBarChangeListener()
    }

    private fun initializeRecyclerViewAdapter(){
        multiMediaRecycler.adapter = multiMediaRecyclerAdapter
    }

    /* We can't instantiate an interface, so we create a class implementing this interface and get
       an object from it
    */
    private fun getInstanceOfRetrofitInterface(){
        multiMediaAPI = RetrofitClient.getRetrofitClient().create(MultiMediaAPI::class.java)
    }

    override fun onEndOfMultiMediaPage() {
        getNextPageMovies()
    }

    private fun makeMoviesRequest(){
        multiMediaAPI.findMediaByName(key, page, searchBar.text.toString())
            .apply { enqueueCallback(this) }
    }

    private fun enqueueCallback(call: Call<MultiMediaResponse>) {
        call.enqueue(object: Callback<MultiMediaResponse> {

            override fun onResponse(call: Call<MultiMediaResponse>,
                                    response: Response<MultiMediaResponse>
            ) {
                if(searchTextChanged){
                    response.body()?.let{totalPages = it.totalPages}
                    page = 1
                    val itemsList = removePeopleEntriesFromResponse(response.body())
                    multiMediaRecyclerAdapter.overwriteList(itemsList)
                }
                else{
                    response.body()?.let{totalPages = it.totalPages}
                    val itemsList = removePeopleEntriesFromResponse(response.body())
                    multiMediaRecyclerAdapter.appendToList(itemsList)
                }
                multiMediaRecyclerAdapter.notifyDataSetChanged()
            }

            override fun onFailure(call: Call<MultiMediaResponse>, t: Throwable) {
                Log.e("Movies error", "Couldn't get movies list")
            }
        })
    }

    private fun setSearchBarChangeListener(){
        searchBar.addTextChangedListener( object : TextWatcher{
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                searchTextChanged = true
                makeMoviesRequest()
            }

        })
    }

    private fun removePeopleEntriesFromResponse(body: MultiMediaResponse?): MutableList<MultiMedia>{
        val entriesList = mutableListOf<MultiMedia>()
        body?.let {
            for(entry in body.results){
                if(entry.mediaType != "person"){
                    entriesList.add(entry)
                }
            }
        }
        return entriesList
    }

    private fun getNextPageMovies(){
        if(page < totalPages) {
            page++
            searchTextChanged = false
            makeMoviesRequest()
        }
    }

    override fun onItemClicked(multiMedia: MultiMedia) {
        val intent = Intent(activity, MovieDetailsActivity::class.java)
        intent.putExtra("media", multiMedia)
        if(multiMedia.mediaType == "movie")
            intent.putExtra("Media Type", MultiMediaRecyclerFragment.MOVIE )
        else
            intent.putExtra("Media Type", MultiMediaRecyclerFragment.SERIES )
        startActivity(intent)
    }
}