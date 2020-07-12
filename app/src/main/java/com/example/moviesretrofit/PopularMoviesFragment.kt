package com.example.moviesretrofit

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.moviesretrofit.networking.MoviesAPI
import com.example.moviesretrofit.networking.PopularMoviesResponse
import com.example.moviesretrofit.networking.RetrofitClient
import kotlinx.android.synthetic.main.fragment_movies_recycler.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PopularMoviesFragment : Fragment(),MoviesRecyclerAdapter.MoviesRecyclerInteraction  {

    private val key = "097aa1909532e2d795f4f414cf4bc13f"
    private var page = 1

    private lateinit var moviesAPI: MoviesAPI
    private var moviesList = mutableListOf<Movie>()
    private val moviesRecyclerAdapter = MoviesRecyclerAdapter(moviesList, this)

    private lateinit var inflated: View

    companion object {

        fun newInstance(getMoviesBasedOn: Int) = PopularMoviesFragment().apply {
            arguments = Bundle().apply {
                putInt("List Type", getMoviesBasedOn)
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        inflated = inflater.inflate(R.layout.fragment_movies_recycler, container, false)
        return inflated
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        Log.i("Fragment", "Recreated")
        initializeRecyclerViewAdapter()
        getInstanceOfRetrofitInterface()
        makeMoviesRequest()
    }

    private fun initializeRecyclerViewAdapter(){
        moviesRecycler.adapter = moviesRecyclerAdapter
    }

    /* We can't instantiate an interface, so we create a class implementing this interface and get
   an object from it
    */
    private fun getInstanceOfRetrofitInterface(){
        moviesAPI = RetrofitClient.getRetrofitClient().create(MoviesAPI::class.java)
    }

    private fun makeMoviesRequest(){
        if( arguments?.getInt("List Type") == 1){
            makePopularMoviesRequest()
        }
        else{
            makeRatedMoviesRequest()
        }
    }

    private fun makePopularMoviesRequest() {
        moviesAPI.getPopularMovies(key, page)
            .apply { enqueueCallback(this) }
    }

    private fun makeRatedMoviesRequest() {
        moviesAPI.getHighRatedMovies(key, page)
            .apply { enqueueCallback(this) }
    }

    private fun enqueueCallback(call: Call<PopularMoviesResponse>) {
        call.enqueue(object: Callback<PopularMoviesResponse> {

            override fun onResponse(call: Call<PopularMoviesResponse>,
                                    response: Response<PopularMoviesResponse>
            ) {

                addNewPageItemsToRecyclerView(response)
            }

            override fun onFailure(call: Call<PopularMoviesResponse>, t: Throwable) {
                Log.e("Movies error", "Couldn't get movies list")
            }
        })
    }

    private fun addNewPageItemsToRecyclerView(response: Response<PopularMoviesResponse>) {
        moviesRecyclerAdapter.appendToList(response.body()?.results)
    }

    override fun onEndOfMoviesPage() {
        getNextPageMovies()
    }

    private fun getNextPageMovies(){
        page++
        makeMoviesRequest()
    }

    override fun onItemClicked(movie: Movie) {
        val intent = Intent(activity, MovieDetailsActivity::class.java)
        intent.putExtra("movie", movie)
        startActivity(intent)
    }
}