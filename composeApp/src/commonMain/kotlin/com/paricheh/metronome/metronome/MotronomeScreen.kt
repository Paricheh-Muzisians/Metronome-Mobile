package com.paricheh.metronome.metronome

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.twotone.Settings
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.paricheh.metronome.navigation.MetronomeScreens
import com.paricheh.metronome.navigation.MetronomeScreens.Setting
import com.paricheh.metronome.theme.headerFont
import com.paricheh.metronome.utils.titleEnglish
import com.paricheh.metronome.utils.titlePersian
import metronome.composeapp.generated.resources.Res
import metronome.composeapp.generated.resources.metroneome_pendulum_axis_layer
import metronome.composeapp.generated.resources.metronome_body_layer
import metronome.composeapp.generated.resources.metronome_sliding_weight
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

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

    Scaffold(
        containerColor = Color.Black,
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Black
                ),
                title = {},
                actions = {
                    IconButton(
                        onClick = {
                            navController.navigate(Setting)
                        }
                    ) {
                        Icon(
                            imageVector = Icons.TwoTone.Settings,
                            tint = Color.White,
                            contentDescription = "setting"
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
                Column(
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    //TODO use font family
                    Text(
                        text = stringResource(currentTempoMarkings.titlePersian()),
                        color = Color.White,
                        fontFamily = headerFont(),
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 57.sp,
                        lineHeight = 64.sp
                    )

                    Text(
                        text = stringResource(currentTempoMarkings.titleEnglish()),
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        style = MaterialTheme.typography.bodyMedium,
                    )
                }
            }

            Column(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .offset(y = -metronomeBodyHeight * 0.85f),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = currentBpm.toInt().toString(),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.ExtraBold
                )

                Text(
                    text = "Bpm",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.labelSmall,
                )

                val onMetronomeColor = MaterialTheme.colorScheme.onSurfaceVariant

                Canvas(modifier = Modifier.padding(top = 8.dp)) {
                    drawCircle(
                        color = onMetronomeColor,
                        radius = 8f
                    )
                }
            }

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
                            0.91f
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