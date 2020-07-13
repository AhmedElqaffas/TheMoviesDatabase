package com.example.moviesretrofit

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.item_cast.view.*
import kotlin.math.PI

class CastRecyclerAdapter(private val castList: List<Cast>):
    RecyclerView.Adapter<CastRecyclerAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){

        fun bindCastData(cast: Cast) {
            setImage(cast.image)
            setName(cast.name)
        }

        private fun setImage(path: String){
            Picasso.get()
                .load("https://image.tmdb.org/t/p/w200$path")
                .fit()
                .into(itemView.castImage)
        }

        private fun setName(name: String){
            itemView.castName.text = name
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CastRecyclerAdapter.ViewHolder {
        val inflater = LayoutInflater.from(parent.context).inflate(R.layout.item_cast, parent, false)
        return ViewHolder(inflater)
    }

    override fun onBindViewHolder(holder: CastRecyclerAdapter.ViewHolder, position: Int) {
        holder.bindCastData(castList[position])
    }

    override fun getItemCount(): Int {
        return castList.size
    }

    fun setList(castList: List<Cast>){

    }
}