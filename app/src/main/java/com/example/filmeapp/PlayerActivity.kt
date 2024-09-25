package com.example.filmeapp

import android.net.Uri
import android.os.Bundle
import android.widget.MediaController
import android.widget.VideoView
import androidx.appcompat.app.AppCompatActivity
import android.widget.Toast

class PlayerActivity : AppCompatActivity() {

    private lateinit var videoView: VideoView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_player)

        // Inicializa a VideoView do layout
        videoView = findViewById(R.id.videoView)

        // Obtém o URL do vídeo passado via Intent
        val videoUrl = intent.getStringExtra("videoUrl")

        if (videoUrl.isNullOrEmpty()) {
            Toast.makeText(this, "URL de vídeo inválido", Toast.LENGTH_SHORT).show()
            finish() // Fecha a atividade se a URL for inválida
            return
        }

        // Configura o URI do vídeo
        val uri = Uri.parse(videoUrl)

        // Configura o MediaController
        val mediaController = MediaController(this)
        mediaController.setAnchorView(videoView)
        videoView.setMediaController(mediaController)

        // Define o URI do vídeo e inicia a reprodução
        videoView.setVideoURI(uri)
        videoView.setOnPreparedListener {
            videoView.start()
        }

        // Tratamento de erros de reprodução
        videoView.setOnErrorListener { _, _, _ ->
            Toast.makeText(this, "Erro ao reproduzir o vídeo", Toast.LENGTH_SHORT).show()
            true // Retorna true para indicar que o erro foi tratado
        }
    }
}
