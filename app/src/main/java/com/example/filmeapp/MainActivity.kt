package com.example.filmeapp

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.filmeapp.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val movieList = listOf(
            Movie("Filme 1", "https://www.dropbox.com/scl/fi/zsl0uzcsflpbmy01aggi2/gato-de-botas-2.mp4?rlkey=ndp990nddnjc5zn0ks8s6ynu7&dl=1"),
            Movie("Filme 2", "https://www.dropbox.com/s/your_movie_link_2.mp4?dl=1")
        )

        binding.recyclerViewMovies.layoutManager = LinearLayoutManager(this)
        binding.recyclerViewMovies.adapter = MovieAdapter(movieList) { movie ->
            val intent = Intent(this, PlayerActivity::class.java).apply {
                putExtra("videoUrl", movie.videoUrl)
            }
            startActivity(intent)
        }
    }
}
