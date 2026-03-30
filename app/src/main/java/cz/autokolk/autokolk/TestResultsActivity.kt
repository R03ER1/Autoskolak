package cz.autokolk

import android.os.Bundle
import android.widget.TextView
import com.google.android.material.button.MaterialButton
import androidx.core.content.ContextCompat
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import android.widget.ScrollView
import com.google.android.material.bottomsheet.BottomSheetDialog

class TestResultsActivity : AutokolkActivity() {
    companion object {
        const val EXTRA_TOTAL_POINTS = "extra_total_points"
        const val EXTRA_MAX_POINTS = "extra_max_points"
        const val EXTRA_FIRST_OF_DAY = "extra_first_of_day"
        const val EXTRA_QUESTIONS_JSON = "extra_questions_json"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test_results)

        window.statusBarColor = ContextCompat.getColor(this, R.color.black)

        val total = intent.getIntExtra(EXTRA_TOTAL_POINTS, 0)
        val max = intent.getIntExtra(EXTRA_MAX_POINTS, 50)
        val firstOfDay = intent.getBooleanExtra(EXTRA_FIRST_OF_DAY, false)

        findViewById<TextView>(R.id.resultsTitle)?.text = "Výsledek testu"
        findViewById<TextView>(R.id.resultsPoints)?.text = "$total / $max"

        // Award points equal to the test score (⚡)
        try {
            if (total > 0) {
                LessonProgress(this).addPoints(total)
            }
        } catch (_: Throwable) { }

        // Persist this test attempt score for statistics
        try {
            LessonProgress(this).addTestScore(total, max)
        } catch (_: Throwable) { }

        // "Podrobnosti" button
        findViewById<MaterialButton>(R.id.detailsButton)?.setOnClickListener {
            showDetailsDialog()
        }

        // "Zavřít" clickable text
        findViewById<TextView>(R.id.resultsCloseText)?.setOnClickListener {
            // Show streak info if it's the first completion of the day
            if (firstOfDay) {
                try {
                    val streakIntent = android.content.Intent(this, StreakActivity::class.java)
                    streakIntent.putExtra(StreakActivity.EXTRA_FROM_TEST, true)
                    startActivity(streakIntent)
                    // Don't finish immediately, let the streak activity handle navigation
                    return@setOnClickListener
                } catch (_: Throwable) { }
            }
            
            // Return to TestAttemptActivity
            val intent = android.content.Intent(this, TestAttemptActivity::class.java)
                .addFlags(android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP or android.content.Intent.FLAG_ACTIVITY_SINGLE_TOP)
            startActivity(intent)
            finish()
        }
    }

    private fun showDetailsDialog() {
        val questionsJson = intent.getStringExtra(EXTRA_QUESTIONS_JSON)
        if (questionsJson.isNullOrEmpty()) return

        val gson = Gson()
        val type = object : TypeToken<List<Question>>() {}.type
        val questions: List<Question> = try {
            gson.fromJson(questionsJson, type) ?: emptyList()
        } catch (_: Exception) {
            emptyList()
        }

        if (questions.isEmpty()) return

        val dialog = BottomSheetDialog(this)
        val view = LayoutInflater.from(this).inflate(R.layout.dialog_test_details, null)
        dialog.setContentView(view)

        val scrollView = view.findViewById<ScrollView>(R.id.detailsScrollView)
        val container = view.findViewById<LinearLayout>(R.id.detailsContainer)

        questions.forEachIndexed { index, question ->
            val questionView = LayoutInflater.from(this).inflate(R.layout.item_test_detail, container, false)
            
            val questionNumber = questionView.findViewById<TextView>(R.id.questionNumber)
            val questionText = questionView.findViewById<TextView>(R.id.questionText)
            val userAnswer = questionView.findViewById<TextView>(R.id.userAnswer)
            val correctAnswer = questionView.findViewById<TextView>(R.id.correctAnswer)
            val isCorrect = questionView.findViewById<TextView>(R.id.isCorrect)

            questionNumber.text = "Otázka ${index + 1}:"
            questionText.text = question.questionText

            // Get user answer text
            val userAnswerKey = normalizeAnswerKey(question.userAnswer)
            val userAnswerText = when (userAnswerKey) {
                "a" -> question.optionA
                "b" -> question.optionB
                "c" -> question.optionC
                else -> "Nezodpovězeno"
            }
            userAnswer.text = "Vaše odpověď: $userAnswerText"

            // Get correct answer text
            val correctKey = resolveCorrectKey(question)
            val correctAnswerText = when (correctKey) {
                "a" -> question.optionA
                "b" -> question.optionB
                "c" -> question.optionC
                else -> question.correctAnswer
            }
            correctAnswer.text = "Správná odpověď: $correctAnswerText"

            // Show if correct or wrong
            val isCorrectAnswer = userAnswerKey == correctKey && userAnswerKey.isNotEmpty()
            if (isCorrectAnswer) {
                isCorrect.text = "✓ Správně"
                isCorrect.setTextColor(ContextCompat.getColor(this, android.R.color.holo_green_dark))
            } else {
                isCorrect.text = "✗ Špatně"
                isCorrect.setTextColor(ContextCompat.getColor(this, android.R.color.holo_red_dark))
            }

            container.addView(questionView)
        }

        view.findViewById<MaterialButton>(R.id.detailsClose)?.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun normalizeAnswerKey(key: String?): String {
        return key?.trim()?.lowercase()?.take(1) ?: ""
    }

    private fun resolveCorrectKey(question: Question): String {
        val rawNorm = normalizeAnswerText(question.correctAnswer)
        val aNorm = normalizeAnswerText(question.optionA)
        val bNorm = normalizeAnswerText(question.optionB)
        val cNorm = normalizeAnswerText(question.optionC)

        // 1) Prefer matching by normalized text content (handles punctuation and casing)
        if (rawNorm.isNotEmpty()) {
            if (rawNorm == aNorm) return "a"
            if (rawNorm == bNorm) return "b"
            if (rawNorm == cNorm) return "c"
        }

        // 2) If correctAnswer is exactly a key, accept it
        val rawKey = normalizeAnswerKey(question.correctAnswer)
        if (rawKey == "a" || rawKey == "b" || rawKey == "c") return rawKey

        // 3) Handle common yes/no synonyms mapping to whichever option matches
        val yesSynonyms = setOf("yes", "ano", "y", "true")
        val noSynonyms = setOf("no", "ne", "n", "false")
        if (rawNorm in yesSynonyms) {
            if (aNorm in yesSynonyms) return "a"
            if (bNorm in yesSynonyms) return "b"
            if (cNorm in yesSynonyms) return "c"
        }
        if (rawNorm in noSynonyms) {
            if (aNorm in noSynonyms) return "a"
            if (bNorm in noSynonyms) return "b"
            if (cNorm in noSynonyms) return "c"
        }

        // Unknown; return empty string
        return ""
    }

    private fun normalizeAnswerText(text: String?): String {
        val lower = text?.lowercase()?.trim() ?: ""
        // Remove non-alphanumeric letters in a Unicode-safe way without regex
        val builder = StringBuilder(lower.length)
        for (ch in lower) {
            if (ch.isLetterOrDigit()) builder.append(ch)
        }
        return builder.toString()
    }
}


