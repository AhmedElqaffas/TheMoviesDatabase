package com.example.moviesretrofit.mediaDetails.infoDialogFragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.moviesretrofit.R
import com.example.moviesretrofit.dataClasses.Movie
import com.example.moviesretrofit.dataClasses.MultiMedia
import com.example.moviesretrofit.dataClasses.Series
import kotlinx.android.synthetic.main.movie_info_dialog.*
import kotlinx.android.synthetic.main.movie_info_dialog.yearTextView
import kotlinx.android.synthetic.main.series_info_dialog.*
import java.text.DecimalFormat

class InfoDialogFragment : androidx.fragment.app.DialogFragment() {

    companion object {
        fun newInstance(multimedia: MultiMedia) = InfoDialogFragment()
            .apply {
                arguments = Bundle().apply {
                    putSerializable("multimedia", multimedia)
                }
            }

        private lateinit var multimedia: MultiMedia
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        multimedia = arguments?.getSerializable("multimedia") as MultiMedia
        if(multimedia is Movie){
            return inflater.inflate(R.layout.movie_info_dialog, container, false)
        }
        else{
            return inflater.inflate(R.layout.series_info_dialog, container, false)
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        if(multimedia is Movie){
            yearTextView.text = multimedia.releaseDate.subSequence(0,4)
            budgetTextView.text = formatMoneyText((multimedia as Movie).budget?.toLong())
            revenueTextView.text = formatMoneyText((multimedia as Movie).revenue)
        }
        else{
            airTimeTextView.text = getYears()
            seasonsTextView.text = getNumberOfSeason()
        }

    }

    private fun formatMoneyText(money: Long?): String{
        return if(money == 0L){
            "-"
        }
        else{
            DecimalFormat().format(money) + "$"
        }
    }

    private fun getYears(): String{
        return "${extractYearFromString((multimedia as Series).releaseDate)}" + "-" + formatLastShowYear()
    }

    private fun formatLastShowYear():String{
        val lastAirYear = extractYearFromString((multimedia as Series).lastAirDate)
        return if((multimedia as Series).inProduction){
                "Now"
        }
        else{
            lastAirYear
        }
    }

    private fun extractYearFromString(date: String): String{
        return try{
            date.subSequence(0,4).toString()
        }catch(e: Exception){
            "?"
        }
    }

    private fun getNumberOfSeason(): String{
        val numberOfSeasons = (multimedia as Series).numberOfSeasons.toString()
        return if(numberOfSeasons == "0"){
            "-"
        }
        else{
            numberOfSeasons
        }
    }
}