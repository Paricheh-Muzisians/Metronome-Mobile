package com.paricheh.metronome.tuner.ui.tuner

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateColor
import androidx.compose.animation.core.animateDp
import androidx.compose.animation.core.updateTransition
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowRight
import androidx.compose.material.icons.automirrored.twotone.ArrowBack
import androidx.compose.material.icons.rounded.Sensors
import androidx.compose.material.icons.rounded.SensorsOff
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.paricheh.metronome.tuner.model.TunerState
import com.paricheh.metronome.tuner.model.NoteInfo
import com.paricheh.metronome.tuner.ui.tuner.component.Guitar6StringInstrumentSection
import com.paricheh.metronome.tuner.ui.tuner.component.TunerSlider
import com.paricheh.metronome.tuner.ui.utils.instrument.Guitar6String
import com.paricheh.metronome.tuner.ui.utils.instrument.Instrument
import com.paricheh.metronome.tuner.ui.utils.instrument.Piano88
import com.paricheh.metronome.tuner.ui.utils.instrument.getTitle
import com.paricheh.metronome.tuner.ui.utils.instrument.getTypeText
import metronome.shared.generated.resources.Res
import metronome.shared.generated.resources.cd_back
import metronome.shared.generated.resources.tuner_title
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TunerScreen(
    navController: NavController,
    viewModel: TunerViewModel = koinViewModel(),
) {
    val tunerState by viewModel.tunerState.collectAsStateWithLifecycle()
    val selectedNote by viewModel.selectedNote.collectAsStateWithLifecycle()
    val selectedInstrument by viewModel.selectedInstrument.collectAsStateWithLifecycle()

    TunerScreenContent(
        state = tunerState,
        currentInstrument = selectedInstrument,
        selectedNote = selectedNote,
        onSelectNote = {
            viewModel.selectNote(it)
        },
        onBackClick = { navController.popBackStack() }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TunerScreenContent(
    state: TunerState,
    currentInstrument: Instrument?,
    selectedNote: NoteInfo?,
    onSelectNote: (NoteInfo?) -> Unit,
    onBackClick: () -> Unit,
) {
    val primaryColor = MaterialTheme.colorScheme.primary

    Scaffold(
        modifier = Modifier.drawWithContent {
            drawContent()
            drawCircle(
                brush = Brush.radialGradient(
                    listOf(
                        primaryColor.copy(alpha = 0.1f),
                        Color.Transparent,
                    ),
                    radius = 120.dp.toPx(),
                    center = Offset(
                        x = center.x,
                        y = 82.dp.toPx()
                    )
                ),
                radius = 120.dp.toPx(),
                center = Offset(
                    x = center.x,
                    y = 82.dp.toPx()
                )
            )
        },
        topBar = {
            CenterAlignedTopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Black
                ),
                title = {
                    Text(
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f))
                            .padding(
                                horizontal = 12.dp,
                                vertical = 4.dp
                            ),
                        text = stringResource(Res.string.tuner_title),
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.TwoTone.ArrowBack,
                            contentDescription = stringResource(Res.string.cd_back),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = {
                            if (selectedNote != null) {
                                onSelectNote(null)
                            } else {
                                onSelectNote(
                                    currentInstrument?.notes
                                        ?.firstOrNull()
                                )
                            }
                        }
                    ) {
                        AnimatedContent(selectedNote == null) {
                            if (it) {
                                Icon(
                                    imageVector = Icons.Rounded.Sensors,
                                    contentDescription = null
                                )
                            } else {
                                Icon(
                                    imageVector = Icons.Rounded.SensorsOff,
                                    contentDescription = null
                                )
                            }
                        }
                    }
                }
            )
        }
    ) { scaffoldPadding ->
        Box(modifier = Modifier.padding(scaffoldPadding)) {
            AnimatedContent(
                targetState = currentInstrument,
                transitionSpec = { fadeIn() togetherWith fadeOut() }
            ) { instrument ->
                if (instrument == null) {
                    CircularProgressIndicator(modifier = Modifier.fillMaxSize())
                } else {
                    TunerMainDisplay(
                        state = state,
                        currentInstrument = instrument,
                        selectedNote = selectedNote,
                        onSelectNote = onSelectNote,
                    )
                }
            }
        }
    }
}

