package com.example.moviesretrofit.mediaDetails

import android.app.Activity
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import com.example.moviesretrofit.R
import com.example.moviesretrofit.dataClasses.MultiMedia
import com.example.moviesretrofit.dataClasses.Person
import com.example.moviesretrofit.recyclersAdapters.CastRecyclerAdapter
import kotlinx.android.synthetic.main.fragment_cast.*


class CastFragment : Fragment(){

    companion object {

        const val MOVIE = 1
        const val SERIES = 2

        fun newInstance(multiMedia: MultiMedia, mediaType: Int) = CastFragment()
            .apply {
            arguments = Bundle().apply {
                putSerializable("media", multiMedia)
                putInt("media type", mediaType)
            }
        }
    }


    private lateinit var multiMedia: MultiMedia
    private var multiMediaType: Int? = 0
    private val mediaDetailsViewModel: MediaDetailsViewModel by viewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_cast, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        multiMedia = arguments?.getSerializable("media") as MultiMedia
        multiMediaType = arguments?.getInt("media type")
        makeCastRequest()
    }

    private fun makeCastRequest(){
        mediaDetailsViewModel.getMultimediaCredits(multiMedia.id, multiMediaType!!)
            .observe(viewLifecycleOwner, Observer{
                it?.let {
                    setRecyclerAdapterList(it)
                }
            })
    }

    private fun setRecyclerAdapterList(cast: List<Person>){
        castRecycler?.adapter = CastRecyclerAdapter(cast)
    }
}