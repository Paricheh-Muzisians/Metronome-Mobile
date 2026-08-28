package com.paricheh.metronome.tuner.ui.tuner

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowRight
import androidx.compose.material.icons.automirrored.twotone.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.paricheh.metronome.tuner.data.theory.Instrument
import com.paricheh.metronome.tuner.data.tuner.TunerResult
import com.paricheh.metronome.tuner.data.tuner.TunerState
import com.paricheh.metronome.tuner.ui.tuner.component.ValueHistorySlider
import kotlinx.coroutines.delay
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

    TunerScreenContent(
        state = tunerState,
        currentInstrument = viewModel.currentInstrument,
        onBackClick = { navController.popBackStack() }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TunerScreenContent(
    state: TunerState,
    currentInstrument: Instrument,
    onBackClick: () -> Unit,
) {
    Scaffold(
        containerColor = Color.Black,
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Black
                ),
                title = {
                    Text(
                        text = stringResource(Res.string.tuner_title),
                        style = MaterialTheme.typography.headlineSmall,
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
                    Row(
                        modifier = Modifier.padding(end = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {

                        Text(
                            text = "خودکار",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Switch(
                            checked = true,
                            onCheckedChange = null
                        )
                    }
                }
            )
        }
    ) { scaffoldPadding ->
        Box(modifier = Modifier.padding(scaffoldPadding)) {
            TunerMainDisplay(
                state = state,
                currentInstrument = currentInstrument
            )
        }
    }
}

@Composable
fun TunerMainDisplay(
    state: TunerState,
    currentInstrument: Instrument,
) {
    var smoothedDetectResult by remember { mutableStateOf<TunerResult?>(null) }
    val rawDetectedResult = (state as? TunerState.Detected)?.result

// 2. Use LaunchedEffect to filter the raw state changes
    LaunchedEffect(rawDetectedResult) {
        if (rawDetectedResult != null) {
            // CASE A: Signal appeared
            if (smoothedDetectResult == null) {
                // Wait to ensure it's not a 2ms noise spike.
                // If rawDetectedResult becomes null before this delay finishes,
                // the coroutine cancels and smoothedDetectResult stays null.
                delay(30)
            }

            // If it survived the delay (or was already non-null), update the UI instantly
            smoothedDetectResult = rawDetectedResult

        } else {
            // CASE B: Signal lost
            // Wait before clearing the UI to prevent flickering from brief tracking dropouts.
            // If a valid pitch comes back within 100ms, this cancels and keeps the old value.
            delay(100)
            smoothedDetectResult = null
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
                            text = "پیانو",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "۸۸ کلید",
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

            smoothedDetectResult?.frequency?.let {
                Text(
                    text = "${it.roundToInt()} Hz",
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }

        Spacer(modifier = Modifier.height(68.dp))

        ValueHistorySlider(
            currentValue = smoothedDetectResult?.centsDifference,
            min = -50f,
            max = 50f,
            goodThreshold = 10f,
            warningThreshold = 20f,
            minText = "-50",
            maxText = "50",
            title = smoothedDetectResult?.note?.displayName.orEmpty(),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(48.dp))
        Spacer(modifier = Modifier.weight(1f))

        val surfaceColor = MaterialTheme.colorScheme.surface
        LazyColumn(
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
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            currentInstrument.notes.forEachIndexed { index, info ->
                item {
                    Box(
                        modifier = Modifier
                            .clip(MaterialTheme.shapes.large)
                            .background(MaterialTheme.colorScheme.secondaryContainer)
                            .padding(16.dp)
                            .fillMaxWidth()
                    ) {
                        Text(
                            text = info.note.displayName,
                            style = MaterialTheme.typography.titleSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                if (info.octave != currentInstrument.notes[minOf(index + 1, 87)].octave) {
                    stickyHeader {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(MaterialTheme.colorScheme.surface),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = "اوکتاو ${currentInstrument.notes[index + 1].octave}",
                                style = MaterialTheme.typography.titleSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            HorizontalDivider(modifier = Modifier.fillMaxWidth())
                        }
                    }
                }
            }
        }
    }
}
