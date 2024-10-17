package com.example.filmeapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager // Importa o GridLayoutManager
import com.example.filmeapp.databinding.ActivityMainBinding
import com.google.firebase.firestore.FirebaseFirestore

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
        // Configura o GridLayoutManager com 2 colunas
        binding.recyclerViewMovies.layoutManager = GridLayoutManager(this, 2)
    }

    private fun fetchMoviesFromFirestore() {
        db.collection("movies")
            .get()
            .addOnSuccessListener { result ->
                val movies = result.map { doc -> doc.toObject(Movie::class.java) }
                setupAdapter(movies)
            }
            .addOnFailureListener { exception ->
                Log.e("MainActivity", "Erro ao buscar filmes", exception)
                binding.errorTextView.text = "Erro ao carregar filmes. Tente novamente."
            }
    }

    private fun setupAdapter(movies: List<Movie>) {
        // Configura o adapter e passa a lista de filmes
        movieAdapter = MovieAdapter { movie ->
            // Ao clicar no pôster, abre a DetailsActivity para mais informações
            val intent = Intent(this, MovieDetailsActivity::class.java)
            intent.putExtra("movie", movie) // Passa o objeto movie
            startActivity(intent)
        }

        // Envia a lista de filmes para o adapter
        movieAdapter.submitList(movies)
        binding.recyclerViewMovies.adapter = movieAdapter

        // Esconde o texto de erro se os filmes foram carregados com sucesso
        binding.errorTextView.text = ""
    }
}
