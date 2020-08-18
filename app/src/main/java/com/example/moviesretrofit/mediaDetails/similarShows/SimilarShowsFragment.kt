package com.example.moviesretrofit.mediaDetails.similarShows

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
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

    private lateinit var multimedia: MultiMedia
    private val similarShowsViewModel: SimilarShowsViewModel by viewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        return inflater.inflate(R.layout.fragment_similar_shows, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        multimedia = arguments?.getSerializable("media") as MultiMedia

        getSimilarShows()
    }

    private fun getSimilarShows(){
        similarShowsViewModel.getSimilarShows(multimedia).observe(viewLifecycleOwner){
            addItemsToRecycler(it)
            hideShimmerEffect()
        }
    }

    private fun addItemsToRecycler(multimediaList: List<MultiMedia>){
        val recyclerAdapter = MultiMediaRecyclerAdapter(MultiMediaRecyclerAdapter.Type.BROWSE, this)
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