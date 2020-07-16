package com.example.moviesretrofit

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import kotlinx.android.synthetic.main.zoomed_image_alert_dialog.view.*


object ImageZooming {

    private lateinit var dialog: Dialog
    private lateinit var inflated: View

    fun zoomImage(clickedImage: ImageView, context: Context){
        initializeVariables(context)
        inflated.zoomedImage.setImageDrawable(clickedImage.drawable)
        customizeDialog()
        showDialog()
        hideImageOnClick()
    }

    private fun initializeVariables(context: Context){
        dialog = Dialog(context)
        inflated = LayoutInflater.from(context).inflate(R.layout.zoomed_image_alert_dialog,null)
    }

    private fun customizeDialog() {
        dialog.setContentView(inflated)
        dialog.setCanceledOnTouchOutside(true)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    }

    private fun showDialog(){
        dialog.show()
    }

    /**
     * Dismisses the zoomed image dialog when the image is clicked on
     */
    private fun hideImageOnClick(){
        inflated.zoomedImage.setOnClickListener{
            dialog.dismiss()
        }
    }
}