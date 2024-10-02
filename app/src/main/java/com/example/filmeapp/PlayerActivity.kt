package com.example.filmeapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.filmeapp.databinding.ActivityPlayerBinding
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem

class PlayerActivity : AppCompatActivity() {

    private lateinit var player: ExoPlayer
    private lateinit var binding: ActivityPlayerBinding // Declaração do binding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inicializa o ViewBinding
        binding = ActivityPlayerBinding.inflate(layoutInflater) // Inicializando o binding
        setContentView(binding.root)

        val playerView = binding.playerView
        val videoUrl = intent.getStringExtra("videoUrl") ?: return

        player = ExoPlayer.Builder(this).build().also { exoPlayer ->
            playerView.player = exoPlayer  // playerView é vinculado ao layout activity_player.xml
            val mediaItem = MediaItem.fromUri(videoUrl)
            exoPlayer.setMediaItem(mediaItem)
            exoPlayer.prepare()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        player.release()
    }
}
