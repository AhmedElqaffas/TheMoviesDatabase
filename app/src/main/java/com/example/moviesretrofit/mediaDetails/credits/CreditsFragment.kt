package com.example.moviesretrofit.mediaDetails.credits

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import com.example.moviesretrofit.R
import com.example.moviesretrofit.dataClasses.MultiMedia
import com.example.moviesretrofit.dataClasses.Person
import com.example.moviesretrofit.recyclersAdapters.CastRecyclerAdapter
import kotlinx.android.synthetic.main.fragment_cast.*


class CreditsFragment : Fragment(){

    companion object {

        fun newInstance(multiMedia: MultiMedia) = CreditsFragment()
            .apply {
            arguments = Bundle().apply {
                putSerializable("media", multiMedia)
            }
        }
    }

    private lateinit var creditsLiveData: LiveData<List<Person>>


    private lateinit var multiMedia: MultiMedia
    private val creditsViewModel: CreditsViewModel by viewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_cast, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        multiMedia = arguments?.getSerializable("media") as MultiMedia
        makeCastRequest()
    }

    /** When the user clicks on one of the items in "similar shows", we have to stop the observer
     * from updating this paused activity, or else, it'll update the cast of this activity with
     * the data of the other item credits
     */
    override fun onPause() {
        super.onPause()
        creditsLiveData.removeObservers(viewLifecycleOwner)
    }

    private fun makeCastRequest(){
        creditsLiveData = creditsViewModel.getMultimediaCredits(multiMedia)
        creditsLiveData.observe(viewLifecycleOwner, Observer{
            it?.let {
                setRecyclerAdapterList(it)
            }
        })
    }

    private fun setRecyclerAdapterList(cast: List<Person>){
        castRecycler?.adapter = CastRecyclerAdapter(cast)
    }
}