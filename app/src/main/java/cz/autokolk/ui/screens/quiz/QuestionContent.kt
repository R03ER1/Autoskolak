package cz.autokolk.ui.screens.quiz

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import cz.autokolk.Question
import cz.autokolk.ui.components.buttons.AnswerButton
import cz.autokolk.ui.components.buttons.AnswerState
import cz.autokolk.ui.components.glass.GlassCard
import cz.autokolk.ui.theme.TextPrimary

@Composable
fun QuestionContent(
    question: Question,
    awaitingAdvance: Boolean,
    pendingAnswerKey: String?,
    isTest: Boolean,
    onPick: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val canChange = isTest || (!awaitingAdvance && pendingAnswerKey == null)
    fun stateFor(key: String): AnswerState {
        if (!isTest && !awaitingAdvance && pendingAnswerKey != null) {
            val p = normalizeAnswerKey(pendingAnswerKey)
            return when {
                key == p -> AnswerState.SELECTED
                else -> AnswerState.DEFAULT
            }
        }
        val sel = normalizeAnswerKey(question.userAnswer)
        if (isTest) {
            if (sel.isEmpty()) return AnswerState.DEFAULT
            return if (key == sel) AnswerState.SELECTED else AnswerState.DEFAULT
        }
        val correct = resolveCorrectKey(question)
        if (!awaitingAdvance) {
            return AnswerState.DEFAULT
        }
        return when {
            key == correct -> AnswerState.CORRECT
            key == sel && sel != correct -> AnswerState.WRONG
            else -> AnswerState.DEFAULT
        }
    }

    Column(modifier.padding(horizontal = 16.dp)) {
        GlassCard(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = question.questionText,
                color = TextPrimary,
                style = MaterialTheme.typography.titleMedium,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
            )
        }
        Spacer(Modifier.height(16.dp))
        AnswerButton(
            text = question.optionA,
            state = stateFor("a"),
            label = "A",
            onClick = { onPick("a") },
            modifier = Modifier.fillMaxWidth(),
            enabled = canChange,
        )
        Spacer(Modifier.height(10.dp))
        AnswerButton(
            text = question.optionB,
            state = stateFor("b"),
            label = "B",
            onClick = { onPick("b") },
            modifier = Modifier.fillMaxWidth(),
            enabled = canChange,
        )
        Spacer(Modifier.height(10.dp))
        AnswerButton(
            text = question.optionC,
            state = stateFor("c"),
            label = "C",
            onClick = { onPick("c") },
            modifier = Modifier.fillMaxWidth(),
            enabled = canChange,
        )
        Spacer(Modifier.height(80.dp))
    }
}
