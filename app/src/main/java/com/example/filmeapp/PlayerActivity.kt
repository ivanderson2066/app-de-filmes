package com.example.filmeapp

import android.net.Uri
import android.os.Bundle
import android.view.View
import android.content.res.Configuration
import androidx.annotation.OptIn
import androidx.appcompat.app.AppCompatActivity
import androidx.media3.common.MediaItem
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.AspectRatioFrameLayout
import com.example.filmeapp.databinding.ActivityPlayerBinding

class PlayerActivity : AppCompatActivity() {

    private lateinit var player: ExoPlayer
    private lateinit var binding: ActivityPlayerBinding
    private var playbackPosition: Long = 0
    private var playWhenReady: Boolean = true

    @OptIn(UnstableApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Obtém a URL do vídeo a partir da intenção
        val videoUrl = intent.getStringExtra("videoUrl") ?: return

        // Inicializa o ExoPlayer
        player = ExoPlayer.Builder(this).build().apply {
            binding.playerView.player = this // Vínculo do PlayerView
            val mediaItem = MediaItem.fromUri(Uri.parse(videoUrl)) // Criar MediaItem
            setMediaItem(mediaItem)
            seekTo(playbackPosition) // Retorna à posição anterior
            playWhenReady = this@PlayerActivity.playWhenReady // Define se deve reproduzir ao iniciar
            prepare()
        }

        // Restaura o estado salvo se existir
        if (savedInstanceState != null) {
            playbackPosition = savedInstanceState.getLong("playbackPosition")
            playWhenReady = savedInstanceState.getBoolean("playWhenReady")
        }

        // Oculta a UI do sistema e define o resizeMode inicial
        hideSystemUi()
        updateResizeMode()
    }

    // Oculta a UI do sistema para uma experiência de tela cheia
    private fun hideSystemUi() {
        binding.playerView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_FULLSCREEN
                or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)
    }

    // Atualiza o resizeMode dependendo da configuração atual
    @OptIn(UnstableApi::class)
    private fun updateResizeMode() {
        val isPortrait = resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT

        // Define o modo de redimensionamento baseado na orientação
        binding.playerView.resizeMode = if (isPortrait) {
            AspectRatioFrameLayout.RESIZE_MODE_FIT // Ajusta para caber na tela sem cortes
        } else {
            AspectRatioFrameLayout.RESIZE_MODE_FILL // Preenche a tela
        }
    }

    override fun onPause() {
        super.onPause()
        playbackPosition = player.currentPosition // Armazena a posição atual
        playWhenReady = player.playWhenReady // Armazena o estado de reprodução
        player.playWhenReady = false // Pausa o player
    }

    override fun onResume() {
        super.onResume()
        player.seekTo(playbackPosition) // Retorna à posição anterior
        player.playWhenReady = playWhenReady // Restaura o estado de reprodução
        hideSystemUi() // Oculta a UI do sistema novamente
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putLong("playbackPosition", player.currentPosition) // Salva a posição
        outState.putBoolean("playWhenReady", player.playWhenReady) // Salva o estado de reprodução
    }

    override fun onDestroy() {
        super.onDestroy()
        playbackPosition = player.currentPosition // Atualiza a posição antes de destruir
        player.release() // Libera os recursos do player
    }

    override fun onStop() {
        super.onStop()
        if (isFinishing) {
            player.release() // Libera o player se a atividade está sendo destruída
        }
    }

    // Método para lidar com mudanças de configuração, como rotação da tela
    @OptIn(UnstableApi::class)
    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        hideSystemUi() // Oculta a UI do sistema novamente ao mudar a configuração
        updateResizeMode() // Atualiza o resizeMode após a rotação
    }
}
