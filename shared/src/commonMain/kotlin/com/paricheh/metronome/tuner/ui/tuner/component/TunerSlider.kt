package com.paricheh.metronome.tuner.ui.tuner.component

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.InfiniteRepeatableSpec
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.paricheh.metronome.designsystem.NonCommonTypography
import kotlinx.coroutines.delay
import kotlin.math.abs
import kotlin.time.Duration.Companion.seconds

private enum class SliderState {
    GOOD, WARNING, BAD, IDLE
}

private const val minRange = -50f
private const val maxRange = 50f
const val goodThreshold = 5f
private const val warningThreshold = 20f

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun TunerSlider(
    centDifference: Float?,
    centDifferenceText: String,
    title: @Composable () -> Unit,
    onTuned: () -> Unit,
) {
    val animatedValue by animateFloatAsState(centDifference ?: 0f)
    var currentStatus by remember { mutableStateOf(SliderState.IDLE) }
    var isTuned by remember { mutableStateOf(false) }

    val indicatorColor by animateColorAsState(
        when (currentStatus) {
            SliderState.GOOD -> MaterialTheme.colorScheme.tertiaryContainer
            SliderState.WARNING -> MaterialTheme.colorScheme.primaryContainer
            SliderState.BAD -> MaterialTheme.colorScheme.errorContainer
            SliderState.IDLE -> MaterialTheme.colorScheme.secondaryContainer
        }
    )
    val onIndicatorColor by animateColorAsState(
        when (currentStatus) {
            SliderState.GOOD -> MaterialTheme.colorScheme.onTertiaryContainer
            SliderState.WARNING -> MaterialTheme.colorScheme.onPrimaryContainer
            SliderState.BAD -> MaterialTheme.colorScheme.onErrorContainer
            SliderState.IDLE -> MaterialTheme.colorScheme.onSecondaryContainer
        }
    )

    LaunchedEffect(centDifference) {
        currentStatus = if (centDifference == null) {
            SliderState.IDLE
        } else {
            val distance = abs(centDifference)
            when {
                distance <= goodThreshold -> SliderState.GOOD
                distance <= warningThreshold -> SliderState.WARNING
                else -> SliderState.BAD
            }
        }
    }

    LaunchedEffect(currentStatus) {
        if (currentStatus == SliderState.GOOD) {
            delay(1.seconds)
            isTuned = true
            onTuned()
        } else {
            isTuned = false
        }
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(2f)
    ) {
        Box(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .fillMaxWidth(),
            contentAlignment = Alignment.TopCenter
        ) {
            title()
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.TopCenter)
                .padding(24.dp),
            verticalAlignment = Alignment.Bottom,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "♯",
                style = NonCommonTypography.musicFont,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Text(
                text = "♭",
                style = NonCommonTypography.musicFont,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
            Slider(
                modifier = Modifier
                    .align(Alignment.Center)
                    .fillMaxWidth(),
                value = animatedValue,
                onValueChange = {},
                enabled = false,
                valueRange = minRange..maxRange,
                thumb = {
                    Box(
                        modifier = Modifier
                            .padding(16.dp)
                            .clip(CircleShape)
                            .size(48.dp)
                            .aspectRatio(1f)
                            .background(indicatorColor),
                        contentAlignment = Alignment.Center
                    ) {
                        AnimatedContent(isTuned) {
                            if (it) {
                                Icon(
                                    modifier = Modifier
                                        .size(28.dp)
                                        .align(Alignment.Center),
                                    imageVector = Icons.Rounded.Check,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onTertiaryContainer
                                )
                            } else {
                                Text(
                                    text = centDifferenceText,
                                    color = onIndicatorColor,
                                    style = MaterialTheme.typography.titleMedium
                                )
                            }
                        }
                    }
                },
                track = {
                    val infiniteTransition = rememberInfiniteTransition()
                    val rotation by infiniteTransition.animateFloat(
                        initialValue = 0f,
                        targetValue = 360f,
                        animationSpec = InfiniteRepeatableSpec(
                            animation = tween(10000),
                            repeatMode = RepeatMode.Reverse
                        )
                    )
                    val onSurfaceColor = MaterialTheme.colorScheme.onSurface

                    Box(
                        modifier = Modifier
                            .height(72.dp)
                            .fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        Canvas(modifier = Modifier.matchParentSize()) {
                            val centerY = size.height / 2f
                            val radius = size.height / 2f

                            val lineColor = onSurfaceColor.copy(alpha = 0.2f)

                            drawLine(
                                color = lineColor,
                                start = Offset(0f, centerY),
                                end = Offset(center.x - radius, centerY),
                                strokeWidth = 1.dp.toPx()
                            )

                            drawLine(
                                color = lineColor,
                                start = Offset(center.x + radius, centerY),
                                end = Offset(size.width, centerY),
                                strokeWidth = 1.dp.toPx()
                            )

                            rotate(
                                rotation
                            ) {
                                drawCircle(
                                    radius = radius,
                                    color = indicatorColor.copy(alpha = 0.8f),
                                    style = Stroke(
                                        width = 2.dp.toPx(),
                                        pathEffect = PathEffect.dashPathEffect(
                                            intervals = floatArrayOf(
                                                2.dp.toPx(),
                                                5.dp.toPx()
                                            )
                                        ),
                                        join = StrokeJoin.Round,
                                        miter = 10f,
                                        cap = StrokeCap.Round
                                    ),
                                )
                            }
                        }
                    }
                },
            )
        }
    }
}