package com.example.filmeapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.filmeapp.databinding.ActivityMainBinding
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var db: FirebaseFirestore
    private lateinit var movieAdapter: MovieAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inicializando o ViewBinding
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Inicializando Firestore
        db = FirebaseFirestore.getInstance()

        // Configura o RecyclerView
        setupRecyclerView()

        // Busca filmes do Firestore
        fetchMoviesFromFirestore()
    }

    private fun setupRecyclerView() {
        binding.recyclerViewMovies.layoutManager = LinearLayoutManager(this)
    }

    private fun fetchMoviesFromFirestore() {
        lifecycleScope.launch {
            db.collection("movies")
                .get()
                .addOnSuccessListener { result ->
                    val movies = result.map { doc -> doc.toObject(Movie::class.java) }
                    setupAdapter(movies)
                }
                .addOnFailureListener { exception ->
                    Log.e("MainActivity", "Erro ao buscar filmes", exception)
                    // Mostra uma mensagem de erro para o usuário
                    binding.errorTextView.text = "Erro ao carregar filmes. Tente novamente."
                }
        }
    }

    private fun setupAdapter(movies: List<Movie>) {
        // Inicializa o adapter somente quando temos a lista de filmes
        movieAdapter = MovieAdapter { movie ->
            // Ao clicar no botão "Assistir", inicia a PlayerActivity
            val intent = Intent(this, PlayerActivity::class.java)
            intent.putExtra("videoUrl", movie.videoUrl)
            startActivity(intent)
        }

        // Envia a lista de filmes para o adapter
        movieAdapter.submitList(movies)
        binding.recyclerViewMovies.adapter = movieAdapter

        // Esconde o texto de erro se os filmes foram carregados com sucesso
        binding.errorTextView.text = ""
    }
}
