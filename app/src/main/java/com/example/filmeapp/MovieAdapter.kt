package com.example.filmeapp

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.filmeapp.databinding.ItemMovieBinding
import com.squareup.picasso.Picasso

class MovieAdapter(private val onClick: (Movie) -> Unit) : RecyclerView.Adapter<MovieAdapter.MovieViewHolder>() {

    private var movies: List<Movie> = listOf()

    @SuppressLint("NotifyDataSetChanged")
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
            // Define o título do filme
            binding.movieTitle.text = movie.title

            // Verifica se o poster_path não é nulo ou vazio e carrega a imagem
            if (movie.poster_path.isNotEmpty()) {
                Picasso.get().load(movie.poster_path).into(binding.moviePoster)
            } else {
                // Carrega uma imagem padrão se o poster_path for nulo ou vazio
                Picasso.get().load(R.drawable.gato).into(binding.moviePoster)
            }

            // Define a descrição do filme
            binding.movieDescription.text = movie.description

            // Configurar o comportamento de clique no botão "Assistir"
            binding.watchButton.setOnClickListener {
                onClick(movie)  // Inicia a PlayerActivity
            }
        }
    }
}
