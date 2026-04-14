package cz.autokolk.ui.screens.quiz

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cz.autokolk.Question
import cz.autokolk.ui.components.buttons.AnswerButton
import cz.autokolk.ui.components.buttons.AnswerState
import cz.autokolk.ui.theme.TextPrimary

@Composable
fun QuestionContent(
    question: Question,
    awaitingAdvance: Boolean,
    isTest: Boolean,
    onPick: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val canChange = isTest || !awaitingAdvance
    fun stateFor(key: String): AnswerState {
        val sel = normalizeAnswerKey(question.userAnswer)
        val correct = resolveCorrectKey(question)
        if (isTest) {
            if (sel.isEmpty()) return AnswerState.DEFAULT
            return when {
                key == correct -> AnswerState.CORRECT
                key == sel && sel != correct -> AnswerState.WRONG
                else -> AnswerState.DEFAULT
            }
        }
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
        Text(
            text = question.questionText,
            color = TextPrimary,
            style = androidx.compose.material3.MaterialTheme.typography.titleMedium,
        )
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
    }
}
