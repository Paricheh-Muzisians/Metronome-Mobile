package com.paricheh.metronome

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.lerp
import metronome.composeapp.generated.resources.Res
import metronome.composeapp.generated.resources.metroneome_pendulum_axis_layer
import metronome.composeapp.generated.resources.metronome_body_layer
import metronome.composeapp.generated.resources.metronome_sliding_weight
import org.jetbrains.compose.resources.painterResource
import kotlin.math.PI
import kotlin.math.roundToInt
import kotlin.math.sin

@Composable
@Preview
fun App() {
    MaterialTheme {
        var metronomeBodyHeight by remember { mutableStateOf(0.dp) }
        val density = LocalDensity.current
        // in between 20 till 240
        var selectedBpm by rememberSaveable { mutableStateOf(100f) }
        val bpmTransaction = updateTransition(selectedBpm)

        val bpmPointerOffsetWeight by bpmTransaction.animateFloat {
            it / -100 * 0.225f
        }

        Box(
            modifier = Modifier
                .background(Color.Black)
                .fillMaxSize()
                .draggable(
                    state = rememberDraggableState {
                        val newBpm = selectedBpm - it/10
                        if (newBpm in 20f..240f) {
                            selectedBpm = newBpm
                        }
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
                        y = (-metronomeBodyHeight * 0.2f) -
                            (-metronomeBodyHeight * bpmPointerOffsetWeight)
                    )
                    .align(Alignment.BottomCenter)
            )
        }


    }
}

@Composable
fun MetronomeCanvas(
    bpm: Float = 120f,
    isPlaying: Boolean = true,
    modifier: Modifier = Modifier,
) {
    val infiniteTransition = rememberInfiniteTransition()

    val phase by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = (2 * PI).toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = (60000 / bpm).toInt(),
                easing = LinearEasing
            )
        )
    )

    val angle = 25f * sin(phase)

    val density = LocalDensity.current

    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height

        val centerX = w / 2f
        val topY = h * 0.1f
        val bottomY = h * 0.9f

        // =========================
        // 🎨 BODY (background)
        // =========================
        drawRoundRect(
            brush = Brush.verticalGradient(
                colors = listOf(
                    Color(0xFF0F1115),
                    Color(0xFF07080A)
                )
            ),
            cornerRadius = CornerRadius(40f, 40f)
        )

        // =========================
        // 📏 SCALE
        // =========================
        val scaleTop = topY + 40f
        val scaleBottom = bottomY - 80f
        val scaleHeight = scaleBottom - scaleTop

        val steps = 30 // visual ticks

        for (i in 0..steps) {
            val t = i / steps.toFloat()
            val y = lerp(scaleTop, scaleBottom, t)

            val isMajor = i % 5 == 0

            drawLine(
                color = if (isMajor) Color(0xFFBFA06A) else Color(0xFF666666),
                start = Offset(centerX - if (isMajor) 40f else 20f, y),
                end = Offset(centerX + if (isMajor) 40f else 20f, y),
                strokeWidth = if (isMajor) 3f else 1.5f
            )
        }

        // =========================
        // ⚙️ PIVOT
        // =========================
        val pivotRadius = 18f
        val pivotCenter = Offset(centerX, scaleBottom + 40f)

        drawCircle(
            brush = Brush.radialGradient(
                listOf(Color.LightGray, Color.DarkGray)
            ),
            radius = pivotRadius,
            center = pivotCenter
        )

        // =========================
        // 🎯 PENDULUM (ROTATING)
        // =========================
        rotate(
            degrees = angle,
            pivot = Offset(centerX, scaleTop)
        ) {
            // Rod
            drawLine(
                color = Color(0xFFCCCCCC),
                start = Offset(centerX, scaleTop),
                end = Offset(centerX, scaleBottom),
                strokeWidth = 6f
            )

            // Weight position (maps BPM)
            val normalized = ((bpm - 40f) / (208f - 40f)).coerceIn(0f, 1f)
            val weightY = lerp(scaleTop + 40f, scaleBottom - 60f, normalized)

            // Weight shadow
            drawRoundRect(
                color = Color.Black.copy(alpha = 0.4f),
                topLeft = Offset(centerX - 32f + 4f, weightY - 20f + 4f),
                size = Size(64f, 40f),
                cornerRadius = CornerRadius(12f, 12f)
            )

            // Weight
            drawRoundRect(
                brush = Brush.verticalGradient(
                    listOf(Color(0xFFE0E0E0), Color(0xFF888888))
                ),
                topLeft = Offset(centerX - 32f, weightY - 20f),
                size = Size(64f, 40f),
                cornerRadius = CornerRadius(12f, 12f)
            )
        }

        // =========================
        // 💡 CENTER GLOW (subtle feedback)
        // =========================
        val glowAlpha = ((sin(phase) + 1f) / 2f) * 0.2f

        drawCircle(
            color = Color(0xFFFFA726).copy(alpha = glowAlpha),
            radius = 120f,
            center = Offset(centerX, h * 0.5f)
        )
    }
}