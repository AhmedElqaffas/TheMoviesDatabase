package com.example.moviesretrofit.mediaDetails.infoDialogFragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.moviesretrofit.R
import com.example.moviesretrofit.dataClasses.MultiMedia
import kotlinx.android.synthetic.main.fragment_info_dialog.*
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
        return inflater.inflate(R.layout.fragment_info_dialog, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        multimedia = arguments?.getSerializable("multimedia") as MultiMedia

        yearTextView.text = multimedia.releaseDate.subSequence(0,4)
        budgetTextView.text = formatMoneyText(multimedia.budget)
    }

    private fun formatMoneyText(money: Int?): String{
        return if(money == 0){
            "-"
        }
        else{
            DecimalFormat().format(money) + "$"
        }
    }
}