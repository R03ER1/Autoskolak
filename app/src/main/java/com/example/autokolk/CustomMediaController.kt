package com.example.autokolk

import android.content.Context
import android.widget.MediaController
import android.view.View
import android.os.Handler
import android.os.Looper
import android.widget.ImageButton
import android.view.ViewGroup
import android.widget.SeekBar
import android.view.LayoutInflater

class CustomMediaController(context: Context) : MediaController(context) {
    private val hideHandler = Handler(Looper.getMainLooper())
    private val hideRunnable = Runnable { super.hide() }

    override fun setMediaPlayer(player: MediaPlayerControl) {
        super.setMediaPlayer(player)
        // Initialize after media player is set
        post {
            removeUnwantedButtons()
        }
    }

    private fun removeUnwantedButtons() {
        try {
            val root = this
            // Hide all immediate children first
            for (i in 0 until root.childCount) {
                val child = root.getChildAt(i)
                // Keep only the main controls container visible
                if (child is ViewGroup && child.childCount > 0) {
                    // This is the main controls container
                    for (j in 0 until child.childCount) {
                        val control = child.getChildAt(j)
                        // Only keep SeekBar and play/pause button visible
                        when (control) {
                            is SeekBar -> control.visibility = View.VISIBLE
                            is ImageButton -> {
                                // Keep only the play/pause button
                                val desc = control.contentDescription?.toString()?.lowercase() ?: ""
                                if (desc.contains("pause") || desc.contains("play")) {
                                    control.visibility = View.VISIBLE
                                } else {
                                    control.visibility = View.GONE
                                }
                            }
                            else -> control.visibility = View.GONE
                        }
                    }
                } else {
                    child.visibility = View.GONE
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun show() {
        super.show()
        removeUnwantedButtons()
        hideHandler.removeCallbacks(hideRunnable)
        hideHandler.postDelayed(hideRunnable, 3000)
    }

    override fun hide() {
        super.hide()
        hideHandler.removeCallbacks(hideRunnable)
    }
} 