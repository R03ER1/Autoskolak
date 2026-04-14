package cz.autokolk.ui.screens.quiz

import cz.autokolk.Question

internal fun normalizeAnswerKey(key: String?): String {
    return key?.trim()?.lowercase()?.take(1) ?: ""
}

internal fun normalizeAnswerText(text: String?): String {
    val lower = text?.lowercase()?.trim() ?: ""
    val builder = StringBuilder(lower.length)
    for (ch in lower) {
        if (ch.isLetterOrDigit()) builder.append(ch)
    }
    return builder.toString()
}

internal fun resolveCorrectKey(question: Question): String {
    val rawNorm = normalizeAnswerText(question.correctAnswer)
    val aNorm = normalizeAnswerText(question.optionA)
    val bNorm = normalizeAnswerText(question.optionB)
    val cNorm = normalizeAnswerText(question.optionC)
    if (rawNorm.isNotEmpty()) {
        if (rawNorm == aNorm) return "a"
        if (rawNorm == bNorm) return "b"
        if (rawNorm == cNorm) return "c"
    }
    val rawKey = normalizeAnswerKey(question.correctAnswer)
    if (rawKey == "a" || rawKey == "b" || rawKey == "c") return rawKey
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
    return ""
}

internal fun correctAnswerIndex(question: Question): Int {
    return when (resolveCorrectKey(question)) {
        "a" -> 0
        "b" -> 1
        "c" -> 2
        else -> 0
    }
}
