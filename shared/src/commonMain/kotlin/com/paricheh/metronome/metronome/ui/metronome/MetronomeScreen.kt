package com.paricheh.metronome.metronome.ui.metronome

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Tune
import androidx.compose.material.icons.twotone.Settings
import androidx.compose.material.icons.twotone.Tune
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.paricheh.metronome.navigation.MetronomeScreens.Setting
import com.paricheh.metronome.designsystem.NonCommonTypography
import com.paricheh.metronome.core.titleEnglish
import com.paricheh.metronome.core.titlePersian
import com.paricheh.metronome.navigation.TunerScreens
import metronome.shared.generated.resources.Res
import metronome.shared.generated.resources.bpm
import metronome.shared.generated.resources.cd_settings
import metronome.shared.generated.resources.continue_text
import metronome.shared.generated.resources.horizontal_illustration_area
import metronome.shared.generated.resources.metroneome_pendulum_axis_layer
import metronome.shared.generated.resources.metronome_body_layer
import metronome.shared.generated.resources.metronome_sliding_weight
import metronome.shared.generated.resources.onboarding_start_desc
import metronome.shared.generated.resources.onboarding_start_title
import metronome.shared.generated.resources.onboarding_tempo_desc
import metronome.shared.generated.resources.onboarding_tempo_title
import metronome.shared.generated.resources.tuner_title
import metronome.shared.generated.resources.vertical_illustration_area
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MetronomeScreen(
    navController: NavController,
    viewModel: MetronomeViewModel = koinViewModel(),
) {
    val currentBpm by viewModel.currentTempoBpm.collectAsStateWithLifecycle()
    val pendulumRotationDegree by viewModel.pendulumAngle.collectAsStateWithLifecycle()
    val durationInMillisecond by viewModel.durationInMillisecond.collectAsStateWithLifecycle()
    val isMetronomeStarted by viewModel.isMetronomeStarted.collectAsStateWithLifecycle()
    val currentTempoMarkings by viewModel.currentTempoMarkings.collectAsStateWithLifecycle()
    val preferences by viewModel.metronomePreferences.collectAsStateWithLifecycle()
    var invalidClickOnScreenCount by remember { mutableStateOf(0) }

    LifecycleEventEffect(Lifecycle.Event.ON_PAUSE) {
        viewModel.stopMetronome()
    }

    DisposableEffect(Unit) {
        onDispose {
            viewModel.stopMetronome()
        }
    }

    var metronomeBodyHeight by remember { mutableStateOf(0.dp) }
    val density = LocalDensity.current
    // in between 20 till 240
    val bpmTransaction = updateTransition(currentBpm)
    val bpmPointerOffsetWeight by bpmTransaction.animateFloat {
        it / -100 * 0.225f
    }

    val angle by animateFloatAsState(
        targetValue = pendulumRotationDegree.coerceIn(-25f, 25f),
        animationSpec = tween(
            durationMillis = if (isMetronomeStarted) {
                durationInMillisecond.toInt()
            } else {
                200
            },
            easing = CubicBezierEasing(0.8f, 0.5f, 0.5f, 0.8f)
        )
    )

    if (preferences?.hasSeenUnboarding == false || invalidClickOnScreenCount >= 3) {
        Dialog(
            onDismissRequest = {},
            properties = DialogProperties(
                dismissOnBackPress = false,
                dismissOnClickOutside = false,
                usePlatformDefaultWidth = false
            ),
        ) {
            var isStartGuideVisible by remember { mutableStateOf(true) }

            Box(modifier = Modifier.fillMaxSize()) {
                val secondaryColor = MaterialTheme.colorScheme.secondaryContainer

                Box(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(24.dp)
                        .drawBehind {
                            drawCircle(
                                brush = Brush.radialGradient(
                                    listOf(secondaryColor, Color.Transparent)
                                )
                            )
                        }
                        .fillMaxWidth()
                        .height(metronomeBodyHeight)
                        .clip(shape = MaterialTheme.shapes.extraLarge)
                        .border(
                            width = 1.dp,
                            color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f),
                            shape = MaterialTheme.shapes.extraLarge
                        )
                        .background(
                            MaterialTheme.colorScheme.surfaceContainerLow.copy(alpha = 0.6f)
                        ),
                ) {
                    AnimatedContent(
                        transitionSpec = {
                            fadeIn() togetherWith fadeOut()
                        },
                        modifier = Modifier.align(Alignment.Center),
                        targetState = isStartGuideVisible
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                        ) {
                            Image(
                                modifier = Modifier.fillMaxWidth(),
                                painter = painterResource(
                                    if (it) {
                                        Res.drawable.horizontal_illustration_area
                                    } else {
                                        Res.drawable.vertical_illustration_area
                                    }
                                ),
                                contentDescription = null,
                            )

                            Spacer(modifier = Modifier.height(20.dp))

                            Text(
                                text = if (it) {
                                    stringResource(Res.string.onboarding_start_title)
                                } else {
                                    stringResource(Res.string.onboarding_tempo_title)
                                },
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.primary
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                modifier = Modifier.padding(horizontal = 24.dp),
                                text = if (it) {
                                    stringResource(Res.string.onboarding_start_desc)
                                } else {
                                    stringResource(Res.string.onboarding_tempo_desc)
                                },
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                textAlign = TextAlign.Center
                            )
                        }
                    }

                    Button(
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding(24.dp)
                            .fillMaxWidth(),
                        onClick = {
                            if (isStartGuideVisible) {
                                isStartGuideVisible = false
                            } else {
                                viewModel.markUnboardingAsSeen()
                                invalidClickOnScreenCount = 0
                            }
                        }
                    ) {
                        Text(stringResource(Res.string.continue_text))
                    }
                }
            }
        }
    }

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
        containerColor = Color.Black,
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
                        text = "مترونوم",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = {
                            navController.navigate(TunerScreens.Tuner)
                        }
                    ) {
                        //TODO Chnage to tuner icon
                        Icon(
                            imageVector = Icons.Rounded.Tune,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            contentDescription = "Tuner"
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = {
                            navController.navigate(Setting)
                        }
                    ) {
                        Icon(
                            imageVector = Icons.TwoTone.Settings,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            contentDescription = stringResource(Res.string.cd_settings)
                        )
                    }
                }
            )
        }
    ) { scaffoldPadding ->
        Box(
            modifier = Modifier
                .background(Color.Black)
                .padding(scaffoldPadding)
                .fillMaxSize()
                .clickable(
                    indication = null,
                    interactionSource = null
                ) {
                    viewModel.stopMetronome()
                    if (!isMetronomeStarted) {
                        invalidClickOnScreenCount++
                    }
                }
                .draggable(
                    state = rememberDraggableState {
                        val newBpm = currentBpm + it / 10
                        if (newBpm in 20f..240f) {
                            viewModel.setTempo(newBpm)
                        }
                    },
                    onDragStarted = {
                        viewModel.stopMetronome()
                    },
                    orientation = Orientation.Vertical
                )
        ) {

            Image(
                painter = painterResource(Res.drawable.metronome_body_layer),
                contentDescription = null,
                modifier = Modifier.fillMaxWidth()
                    .onGloballyPositioned {
                        with(density) {
                            metronomeBodyHeight = it.size.height.toDp()
                        }
                    }
                    .align(Alignment.BottomCenter)
            )

            AnimatedContent(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.TopCenter),
                targetState = currentTempoMarkings,
                transitionSpec = {
                    fadeIn() togetherWith fadeOut()
                }
            ) {
                Row(
                    modifier = Modifier
                        .padding(top = 16.dp)
                        .padding(horizontal = 24.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    val musicianTempoText = buildAnnotatedString {
                        append(stringResource(currentTempoMarkings.titleEnglish()))
                    }
                    Text(
                        text = stringResource(currentTempoMarkings.titlePersian()),
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        style = NonCommonTypography.PersianSonatiHeader
                    )

                    Text(
                        text = musicianTempoText,
                        color = MaterialTheme.colorScheme.onSurface,
                        style = NonCommonTypography.EnglishSontatiHeader,
                    )
                }
            }


            val tempoText = buildAnnotatedString {
                val currentTempoNumber = currentBpm.roundToInt()
                    .toString()

                preferences?.selectedTimeSignature?.let {
                    withStyle(
                        style = NonCommonTypography.musicFont.toSpanStyle()
                    ) {
                        append(it.getUnitNote().getUnitCharByUnit())
                        append("=")
                    }
                }

                withStyle(
                    style = NonCommonTypography.PersianSonatiNumber
                        .toSpanStyle()
                        .copy(
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                ) {
                    append(currentTempoNumber)
                }

                appendLine()
                append(stringResource(Res.string.bpm))
            }

            Text(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .offset(y = -metronomeBodyHeight * 0.805f),
                text = tempoText,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = NonCommonTypography.PersianSonatiLabelSmall,
            )

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .draggable(
                        state = rememberDraggableState { delta ->
                            viewModel.setPendulumAngle(pendulumRotationDegree + delta / 10)
                        },
                        onDragStarted = {
                            viewModel.stopMetronome()
                        },
                        onDragStopped = { velocity ->
                            viewModel.startMetronome(velocity)
                        },
                        orientation = Orientation.Horizontal
                    )
                    .graphicsLayer {
                        rotationZ = angle
                        transformOrigin = TransformOrigin(
                            0.5f,
                            0.9f
                        )
                    }
            ) {
                Image(
                    painter = painterResource(Res.drawable.metroneome_pendulum_axis_layer),
                    contentDescription = null,
                    modifier = Modifier.fillMaxWidth()
                        .height(metronomeBodyHeight * 0.75f)
                        .offset(y = -metronomeBodyHeight * 0.085f)
                        .align(Alignment.BottomCenter)
                )

                Image(
                    painter = painterResource(Res.drawable.metronome_sliding_weight),
                    contentDescription = null,
                    modifier = Modifier.fillMaxWidth()
                        .height(metronomeBodyHeight * 0.08f)
                        .offset(
                            y = (-metronomeBodyHeight * 0.75f) -
                                (metronomeBodyHeight * bpmPointerOffsetWeight)
                        )
                        .align(Alignment.BottomCenter)
                )
            }
        }
    }
}
