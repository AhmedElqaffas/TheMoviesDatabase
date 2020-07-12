package com.example.moviesretrofit.recyclersAdapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.moviesretrofit.R
import com.example.moviesretrofit.models.MultiMedia
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.item_media.view.*


class MultiMediaRecyclerAdapter(private val recyclerType: Int,
                                private val interactionListener: MultiMediaRecyclerInteraction
)
    : RecyclerView.Adapter<MultiMediaRecyclerAdapter.ViewHolder>() {

    object Type{
        const val BROWSE = 1
        const val SEARCH = 2
    }

    private var multiMediaList = mutableListOf<MultiMedia>()

    inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        private val poster = itemView.mediaImage

        fun bindMovieData(multiMedia: MultiMedia){
            setClickListener(multiMedia)
            setItemImage(multiMedia)
        }

        private fun setClickListener(multiMedia: MultiMedia){
            itemView.setOnClickListener{
                interactionListener.onItemClicked(multiMedia)
            }
        }

        private fun setItemImage(multiMedia: MultiMedia) {
            Picasso.get()
                .load("https://image.tmdb.org/t/p/w200${multiMedia.poster}")
                .fit()
                .placeholder(R.drawable.loading_movie_image)
                .into(poster)
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflated = determineInflatedLayout(parent)
        return ViewHolder(inflated)
    }

    private fun determineInflatedLayout(parent: ViewGroup): View {
        return when(recyclerType){
            Type.BROWSE ->
                LayoutInflater.from(parent.context).inflate(R.layout.item_media, parent, false)
            else ->
                LayoutInflater.from(parent.context).inflate(R.layout.item_media_search, parent, false)
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindMovieData(multiMediaList[position])
        if(reachedEndOfPage(position)){
            interactionListener.onEndOfMultiMediaPage()
        }
    }

    private fun reachedEndOfPage(position: Int): Boolean{
        return position == multiMediaList.lastIndex
    }

    override fun getItemCount(): Int {
        return multiMediaList.size
    }

    fun appendToList(extraList: List<MultiMedia>?){
        extraList?.let{multiMediaList.addAll(it)}
        notifyDataSetChanged()
    }

    fun overwriteList(newList: List<MultiMedia>?){
        multiMediaList.clear()
        newList?.let { multiMediaList = it as MutableList<MultiMedia>}
        notifyDataSetChanged()
    }

    interface MultiMediaRecyclerInteraction{
        fun onEndOfMultiMediaPage()
        fun onItemClicked(multiMedia: MultiMedia)
    }

}