package com.example.autokolk

import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment

class AlexPageOneFragment : Fragment() {
    private var handler: Handler? = null
    private val updateRunnable = object : Runnable {
        override fun run() {
            val view = view ?: return
            val manager = HungerManager(requireContext())
            val hunger = manager.getCurrentHunger()
            view.findViewById<ProgressBar>(R.id.healthProgress)?.apply {
                max = HungerManager.MAX_HUNGER
                progress = hunger
            }
            view.findViewById<TextView>(R.id.healthProgressLabel)?.text = "$hunger/${HungerManager.MAX_HUNGER}"
            view.findViewById<TextView>(R.id.hungerFreezeLabel)?.let { freeze ->
                val manager = HungerManager(requireContext())
                if (manager.isFrozenNow()) {
                    val now = System.currentTimeMillis()
                    val until = manager.getFreezeUntilEpochMillis()
                    val remaining = (until - now).coerceAtLeast(0L)
                    val hours = (remaining / 3_600_000L).toInt()
                    val minutes = ((remaining % 3_600_000L) / 60_000L).toInt()
                    freeze.text = "Hladovění začne za ${hours} hod a ${minutes} min"
                    freeze.visibility = View.VISIBLE
                } else {
                    freeze.text = ""
                    freeze.visibility = View.GONE
                }
            }
            
            // Update hunger message as title
            val prefs = requireContext().getSharedPreferences("lesson_progress", android.content.Context.MODE_PRIVATE)
            val lionName = prefs.getString("lion_name", "Alex") ?: "Alex"
            view.findViewById<TextView>(R.id.pageTitle)?.text = getHungerMessage(hunger, lionName)
            
            // Update Alex image based on hunger level
            updateAlexImage(view, hunger)

            // Update every minute to keep UI fresh; decay is hourly and applied lazily
            handler?.postDelayed(this, 60_000L)
        }
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_alex_page_one, container, false)

        // Set the lion name dynamically
        val prefs = requireContext().getSharedPreferences("lesson_progress", android.content.Context.MODE_PRIVATE)
        val lionName = prefs.getString("lion_name", "Alex") ?: "Alex"

        val manager = HungerManager(requireContext())
        val hunger = manager.getCurrentHunger()
        view.findViewById<ProgressBar>(R.id.healthProgress)?.apply {
            max = HungerManager.MAX_HUNGER
            progress = hunger
        }
        view.findViewById<TextView>(R.id.healthProgressLabel)?.text = "$hunger/${HungerManager.MAX_HUNGER}"
        view.findViewById<TextView>(R.id.hungerFreezeLabel)?.let { freeze ->
            val manager = HungerManager(requireContext())
            if (manager.isFrozenNow()) {
                val now = System.currentTimeMillis()
                val until = manager.getFreezeUntilEpochMillis()
                val remaining = (until - now).coerceAtLeast(0L)
                val hours = (remaining / 3_600_000L).toInt()
                val minutes = ((remaining % 3_600_000L) / 60_000L).toInt()
                freeze.text = "Hladovění začne za ${hours} hod a ${minutes} min"
                freeze.visibility = View.VISIBLE
            } else {
                freeze.text = ""
                freeze.visibility = View.GONE
            }
        }
        
        // Set hunger message as title
        view.findViewById<TextView>(R.id.pageTitle)?.text = getHungerMessage(hunger, lionName)
        
        // Load Alex image based on hunger level
        updateAlexImage(view, hunger)

        return view
    }

    override fun onResume() {
        super.onResume()
        parentFragmentManager.setFragmentResultListener("hunger_updated", this) { _, _ ->
            updateRunnable.run()
        }
        if (handler == null) handler = Handler(Looper.getMainLooper())
        handler?.post(updateRunnable)
    }

    override fun onPause() {
        super.onPause()
        handler?.removeCallbacksAndMessages(null)
        handler = null
    }
    
    private fun getHungerMessage(hunger: Int, lionName: String): String {
        val hungerLevel = (hunger / 10) * 10 // Round down to nearest 10
        return when (hungerLevel) {
            0 -> "$lionName je úplně vyhladovělý! 😰"
            10 -> "$lionName má obrovský hlad! 😢"
            20 -> "$lionName je velmi hladový! 😔"
            30 -> "$lionName má velký hlad! 😕"
            40 -> "$lionName má hlad! 😐"
            50 -> "$lionName je trochu hladový! 🤔"
            60 -> "$lionName je v pořádku! 😊"
            70 -> "$lionName se cítí dobře! 😄"
            80 -> "$lionName je spokojený! 😁"
            90 -> "$lionName je velmi spokojený! 😍"
            100 -> "$lionName se velmi dobře napapal! 🤤"
            else -> "$lionName je v pořádku! 😊"
        }
    }

    private fun getAlexImageName(hunger: Int): String {
        val base = when {
            hunger <= 20 -> "AlexHungry.png"
            hunger <= 40 -> "AlexSad.png"
            hunger <= 60 -> "Alex.png"
            hunger <= 80 -> "AlexHappy.png"
            else -> "AlexCool.png"
        }
        val progress = LessonProgress(requireContext())
        if (progress.isSunglassesEnabled()) {
            return "C$base"
        }
        return base
    }
    
    private fun updateAlexImage(container: View, hunger: Int) {
        try {
            val imageName = getAlexImageName(hunger)
            val input = requireContext().assets.open("images/alex/$imageName")
            val bmp = BitmapFactory.decodeStream(input)
            input.close()
            container.findViewById<ImageView>(R.id.alexImage)?.setImageBitmap(bmp)
        } catch (_: Throwable) { }
    }
}