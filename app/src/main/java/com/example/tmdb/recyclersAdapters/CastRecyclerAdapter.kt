package com.example.tmdb.recyclersAdapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.tmdb.R
import com.example.tmdb.dataClasses.Person
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.item_cast.view.*


class CastRecyclerAdapter(private val castCrewList: List<Person>):
    RecyclerView.Adapter<CastRecyclerAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){

        fun bindCastData(person: Person) {
            setImage(person.image)
            setName(person.name)
        }

        private fun setImage(path: String?){
            Picasso.get()
                .load("https://image.tmdb.org/t/p/w200$path")
                .fit()
                .placeholder(R.drawable.ic_baseline_person_24)
                .into(itemView.castImage)
        }

        private fun setName(name: String){
            itemView.castName.text = name
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context).inflate(R.layout.item_cast, parent, false)
        return ViewHolder(inflater)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindCastData(castCrewList[position])
    }

    override fun getItemCount(): Int {
        return castCrewList.size
    }
}