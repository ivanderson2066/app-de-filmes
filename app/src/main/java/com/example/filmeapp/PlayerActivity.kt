package com.example.filmeapp

import android.net.Uri
import android.os.Bundle
import android.view.View
import android.content.res.Configuration
import android.os.Build
import android.view.WindowInsets
import android.view.WindowInsetsController
import android.widget.Toast
import androidx.annotation.OptIn
import androidx.appcompat.app.AppCompatActivity
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.ExoPlayer.Builder
import androidx.media3.ui.AspectRatioFrameLayout
import com.example.filmeapp.databinding.ActivityPlayerBinding

class PlayerActivity : AppCompatActivity() {

    private lateinit var player: ExoPlayer
    private lateinit var binding: ActivityPlayerBinding
    private var playbackPosition: Long = 0
    private var playWhenReady: Boolean = true
    private var videoUrl: String? = null

    @OptIn(UnstableApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Obtém a URL do vídeo a partir da intenção
        videoUrl = intent.getStringExtra("videoUrl") ?: return

        // Restaura o estado salvo se existir
        if (savedInstanceState != null) {
            playbackPosition = savedInstanceState.getLong("playbackPosition")
            playWhenReady = savedInstanceState.getBoolean("playWhenReady")
        }

        // Oculta a UI do sistema
        hideSystemUi()
        updateResizeMode()
    }

    // Inicializa o ExoPlayer no onStart para otimizar o ciclo de vida
    @OptIn(UnstableApi::class)
    override fun onStart() {
        super.onStart()

        if (videoUrl != null) {
            initializePlayer()
        }
    }

    // Método responsável por inicializar o ExoPlayer
    private fun initializePlayer() {
        player = Builder(this).build().apply {
            binding.playerView.player = this
            val mediaItem = MediaItem.fromUri(Uri.parse(videoUrl))
            setMediaItem(mediaItem)
            seekTo(playbackPosition)
            playWhenReady = this@PlayerActivity.playWhenReady
            prepare()
        }

        // Adiciona listener para monitorar eventos de playback
        player.addListener(object : Player.Listener {
            override fun onPlayerError(error: PlaybackException) {
                // Handle player error
                Toast.makeText(this@PlayerActivity, "Erro de reprodução: ${error.message}", Toast.LENGTH_SHORT).show()
            }

            override fun onPlaybackStateChanged(playbackState: Int) {
                when (playbackState) {
                    Player.STATE_ENDED -> {
                        // O vídeo terminou
                        Toast.makeText(this@PlayerActivity, "Vídeo terminado", Toast.LENGTH_SHORT).show()
                    }

                    Player.STATE_BUFFERING -> {
                        // O player está carregando
                        binding.progressBar.visibility = View.VISIBLE
                    }

                    Player.STATE_IDLE -> {
                        // O player está inativo
                        binding.progressBar.visibility = View.GONE
                    }

                    Player.STATE_READY -> {
                        // O player está pronto para reproduzir
                        binding.progressBar.visibility = View.GONE
                    }
                }
            }
        })
    }

    private fun hideSystemUi() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            // Para Android 11 (API level 30) e acima
            window.insetsController?.let { controller ->
                controller.hide(WindowInsets.Type.systemBars() or WindowInsets.Type.navigationBars() or WindowInsets.Type.statusBars())
                controller.systemBarsBehavior = WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            }
        } else {
            // Para versões anteriores ao Android 11
            val decorView = window.decorView
            decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    or View.SYSTEM_UI_FLAG_FULLSCREEN
                    or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)
        }
    }

    // Atualiza o resizeMode dependendo da configuração atual
    @OptIn(UnstableApi::class)
    private fun updateResizeMode() {
        val isPortrait = resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT
        binding.playerView.resizeMode = if (isPortrait) {
            AspectRatioFrameLayout.RESIZE_MODE_FIT
        } else {
            AspectRatioFrameLayout.RESIZE_MODE_FILL
        }
    }

    override fun onPause() {
        super.onPause()
        if (::player.isInitialized) {
            playbackPosition = player.currentPosition
            playWhenReady = player.playWhenReady
            player.playWhenReady = false
        }
    }

    override fun onResume() {
        super.onResume()
        if (::player.isInitialized) {
            player.seekTo(playbackPosition)
            player.playWhenReady = playWhenReady
            hideSystemUi()
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putLong("playbackPosition", playbackPosition)
        outState.putBoolean("playWhenReady", playWhenReady)
    }

    // Libera o player no onStop para liberar recursos
    override fun onStop() {
        super.onStop()
        releasePlayer()
    }

    private fun releasePlayer() {
        if (::player.isInitialized) {
            playbackPosition = player.currentPosition
            player.release()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        releasePlayer()
    }

    // Método para lidar com mudanças de configuração
    @OptIn(UnstableApi::class)
    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        hideSystemUi()
        updateResizeMode()
    }
}
