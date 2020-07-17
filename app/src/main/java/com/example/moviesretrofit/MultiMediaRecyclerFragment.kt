package com.example.moviesretrofit

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.moviesretrofit.networking.MultiMediaAPI
import com.example.moviesretrofit.networking.MultiMediaResponse
import com.example.moviesretrofit.networking.RetrofitClient
import kotlinx.android.synthetic.main.fragment_media_recycler.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class MultiMediaRecyclerFragment : Fragment(),MultiMediaRecyclerAdapter.MultiMediaRecyclerInteraction  {


    private val key = "097aa1909532e2d795f4f414cf4bc13f"
    private var page = 1
    private var totalPages = 0

    private lateinit var multiMediaAPI: MultiMediaAPI
    private var multiMediaList = mutableListOf<MultiMedia>()
    private var multiMediaRecyclerAdapter = MultiMediaRecyclerAdapter(multiMediaList,
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
        getInstanceOfRetrofitInterface()
        makeMoviesRequest()
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

    private fun makeMoviesRequest(){
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
        multiMediaAPI.getPopularMovies(key, page)
            .apply { enqueueCallback(this) }
    }

    private fun makeRatedMoviesRequest() {
        multiMediaAPI.getHighRatedMovies(key, page)
            .apply { enqueueCallback(this) }
    }

    private fun makePopularSeriesRequest() {
        multiMediaAPI.getPopularSeries(key, page)
            .apply { enqueueCallback(this) }
    }

    private fun makeRatedSeriesRequest() {
        multiMediaAPI.getHighRatedSeries(key, page)
            .apply { enqueueCallback(this) }
    }

    private fun enqueueCallback(call: Call<MultiMediaResponse>) {
        call.enqueue(object: Callback<MultiMediaResponse> {

            override fun onResponse(call: Call<MultiMediaResponse>,
                                    response: Response<MultiMediaResponse>
            ) {

                response.body()?.let{totalPages = it.totalPages}
                addNewPageItemsToRecyclerView(response)
                hideShimmerEffect()
            }

            override fun onFailure(call: Call<MultiMediaResponse>, t: Throwable) {
                Log.e("Movies error", "Couldn't get movies list")
            }
        })
    }

    private fun addNewPageItemsToRecyclerView(response: Response<MultiMediaResponse>) {
        multiMediaRecyclerAdapter.appendToList(response.body()?.results)
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
            makeMoviesRequest()
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
}