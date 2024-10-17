package com.example.filmeapp

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.filmeapp.databinding.ItemMovieBinding
import com.squareup.picasso.Picasso

class MovieAdapter(private val onClick: (Movie) -> Unit) : RecyclerView.Adapter<MovieAdapter.MovieViewHolder>() {

    private var movies: List<Movie> = listOf()

    fun submitList(movieList: List<Movie>) {
        this.movies = movieList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieViewHolder {
        val binding = ItemMovieBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MovieViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MovieViewHolder, position: Int) {
        holder.bind(movies[position])
    }

    override fun getItemCount(): Int = movies.size

    inner class MovieViewHolder(private val binding: ItemMovieBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(movie: Movie) {
            // Exibe o nome do filme
            binding.movieTitle.text = movie.title

            // Carrega o pôster do filme
            Picasso.get().load(movie.poster_path).into(binding.moviePoster)

            // Configura o clique no pôster para abrir a tela de detalhes
            binding.moviePoster.setOnClickListener {
                onClick(movie)
            }
        }
    }
}
