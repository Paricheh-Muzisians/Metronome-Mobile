package com.paricheh.metronome.tuner.ui.tuner

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowRight
import androidx.compose.material.icons.automirrored.twotone.ArrowBack
import androidx.compose.material.icons.rounded.Sensors
import androidx.compose.material.icons.rounded.SensorsOff
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.composeunstyled.SheetDetent
import com.composeunstyled.rememberModalBottomSheetState
import com.paricheh.metronome.core.analytics.LocalAnalyticsManager
import com.paricheh.metronome.core.platform.LocalPlatformActionHandler
import com.paricheh.metronome.tuner.model.NoteInfo
import com.paricheh.metronome.tuner.model.TunerState
import com.paricheh.metronome.tuner.ui.tuner.component.Guitar6StringInstrumentSection
import com.paricheh.metronome.tuner.ui.tuner.component.InstrumentSelectorBottomSheet
import com.paricheh.metronome.tuner.ui.tuner.component.PermissionDialog
import com.paricheh.metronome.tuner.ui.tuner.component.PianoInstrument
import com.paricheh.metronome.tuner.ui.tuner.component.SetarInstrumentSection
import com.paricheh.metronome.tuner.ui.tuner.component.TunerSlider
import com.paricheh.metronome.tuner.ui.tuner.component.goodThreshold
import com.paricheh.metronome.tuner.ui.utils.instrument.Guitar6String
import com.paricheh.metronome.tuner.ui.utils.instrument.Instrument
import com.paricheh.metronome.tuner.ui.utils.instrument.Piano88
import com.paricheh.metronome.tuner.ui.utils.instrument.Setar
import com.paricheh.metronome.tuner.ui.utils.instrument.getTitle
import com.paricheh.metronome.tuner.ui.utils.instrument.getTypeText
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import metronome.shared.generated.resources.Res
import metronome.shared.generated.resources.cd_back
import metronome.shared.generated.resources.tuner_badge_beta
import metronome.shared.generated.resources.tuner_loosen_string
import metronome.shared.generated.resources.tuner_tighten_string
import metronome.shared.generated.resources.tuner_title
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TunerScreen(
    navController: NavController,
    onHasBackgroundBlurChanged: (Boolean) -> Unit,
    viewModel: TunerViewModel = koinViewModel(),
) {
    val scope = rememberCoroutineScope()
    val platformActionHandler = LocalPlatformActionHandler.current

    val analyticsManager = LocalAnalyticsManager.current
    val tunerState by viewModel.tunerState.collectAsStateWithLifecycle()
    val selectedNote by viewModel.selectedNote.collectAsStateWithLifecycle()
    val selectedInstrument by viewModel.selectedInstrument.collectAsStateWithLifecycle()
    val allInstruments by viewModel.allInstruments.collectAsStateWithLifecycle()
    val showPermissionDialog by viewModel.showPermissionDialog.collectAsStateWithLifecycle()
    val permissionState = rememberAudioPermissionState(
        onPermissionResult = { isGranted ->
            viewModel.onPermissionResult(isGranted)
        }
    )

    DisposableEffect(Unit) {
        onDispose {
            viewModel.increaseRatingPoint()
        }
    }

    LifecycleEventEffect(Lifecycle.Event.ON_RESUME) {
        viewModel.checkPermission()
    }

    LaunchedEffect(Unit) {
        viewModel.shouldRequestPermission.collectLatest {
            permissionState.requestPermission()
        }
    }

    LaunchedEffect(showPermissionDialog) {
        onHasBackgroundBlurChanged(showPermissionDialog)
    }

    if (showPermissionDialog) {
        PermissionDialog(
            onOpenSettings = {
                analyticsManager.track(
                    "tuner_open_setting_because_of_permission"
                )
                platformActionHandler.openAppSettings()
                onHasBackgroundBlurChanged(false)
            },
            onBack = {
                navController.popBackStack()
                analyticsManager.track(
                    "tuner_back_because_of_permission"
                )
                onHasBackgroundBlurChanged(false)
            }
        )
    }

    val instrumentSelectorSheetState = rememberModalBottomSheetState(
        initialDetent = SheetDetent.Hidden
    )

    InstrumentSelectorBottomSheet(
        state = instrumentSelectorSheetState,
        instruments = allInstruments,
        currentInstrument = selectedInstrument,
        onDismiss = {
            scope.launch {
                instrumentSelectorSheetState.animateTo(SheetDetent.Hidden)
            }
        },
        onInstrumentSelected = {
            viewModel.selectInstrument(it)
            scope.launch {
                instrumentSelectorSheetState.animateTo(SheetDetent.Hidden)
            }
        }
    )

    TunerScreenContent(
        state = tunerState,
        currentInstrument = selectedInstrument,
        selectedNote = selectedNote,
        onSelectNote = {
            viewModel.selectNote(it)
        },
        onInstrumentPickRequest = {
            scope.launch {
                instrumentSelectorSheetState.animateTo(SheetDetent.FullyExpanded)
            }
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
    onInstrumentPickRequest: () -> Unit,
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
                    BadgedBox(
                        badge = {
                            Text(
                                text = stringResource(Res.string.tuner_badge_beta),
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.tertiary
                            )
                        }
                    ) {
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
                    }
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
                        AnimatedContent(
                            targetState = selectedNote == null,
                            transitionSpec = {
                                fadeIn() togetherWith fadeOut()
                            }
                        ) {
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
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                } else {
                    TunerMainDisplay(
                        state = state,
                        currentInstrument = instrument,
                        selectedNote = selectedNote,
                        onSelectNote = onSelectNote,
                        onInstrumentPickRequest = onInstrumentPickRequest
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
    onInstrumentPickRequest: () -> Unit,
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
            detectResult?.centsDifference?.roundToInt()?.toString()
        }
    }

    val hintTextRes by remember(centDiff) {
        derivedStateOf {
            val cent = centDiff
            if (cent == null || cent in -goodThreshold..goodThreshold) {
                null
            } else if (cent < 0) {
                Res.string.tuner_tighten_string
            } else if (cent > 0) {
                Res.string.tuner_loosen_string
            } else {
                null
            }
        }
    }
    val frequencyText by remember(detectResult?.noteInfo?.frequency) {
        derivedStateOf {
            detectResult?.noteInfo
                ?.frequency
                ?.let { (it * 10).roundToInt() / 10f }
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
                shape = MaterialTheme.shapes.medium,
                onClick = {
                    onInstrumentPickRequest()
                },
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
                    text = hintTextRes?.let { stringResource(it) }.orEmpty(),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                )
            },
            centDifferenceText = centDiffText.orEmpty(),
            onTuned = {

            }
        )

        Spacer(modifier = Modifier.weight(1f))

        AnimatedContent(
            targetState = currentInstrument,
            transitionSpec = { fadeIn() togetherWith fadeOut() }
        ) {
            when (it) {
                is Guitar6String -> {
                    Guitar6StringInstrumentSection(
                        currentInstrument = it,
                        selectedNote = selectedNote ?: detectResult?.noteInfo,
                        onSelectNote = onSelectNote
                    )
                }

                is Piano88 -> {
                    PianoInstrument(
                        currentInstrument = it,
                        selectedNote = selectedNote ?: detectResult?.noteInfo,
                        onSelectNote = onSelectNote
                    )
                }

                is Setar -> {
                    SetarInstrumentSection(
                        currentInstrument = it,
                        selectedNote = selectedNote ?: detectResult?.noteInfo,
                        onSelectNote = onSelectNote
                    )
                }
            }
        }
    }
}
