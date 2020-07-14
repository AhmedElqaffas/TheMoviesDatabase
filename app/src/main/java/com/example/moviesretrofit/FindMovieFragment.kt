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
import com.example.moviesretrofit.networking.MoviesAPI
import com.example.moviesretrofit.networking.PopularMoviesResponse
import com.example.moviesretrofit.networking.RetrofitClient
import kotlinx.android.synthetic.main.fragment_find_movie.*
import kotlinx.android.synthetic.main.fragment_movies_recycler.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class FindMovieFragment : Fragment(),MoviesRecyclerAdapter.MoviesRecyclerInteraction {

    private val key = "097aa1909532e2d795f4f414cf4bc13f"
    private var page = 1
    private var totalPages = 0

    private lateinit var moviesAPI: MoviesAPI
    private var moviesList = mutableListOf<Movie>()
    private var moviesRecyclerAdapter = MoviesRecyclerAdapter(moviesList, this)

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
        moviesRecycler.adapter = moviesRecyclerAdapter
    }

    /* We can't instantiate an interface, so we create a class implementing this interface and get
       an object from it
    */
    private fun getInstanceOfRetrofitInterface(){
        moviesAPI = RetrofitClient.getRetrofitClient().create(MoviesAPI::class.java)
    }

    override fun onEndOfMoviesPage() {
        getNextPageMovies()
    }

    private fun makeMoviesRequest(){
        moviesAPI.findMovieByName(key, page, searchBar.text.toString())
            .apply { enqueueCallback(this) }
    }

    private fun enqueueCallback(call: Call<PopularMoviesResponse>) {
        call.enqueue(object: Callback<PopularMoviesResponse> {

            override fun onResponse(call: Call<PopularMoviesResponse>,
                                    response: Response<PopularMoviesResponse>
            ) {
                if(searchTextChanged){
                    response.body()?.let{totalPages = it.totalPages}
                    page = 1
                    moviesRecyclerAdapter.overwriteList(response.body()?.results)
                }
                else{
                    response.body()?.let{totalPages = it.totalPages}
                    moviesRecyclerAdapter.appendToList(response.body()?.results)
                }
                moviesRecyclerAdapter.notifyDataSetChanged()
            }

            override fun onFailure(call: Call<PopularMoviesResponse>, t: Throwable) {
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

    private fun getNextPageMovies(){
        if(page < totalPages) {
            page++
            searchTextChanged = false
            makeMoviesRequest()
        }
    }

    override fun onItemClicked(movie: Movie) {
        val intent = Intent(activity, MovieDetailsActivity::class.java)
        intent.putExtra("movie", movie)
        startActivity(intent)
    }
}