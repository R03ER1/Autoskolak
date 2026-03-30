package cz.autokolk

import android.os.Bundle
import android.widget.TextView
import androidx.core.content.ContextCompat

class ChangelogActivity : AutokolkActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_changelog)

        window.statusBarColor = ContextCompat.getColor(this, R.color.black)

        val textView = findViewById<TextView>(R.id.changelogText)
        textView.text = loadChangelogFromAssets()
    }

    private fun loadChangelogFromAssets(): String {
        return try {
            assets.open("CHANGELOG.md").bufferedReader().use { it.readText() }
        } catch (_: Throwable) {
            "Changelog není k dispozici."
        }
    }
}


