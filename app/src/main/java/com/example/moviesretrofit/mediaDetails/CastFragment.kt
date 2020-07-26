package com.example.moviesretrofit.mediaDetails

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.example.moviesretrofit.R
import com.example.moviesretrofit.dataClasses.MultiMedia
import com.example.moviesretrofit.dataClasses.Person
import com.example.moviesretrofit.recyclersAdapters.CastRecyclerAdapter
import kotlinx.android.synthetic.main.fragment_cast.*


class CastFragment : Fragment(){

    companion object {

        fun newInstance(multiMedia: MultiMedia) = CastFragment()
            .apply {
            arguments = Bundle().apply {
                putSerializable("media", multiMedia)
            }
        }
    }


    private lateinit var multiMedia: MultiMedia
    private val mediaDetailsViewModel: MediaDetailsViewModel by viewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_cast, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        multiMedia = arguments?.getSerializable("media") as MultiMedia
        makeCastRequest()
    }

    private fun makeCastRequest(){
        mediaDetailsViewModel.getMultimediaCredits(multiMedia)
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