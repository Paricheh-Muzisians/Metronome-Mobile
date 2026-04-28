package com.paricheh.metronome

import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import metronome.composeapp.generated.resources.Res
import metronome.composeapp.generated.resources.metroneome_pendulum_axis_layer
import metronome.composeapp.generated.resources.metronome_body_layer
import metronome.composeapp.generated.resources.metronome_sliding_weight
import org.jetbrains.compose.resources.painterResource
import kotlin.math.roundToInt

@Composable
@Preview
fun App() {
    MaterialTheme {
        var metronomeBodyHeight by remember { mutableStateOf(0.dp) }
        val density = LocalDensity.current
        // in between 20 till 240
        var selectedBpm by rememberSaveable { mutableStateOf(100f) }
        val bpmTransaction = updateTransition(selectedBpm)
        var pendulumRotationDegree by remember { mutableStateOf(0f) }
        var shouldStartMetronome by remember { mutableStateOf(false) }
        val bpmPointerOffsetWeight by bpmTransaction.animateFloat {
            it / -100 * 0.225f
        }

        val durationInMillisecond by rememberUpdatedState(60000 / selectedBpm)

        LaunchedEffect(shouldStartMetronome) {
            while (shouldStartMetronome) {
                pendulumRotationDegree = if (pendulumRotationDegree > 0) {
                    -25f
                } else {
                    25f
                }
                delay(durationInMillisecond.toLong())
            }
        }

        val angle by animateFloatAsState(
            targetValue = pendulumRotationDegree.coerceIn(-25f, 25f),
            animationSpec = tween(
                durationMillis = if (shouldStartMetronome) {
                    durationInMillisecond.toInt()
                } else {
                    200
                },
                easing = CubicBezierEasing(0.8f, 0.5f, 0.5f, 0.8f)
            )
        )

        Box(
            modifier = Modifier
                .background(Color.Black)
                .fillMaxSize()
                .clickable(
                    indication = null,
                    interactionSource = null
                ) {
                    shouldStartMetronome = false
                    pendulumRotationDegree = 0f
                }
                .draggable(
                    state = rememberDraggableState {
                        val newBpm = selectedBpm + it / 10
                        if (newBpm in 20f..240f) {
                            selectedBpm = newBpm
                        }
                    },
                    onDragStarted = {
                        shouldStartMetronome = false
                        pendulumRotationDegree = 0f
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

            Text(
                modifier = Modifier
                    .padding(top = 80.dp)
                    .align(Alignment.TopCenter),
                text = "${selectedBpm.roundToInt()} BPM",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.displaySmall,
            )

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .draggable(
                        state = rememberDraggableState { delta ->
                            pendulumRotationDegree += delta / 10
                        },
                        onDragStarted = {
                            shouldStartMetronome = false
                            pendulumRotationDegree = 0f
                        },
                        onDragStopped = { velocity ->
                            shouldStartMetronome = true
                            pendulumRotationDegree = velocity
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