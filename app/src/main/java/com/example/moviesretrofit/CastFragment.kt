package com.example.moviesretrofit

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.moviesretrofit.networking.MoviesAPI
import com.example.moviesretrofit.networking.RetrofitClient
import kotlinx.android.synthetic.main.fragment_cast.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class CastFragment : Fragment() {

    companion object {

        fun newInstance(movie: Movie) = CastFragment().apply {
            arguments = Bundle().apply {
                putSerializable("movie", movie)
            }
        }
    }

    private val key = "097aa1909532e2d795f4f414cf4bc13f"
    private lateinit var moviesAPI: MoviesAPI
    private lateinit var movie: Movie

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_cast, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        movie = arguments?.getSerializable("movie") as Movie
        getInstanceOfRetrofitInterface()
        makeCastRequest()
    }

    private fun getInstanceOfRetrofitInterface(){
        moviesAPI = RetrofitClient.getRetrofitClient().create(MoviesAPI::class.java)
    }

    private fun makeCastRequest(){
        moviesAPI.getMovieCast(movie.id, key)
            .apply {enqueueCallback(this)}
    }

    private fun enqueueCallback(call: Call<CastResponse>) {
        call.enqueue(object: Callback<CastResponse> {

            override fun onResponse(call: Call<CastResponse>, response: Response<CastResponse>) {
                response.body()?.let { setRecyclerAdapterList(it.cast) }
            }

            override fun onFailure(call: Call<CastResponse>, t: Throwable) {
                Log.i("Cast Fragment", "Retrofit Call Failed")
            }

        })
    }

    private fun setRecyclerAdapterList(cast: List<Cast>){
        val castRecyclerAdapter = CastRecyclerAdapter(cast)
        castRecycler?.adapter = castRecyclerAdapter
    }
}