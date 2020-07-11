package com.example.moviesretrofit

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.example.moviesretrofit.networking.MoviesAPI
import com.example.moviesretrofit.networking.PopularMoviesResponse
import com.example.moviesretrofit.networking.RetrofitClient
import kotlinx.android.synthetic.main.activity_main.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity(), MoviesRecyclerAdapter.MoviesRecyclerInteraction {

    private val key = "097aa1909532e2d795f4f414cf4bc13f"
    private var page = 1

    private lateinit var moviesAPI: MoviesAPI
    private var moviesList = mutableListOf<Movie>()
    private val moviesRecyclerAdapter = MoviesRecyclerAdapter(moviesList, this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

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
        moviesAPI.getPopularMovies(key, page)
            .enqueue(object: Callback<PopularMoviesResponse>{

                override fun onResponse(call: Call<PopularMoviesResponse>,
                                        response: Response<PopularMoviesResponse>) {

                        addNewPageItemsToRecyclerView(response)
                }

                override fun onFailure(call: Call<PopularMoviesResponse>, t: Throwable) {
                    Log.e("Movies error", "Couldn't get popular movies list")
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
        val intent = Intent(this, MovieDetailsActivity::class.java)
        intent.putExtra("movie", movie)
        startActivity(intent)
    }
}