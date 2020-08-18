package com.example.moviesretrofit.mediaDetails.similarShows

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.LiveData
import androidx.lifecycle.observe
import com.example.moviesretrofit.R
import com.example.moviesretrofit.dataClasses.MultiMedia
import com.example.moviesretrofit.mediaDetails.MultimediaDetailsActivity
import com.example.moviesretrofit.recyclersAdapters.MultiMediaRecyclerAdapter
import kotlinx.android.synthetic.main.fragment_similar_shows.multiMediaRecycler
import kotlinx.android.synthetic.main.fragment_similar_shows.multiMediaShimmerContainer

class SimilarShowsFragment : Fragment(), MultiMediaRecyclerAdapter.MultiMediaRecyclerInteraction {

    companion object {
        fun newInstance(multiMedia: MultiMedia) = SimilarShowsFragment()
            .apply {
                arguments = Bundle().apply {
                    putSerializable("media", multiMedia)
                }
            }
    }

    private lateinit var recyclerAdapter: MultiMediaRecyclerAdapter
    private lateinit var showsLiveData: LiveData<List<MultiMedia>>
    private lateinit var multimedia: MultiMedia
    private val similarShowsViewModel: SimilarShowsViewModel by viewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        return inflater.inflate(R.layout.fragment_similar_shows, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        multimedia = arguments?.getSerializable("media") as MultiMedia
        getSimilarShows()
    }

    /** When the user clicks on one of the items in "similar shows", we have to stop the observer
     * from updating this paused activity, or else, it'll update the similar shows of this activity with
     * the data of the other item similar shows
     */
    override fun onPause() {
        super.onPause()
        showsLiveData.removeObservers(viewLifecycleOwner)
    }

    private fun getSimilarShows(){
        showsLiveData = similarShowsViewModel.getSimilarShows(multimedia)
        showsLiveData.observe(viewLifecycleOwner){
            addItemsToRecycler(it)
            hideShimmerEffect()
        }
    }

    private fun addItemsToRecycler(multimediaList: List<MultiMedia>){
        recyclerAdapter = MultiMediaRecyclerAdapter(MultiMediaRecyclerAdapter.Type.BROWSE, this)
        multiMediaRecycler.adapter = recyclerAdapter
        recyclerAdapter.appendToList(multimediaList)
    }

    private fun hideShimmerEffect(){
        multiMediaShimmerContainer?.stopShimmer()
        multiMediaShimmerContainer?.visibility = View.GONE
    }


    override fun onItemClicked(multiMedia: MultiMedia) {
        val intent = Intent(activity, MultimediaDetailsActivity::class.java)
        intent.putExtra("media", multiMedia)
        startActivity(intent)
    }

    // Pagination not needed
    override fun onEndOfMultiMediaPage() {
    }

}