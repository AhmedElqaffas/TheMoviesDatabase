package com.example.moviesretrofit

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.item_movie.view.*


class MoviesRecyclerAdapter(private val moviesList: MutableList<Movie>,
                            private val interactionListener: MoviesRecyclerInteraction)
    : RecyclerView.Adapter<MoviesRecyclerAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        private val moviePoster = itemView.movieImage

        fun bindMovieData(movie: Movie){
            setClickListener(movie)
            setItemImage(movie)
        }

        private fun setClickListener(movie: Movie){
            itemView.setOnClickListener{
                interactionListener.onItemClicked(movie)
            }
        }

        private fun setItemImage(movie: Movie) {
            Picasso.get()
                .load("https://image.tmdb.org/t/p/w200${movie.poster}")
                .fit()
                .placeholder(R.drawable.loading_movie_image)
                .into(moviePoster)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflated = LayoutInflater.from(parent.context).inflate(R.layout.item_movie, parent, false)
        return ViewHolder(inflated)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindMovieData(moviesList[position])
        if(reachedEndOfPage(position)){
            interactionListener.onEndOfMoviesPage()
        }
    }

    private fun reachedEndOfPage(position: Int): Boolean{
        return position == moviesList.lastIndex
    }

    override fun getItemCount(): Int {
        return moviesList.size
    }

    fun appendToList(extraList: List<Movie>?){
        extraList?.let{moviesList.addAll(it)}
        notifyDataSetChanged()
    }

    interface MoviesRecyclerInteraction{
        fun onEndOfMoviesPage()
        fun onItemClicked(movie: Movie)
    }
}