package cz.autokolk.ui.screens.practice

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import cz.autokolk.LessonProgress
import cz.autokolk.R
import cz.autokolk.ui.components.animation.AnimatedBackground
import cz.autokolk.ui.components.buttons.PrimaryGradientButton
import cz.autokolk.ui.components.glass.GlassCard
import cz.autokolk.ui.components.progress.AnimatedProgressBar
import cz.autokolk.ui.navigation.Route
import cz.autokolk.ui.screens.quiz.QuizMedia
import cz.autokolk.ui.theme.AccentCyan
import cz.autokolk.ui.theme.WarningAmber

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PracticeScreen(navController: NavHostController) {
    val vm: PracticeViewModel = viewModel()
    val categories by vm.categories.collectAsStateWithLifecycle()
    val mistakes by vm.mistakes.collectAsStateWithLifecycle()
    val filterMode by vm.filterMode.collectAsStateWithLifecycle()
    val expanded by vm.expanded.collectAsStateWithLifecycle()
    val searchQuery by vm.searchQuery.collectAsStateWithLifecycle()
    val searchHits by vm.searchHits.collectAsStateWithLifecycle()

    var selectedHit by remember { mutableStateOf<PracticeSearchHitUi?>(null) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    fun navigatePractice(
        categoryKey: String,
        subcategoryKey: String = Route.PracticeQuiz.ALL_SUB,
        focusQuestionId: String = Route.PracticeQuiz.FOCUS_NONE,
    ) {
        navController.navigate(
            Route.PracticeQuiz(
                categoryKey = categoryKey,
                practiceMode = filterMode,
                subcategoryKey = subcategoryKey,
                focusQuestionId = focusQuestionId,
            ).buildPath(),
        )
    }

    Box(Modifier.fillMaxSize()) {
        AnimatedBackground(modifier = Modifier.fillMaxSize()) {
            Column(
                Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
            ) {
                Text(
                    text = stringResource(R.string.practice_page_title),
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(bottom = 8.dp),
                )
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { vm.setSearchQuery(it) },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Hledat v otázkách…", color = MaterialTheme.colorScheme.onSurfaceVariant) },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant) },
                    singleLine = true,
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = MaterialTheme.colorScheme.onSurface,
                        unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                        focusedBorderColor = AccentCyan,
                        unfocusedBorderColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f),
                    ),
                )
                Spacer(Modifier.height(10.dp))
                FilterChipRow(
                    selected = filterMode,
                    onSelect = { vm.setFilter(it) },
                )
                Spacer(Modifier.height(8.dp))
                if (searchHits.isNotEmpty() && searchQuery.trim().length >= 2) {
                    Text("Výsledky hledání", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(Modifier.height(4.dp))
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.padding(bottom = 8.dp),
                    ) {
                        items(searchHits, key = { it.id }) { hit ->
                            GlassCard(
                                modifier = Modifier
                                    .width(200.dp)
                                    .clickable { selectedHit = hit },
                            ) {
                                Column(Modifier.padding(12.dp)) {
                                    Text(hit.categoryTitle, style = MaterialTheme.typography.labelSmall, color = AccentCyan)
                                    Spacer(Modifier.height(4.dp))
                                    Text(
                                        hit.preview,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurface,
                                        maxLines = 4,
                                        overflow = TextOverflow.Ellipsis,
                                    )
                                }
                            }
                        }
                    }
                }
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = Modifier.weight(1f),
                    contentPadding = PaddingValues(bottom = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    item(span = { GridItemSpan(2) }) {
                        MistakesOverviewCard(
                            mistakes = mistakes,
                            onOpen = { navigatePractice(LessonProgress.CATEGORY_USER_MISTAKES) },
                        )
                    }
                    items(categories, key = { it.code }) { cat ->
                        CategoryPracticeCard(
                            data = cat,
                            expanded = cat.code in expanded,
                            onToggleExpand = { vm.toggleExpanded(cat.code) },
                            onOpenCategory = { navigatePractice(cat.code) },
                            onOpenSub = { sub -> navigatePractice(cat.code, subcategoryKey = sub) },
                            onOpenWorst = { qid -> navigatePractice(cat.code, focusQuestionId = qid) },
                        )
                    }
                }
            }
        }

        selectedHit?.let { hit ->
            ModalBottomSheet(
                onDismissRequest = { selectedHit = null },
                sheetState = sheetState,
                containerColor = MaterialTheme.colorScheme.surface,
            ) {
                Column(
                    Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp)
                        .padding(bottom = 28.dp),
                ) {
                    Text(hit.categoryTitle, style = MaterialTheme.typography.titleSmall, color = AccentCyan)
                    Spacer(Modifier.height(8.dp))
                    Text(hit.preview, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurface)
                    if (hit.imagePath != null) {
                        Spacer(Modifier.height(12.dp))
                        QuizMedia(imagePath = hit.imagePath, videoPath = null)
                    }
                    Spacer(Modifier.height(20.dp))
                    PrimaryGradientButton(
                        text = "Procvičit",
                        onClick = {
                            navigatePractice(
                                categoryKey = hit.categoryCode,
                                subcategoryKey = Route.PracticeQuiz.ALL_SUB,
                                focusQuestionId = hit.id,
                            )
                            selectedHit = null
                        },
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
            }
        }
    }
}

