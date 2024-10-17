package com.example.filmeapp

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.filmeapp.databinding.ActivityMovieDetailsBinding
import com.squareup.picasso.Picasso

class MovieDetailsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMovieDetailsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inicializa o ViewBinding
        binding = ActivityMovieDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Obtém o filme passado via Intent
        val movie = intent.getParcelableExtra<Movie>("movie")

        if (movie != null) {
            // Exibe os detalhes do filme
            binding.movieTitle.text = movie.title
            binding.movieDescription.text = movie.description
            binding.releaseDate.text = "Lançamento: ${movie.release_date}"

            // Carrega a imagem do pôster
            Picasso.get().load(movie.poster_path).into(binding.moviePoster)

            // Exibe os gêneros do filme
            binding.movieGenres.text = "Gêneros: ${movie.genres.joinToString(", ")}"

            // Configura o botão "Assistir" para iniciar a PlayerActivity
            binding.watchButton.setOnClickListener {
                val intent = Intent(this, PlayerActivity::class.java)
                intent.putExtra("videoUrl", movie.videoUrl)
                startActivity(intent)
            }
        }
    }
}
