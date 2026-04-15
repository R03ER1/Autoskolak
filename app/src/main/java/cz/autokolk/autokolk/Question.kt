package cz.autokolk

data class Question(
    val id: String,
    val questionText: String,
    val optionA: String,
    val optionB: String,
    val optionC: String,
    val correctAnswer: String,
    val category: String? = null,
    val imagePath: String? = null,
    val videoPath: String? = null,
    /** Volitelné vysvětlení (např. z dat); jinak se použije fun fact. */
    val explanation: String? = null,
    /** Zajímavost z [DrivingFunFacts] nebo null. */
    val funFact: String? = null,
    var userAnswer: String? = null,
) 