@Composable
fun TunerMainDisplay(
    state: TunerState,
    currentInstrument: Instrument,
    selectedNote: NoteInfo?,
    onSelectNote: (NoteInfo?) -> Unit,
) {
    val detectResult = (state as? TunerState.Detected)?.result
    val centDiff by remember(detectResult?.centsDifference) {
        derivedStateOf {
            detectResult?.centsDifference?.coerceIn(
                minimumValue = -50f,
                maximumValue = 50f
            )
        }
    }

    val centDiffText by remember(centDiff) {
        derivedStateOf {
            centDiff?.let { (it * 10).roundToInt() / 10f }
        }
    }

    val frequencyText by remember(detectResult?.frequency) {
        derivedStateOf {
            detectResult?.frequency?.let { (it * 10).roundToInt() / 10f }
        }
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 20.dp)
                .fillMaxWidth()
        ) {
            TextButton(
                onClick = {},
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(32.dp)
                ) {
                    Column {
                        Text(
                            text = stringResource(currentInstrument.getTitle()),
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = stringResource(currentInstrument.getTypeText()),
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Icon(
                        imageVector = Icons.AutoMirrored.Rounded.ArrowRight,
                        contentDescription = null
                    )
                }
            }

            Spacer(Modifier.weight(1f))

            frequencyText?.let {
                Text(
                    text = "$it Hz",
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

        }

        Spacer(modifier = Modifier.height(68.dp))

        TunerSlider(
            centDifference = detectResult?.centsDifference?.coerceIn(
                minimumValue = -50f,
                maximumValue = 50f,
            ),
            title = {
                Text(
                    text = centDiffText?.toString().orEmpty(),
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                )
            },
            onTuned = {}
        )

        Spacer(modifier = Modifier.weight(1f))

        when (currentInstrument) {
            is Guitar6String -> {
                Guitar6StringInstrumentSection(
                    currentInstrument = currentInstrument,
                    selectedNote = selectedNote,
                    onSelectNote = onSelectNote
                )
            }

            is Piano88 -> {
                PianoInstrument(
                    currentInstrument = currentInstrument,
                    selectedNote = selectedNote,
                    onSelectNote = onSelectNote
                )
            }
        }
    }
}

@Composable
private fun PianoInstrument(
    currentInstrument: Instrument,
    selectedNote: NoteInfo?,
    onSelectNote: (NoteInfo?) -> Unit,
) {
    val surfaceColor = MaterialTheme.colorScheme.surface
    val lazyState = rememberLazyListState()

    LazyColumn(
        state = lazyState,
        modifier = Modifier
            .drawWithContent {
                drawContent()
                drawRect(
                    brush = Brush.verticalGradient(
                        listOf(
                            Color.Transparent,
                            Color.Transparent,
                            Color.Transparent,
                            Color.Transparent,
                            surfaceColor,
                        )
                    ),
                    size = Size(
                        height = size.height,
                        width = size.width
                    ),
                )
            }
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        stickyHeader {
            Column(
                modifier = Modifier.fillMaxWidth()
                    .background(
                        brush = Brush.verticalGradient(
                            listOf(
                                surfaceColor,
                                surfaceColor,
                                Color.Transparent,
                            )
                        ),
                    )
                    .padding(vertical = 24.dp)
            ) {
                HorizontalDivider()

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "انتخاب کلاویه",
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onSurface,
                    textAlign = TextAlign.Start
                )
            }
        }

        itemsIndexed(currentInstrument.notes) { index, note ->
            val isSelectedTransition = updateTransition(note == selectedNote)
            val backgroundColor by isSelectedTransition.animateColor {
                if (it) {
                    MaterialTheme.colorScheme.primaryContainer
                } else {
                    MaterialTheme.colorScheme.secondaryContainer
                }
            }

            val textColor by isSelectedTransition.animateColor {
                if (it) {
                    MaterialTheme.colorScheme.onPrimaryContainer
                } else {
                    MaterialTheme.colorScheme.onSecondaryContainer
                }
            }

            val verticalPadding by isSelectedTransition.animateDp {
                if (it) {
                    20.dp
                } else {
                    16.dp
                }
            }

            Row(
                modifier = Modifier
                    .padding(bottom = 16.dp)
                    .clip(MaterialTheme.shapes.large)
                    .background(backgroundColor)
                    .clickable {
                        onSelectNote(note)
                    }
                    .padding(vertical = verticalPadding)
                    .padding(horizontal = 16.dp)
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Text(
                    text = index.toString(),
                    style = MaterialTheme.typography.titleSmall,
                    color = textColor
                )

                Text(
                    text = note.note.displayName,
                    style = MaterialTheme.typography.titleSmall,
                    color = textColor
                )

                Text(
                    text = "${note.frequency.roundToInt()} Hz",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
