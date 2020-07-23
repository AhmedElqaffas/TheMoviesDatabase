package com.example.moviesretrofit

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.viewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.example.moviesretrofit.dataClasses.MultiMedia
import com.example.moviesretrofit.dataClasses.Person
import com.example.moviesretrofit.mediaDetails.CastFragment
import com.example.moviesretrofit.mediaDetails.MediaDetailsViewModel
import com.example.moviesretrofit.recyclersAdapters.CastRecyclerAdapter
import kotlinx.android.synthetic.main.fragment_cast.*

class testActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test)

        makeCastRequest()
    }



    private val mediaDetailsViewModel: MediaDetailsViewModel by viewModels()


    private fun makeCastRequest(){
        mediaDetailsViewModel.getMultimediaCredits(17557, 1)
            .observe(this, Observer{
                it?.let {
                    setRecyclerAdapterList(mediaDetailsViewModel.appendCastAndCrewLists(it))
                }
            })
    }

    private fun setRecyclerAdapterList(cast: List<Person>){
        castRecycler?.adapter = CastRecyclerAdapter(cast)
    }
}