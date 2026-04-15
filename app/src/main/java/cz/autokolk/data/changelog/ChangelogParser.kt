package cz.autokolk.data.changelog

data class ChangelogEntry(
    val version: String,
    val date: String?,
    val changes: List<String>,
)

/**
 * Jednoduchý parser [CHANGELOG.md] ve formátu `## [verze] - datum`.
 */
object ChangelogParser {

    fun parse(text: String): List<ChangelogEntry> {
        val lines = text.lines()
        val out = mutableListOf<ChangelogEntry>()
        var currentVersion: String? = null
        var currentDate: String? = null
        val currentBullets = mutableListOf<String>()

        fun flush() {
            val v = currentVersion ?: return
            if (currentBullets.isNotEmpty()) {
                out.add(ChangelogEntry(version = v, date = currentDate, changes = currentBullets.toList()))
            }
            currentBullets.clear()
        }

        val headerRe = Regex("^##\\s+\\[([^\\]]+)\\]\\s*(?:-\\s*(.+))?\\s*$")

        for (line in lines) {
            val m = headerRe.find(line.trim())
            if (m != null) {
                flush()
                currentVersion = m.groupValues[1].trim()
                currentDate = m.groupValues.getOrNull(2)?.trim()?.takeIf { it.isNotBlank() }
                continue
            }
            val t = line.trim()
            if (currentVersion != null && t.startsWith("-")) {
                currentBullets.add(t.removePrefix("-").trim())
            }
        }
        flush()
        return out
    }
}