@Composable
private fun FilterChipRow(selected: Int, onSelect: (Int) -> Unit) {
    val modeLabels = listOf(
        PracticeMode.ALL to "Všechny",
        PracticeMode.UNANSWERED to "Nenaučené",
        PracticeMode.WRONG to "Chybné",
        PracticeMode.CORRECT to "Správně",
    )
    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        items(modeLabels, key = { it.first }) { (mode, label) ->
            FilterChip(
                selected = selected == mode,
                onClick = { onSelect(mode) },
                label = { Text(label) },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = AccentCyan.copy(alpha = 0.35f),
                    selectedLabelColor = MaterialTheme.colorScheme.onSurface,
                    labelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                ),
            )
        }
    }
}

@Composable
private fun MistakesOverviewCard(
    mistakes: PracticeMistakesUi,
    onOpen: () -> Unit,
) {
    GlassCard(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onOpen),
        borderGradient = listOf(MaterialTheme.colorScheme.error.copy(alpha = 0.55f), MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f)),
    ) {
        Column(Modifier.padding(16.dp)) {
            Text("Tvoje chyby", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.error)
            Spacer(Modifier.height(8.dp))
            Text(
                "K procvičení: ${mistakes.wrongCount} · Opravené: ${mistakes.correctCount}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
private fun CategoryPracticeCard(
    data: PracticeCategoryUi,
    expanded: Boolean,
    onToggleExpand: () -> Unit,
    onOpenCategory: () -> Unit,
    onOpenSub: (String) -> Unit,
    onOpenWorst: (String) -> Unit,
) {
    val interaction = remember { MutableInteractionSource() }
    val pressed by interaction.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (pressed) 0.97f else 1f,
        animationSpec = tween(120),
        label = "cardPress",
    )
    val border = if (data.completed) {
        listOf(WarningAmber, WarningAmber.copy(alpha = 0.4f))
    } else {
        listOf(MaterialTheme.colorScheme.onSurface, MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f))
    }
    GlassCard(
        modifier = Modifier
            .aspectRatio(1f)
            .scale(scale)
            .clickable(interactionSource = interaction, indication = null, onClick = onOpenCategory),
        borderGradient = border,
    ) {
        Column(
            Modifier
                .fillMaxSize()
                .padding(14.dp),
            verticalArrangement = Arrangement.SpaceBetween,
        ) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.Top) {
                Image(
                    painter = painterResource(PracticeCategoryIcons.drawableResForCategory(data.code)),
                    contentDescription = null,
                    modifier = Modifier.size(40.dp),
                )
                if (data.completed) {
                    Icon(Icons.Default.CheckCircle, contentDescription = null, tint = WarningAmber, modifier = Modifier.size(22.dp))
                }
            }
            Text(
                data.title,
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
            Column {
                AnimatedProgressBar(
                    progress = data.progress.coerceIn(0f, 1f),
                    modifier = Modifier.fillMaxWidth(),
                    accent = AccentCyan,
                    height = 6.dp,
                )
                Spacer(Modifier.height(4.dp))
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        "${(data.progress * 100).toInt()}%",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    Text(
                        "${data.percentCorrect}% správně · ${data.attemptCount} odp.",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
            }
            Row(
                Modifier
                    .fillMaxWidth()
                    // Krok 159 (touch target audit): celý řádek je klikatelný a má min. výšku
                    // 48dp — dřív mělo aktivní plochu jen samotné ikonky (24dp).
                    .heightIn(min = 48.dp)
                    .clickable(onClick = onToggleExpand),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    if (expanded) "Skrýt" else "Podkategorie",
                    style = MaterialTheme.typography.labelMedium,
                    color = AccentCyan,
                    modifier = Modifier.weight(1f),
                )
                Icon(
                    if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                    contentDescription = null,
                    tint = AccentCyan,
                    modifier = Modifier.size(24.dp),
                )
            }
            if (expanded) {
                Column(
                    Modifier
                        .fillMaxWidth()
                        .height(120.dp)
                        .verticalScroll(rememberScrollState()),
                ) {
                    data.subcategories.forEach { sub ->
                        Row(
                            Modifier
                                .fillMaxWidth()
                                .clickable { onOpenSub(sub.code) }
                                .padding(vertical = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                        ) {
                            Text(sub.title, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurface, modifier = Modifier.weight(1f))
                            Text("${sub.questionCount}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                    if (data.worstQuestionIds.isNotEmpty()) {
                        Spacer(Modifier.height(6.dp))
                        Text("Nejhůř:", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        data.worstQuestionIds.forEach { id ->
                            Text(
                                "#$id",
                                style = MaterialTheme.typography.labelMedium,
                                color = AccentCyan,
                                modifier = Modifier
                                    .clickable { onOpenWorst(id) }
                                    .padding(vertical = 2.dp),
                            )
                        }
                    }
                }
            }
        }
    }
}
