package com.paricheh.metronome.tuner.ui.tuner.component

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
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
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlin.math.abs
import kotlin.math.roundToInt
import kotlin.time.Duration.Companion.seconds

private enum class SliderState {
    GOOD, WARNING, BAD, IDLE
}

private const val minRange = -50f
private const val maxRange = 50f
private const val goodThreshold = 7f
private const val warningThreshold = 20f

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TunerSlider(
    centDifference: Float?,
    title: @Composable () -> Unit,
    onTuned: () -> Unit,
) {
    val animatedValue by animateFloatAsState(centDifference ?: 0f)
    var currentStatus by remember { mutableStateOf(SliderState.IDLE) }
    var isTuned by remember { mutableStateOf(false) }

    val backgroundColor by animateColorAsState(
        when (currentStatus) {
            SliderState.IDLE -> MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
            SliderState.GOOD -> MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.2f)
            SliderState.WARNING -> MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
            SliderState.BAD -> MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.2f)
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
        modifier = Modifier.fillMaxWidth()
            .background(
                brush = Brush.verticalGradient(
                    listOf(
                        Color.Transparent,
                        backgroundColor,
                        Color.Transparent
                    )
                )
            )
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
                .padding(24.dp), verticalAlignment = Alignment.Bottom,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "${minRange.toInt()}",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Text(
                text = "+${maxRange.toInt()}",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }


        Slider(
            modifier = Modifier
                .align(Alignment.Center)
                .fillMaxWidth(),
            value = animatedValue,
            onValueChange = {},
            enabled = false,
            valueRange = minRange..maxRange,
            thumb = {
                AnimatedContent(isTuned) {
                    if (it) {
                        Box(
                            modifier = Modifier
                                .wrapContentHeight()
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.7f))
                                .size(24.dp)
                        ) {
                            Icon(
                                modifier = Modifier
                                    .matchParentSize()
                                    .padding(2.dp)
                                    .align(Alignment.Center),
                                imageVector = Icons.Rounded.Check,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onTertiaryContainer
                            )
                        }
                    } else {
                        Box(
                            modifier = Modifier
                                .wrapContentHeight()
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.secondary.copy(alpha = 0.7f))
                                .size(24.dp)
                        ) {
                            centDifference?.roundToInt()?.let {
                                Text(
                                    modifier = Modifier.align(Alignment.Center),
                                    text = it.toString(),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSecondary
                                )
                            }
                        }
                    }

                }
            },
            track = {
                val primaryColor = MaterialTheme.colorScheme.primary

                Canvas(
                    modifier = Modifier
                        .height(72.dp)
                        .fillMaxWidth()
                ) {
                    drawCircle(
                        color = primaryColor.copy(alpha = 0.1f)
                    )

                    drawCircle(
                        color = primaryColor.copy(alpha = 0.1f),
                        radius = size.height / 4
                    )
                }
            },
        )

    }
}