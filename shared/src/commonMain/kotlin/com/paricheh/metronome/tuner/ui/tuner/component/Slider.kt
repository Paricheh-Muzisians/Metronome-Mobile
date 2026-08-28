package com.paricheh.metronome.tuner.ui.tuner.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import kotlin.math.abs
import kotlin.math.roundToInt

// --- 1. State Models & Theming ---

enum class SliderState { GOOD, WARNING, BAD, IDLE }

data class HistoryPoint(
    val value: Float,
    val state: SliderState,
    val spawnScroll: Float,
)

data class SliderColors(
    val goodColor: Color,
    val goodTextColor: Color,
    val warningTextColor: Color,
    val warningColor: Color,
    val badColor: Color,
    val badTextColor: Color,
    val idleColor: Color,
)

@Composable
fun sliderColors(
    goodColor: Color = MaterialTheme.colorScheme.tertiaryContainer,
    warningColor: Color = MaterialTheme.colorScheme.primaryContainer,
    badColor: Color = MaterialTheme.colorScheme.errorContainer,
    idleColor: Color = MaterialTheme.colorScheme.outline,
    goodTextColor: Color = MaterialTheme.colorScheme.onTertiaryContainer,
    warningTextColor: Color = MaterialTheme.colorScheme.onPrimaryContainer,
    badTextColor: Color = MaterialTheme.colorScheme.onErrorContainer,
) = SliderColors(
    goodColor = goodColor,
    goodTextColor = goodTextColor,
    warningTextColor = warningTextColor,
    warningColor = warningColor,
    badColor = badColor,
    badTextColor = badTextColor,
    idleColor = idleColor
)

fun resolveState(
    currentValue: Float?,
    goodThreshold: Float,
    warningThreshold: Float,
): SliderState {
    if (currentValue == null) return SliderState.IDLE
    val distance = abs(currentValue)
    return when {
        distance <= goodThreshold -> SliderState.GOOD
        distance <= warningThreshold -> SliderState.WARNING
        else -> SliderState.BAD
    }
}

// --- 2. Component Implementation ---

@Composable
fun ValueHistorySlider(
    currentValue: Float?,
    min: Float,
    max: Float,
    goodThreshold: Float,
    warningThreshold: Float,
    minText: String,
    maxText: String,
    title: String,
    modifier: Modifier = Modifier,
    colors: SliderColors = sliderColors(),
) {
    // History & Scroll State
    var scrollOffset by remember { mutableStateOf(0f) }
    val historyList = remember { mutableStateListOf<HistoryPoint>() }
    val density = LocalDensity.current
    var lastNonNullValue by remember { mutableStateOf(currentValue ?: 0f) }
    val targetValue = currentValue ?: lastNonNullValue

    // Animations
    val animatedValue by animateFloatAsState(
        targetValue = targetValue,
        animationSpec = tween(500, easing = FastOutSlowInEasing),
        label = "indicatorX"
    )

    val currentState = resolveState(currentValue, goodThreshold, warningThreshold)
    val targetColor = when (currentState) {
        SliderState.GOOD -> colors.goodColor
        SliderState.WARNING -> colors.warningColor
        SliderState.BAD -> colors.badColor
        SliderState.IDLE -> colors.idleColor
    }

    val indicatorColor by animateColorAsState(
        targetValue = targetColor,
        animationSpec = tween(300),
        label = "indicatorColor"
    )

    val bounceAnimatable = remember { Animatable(0f) }

    LaunchedEffect(currentValue != null) {
        if (currentValue != null) {
            bounceAnimatable.animateTo(0f, tween(300))
        } else {
            bounceAnimatable.animateTo(
                targetValue = -15f,
                animationSpec = infiniteRepeatable(
                    animation = tween(800),
                    repeatMode = RepeatMode.Reverse
                )
            )
        }
    }

    // Anchor Point Creation & Idle Bouncing
    LaunchedEffect(currentValue) {
        if (currentValue != null) {
            // Drop an anchor at the exact current position to handle rapid changes seamlessly
            val newState = resolveState(currentValue, goodThreshold, warningThreshold)

            // Avoid dropping duplicates if the value hasn't actually changed
            if (historyList.isEmpty() || currentValue != lastNonNullValue) {
                historyList.add(HistoryPoint(animatedValue, newState, scrollOffset))
            }
            lastNonNullValue = currentValue
        } else {
            // Cap off history line before going idle
            historyList.add(HistoryPoint(animatedValue, SliderState.IDLE, scrollOffset))
        }
    }

    // Scroll Ticker
    LaunchedEffect(Unit) {
        var lastTime = withFrameNanos { it }
        while (true) {
            val time = withFrameNanos { it }
            val deltaSeconds = (time - lastTime) / 1_000_000_000f
            scrollOffset += deltaSeconds * 60f // Scrolling speed (pixels/sec conceptually)
            lastTime = time

            // Memory cleanup: Discard points completely off the bottom of the canvas
            // We keep points until their Y coordinate (which is indicatorBaseYPx + scrollOffset - spawn) is past the board
            val cutoff = scrollOffset - 1500f
            var dropCount = 0
            while (dropCount < historyList.size - 1 && historyList[dropCount + 1].spawnScroll < cutoff) {
                dropCount++
            }
            if (dropCount > 0) {
                val retained = historyList.drop(dropCount)
                historyList.clear()
                historyList.addAll(retained)
            }
        }
    }

    // Layout
    Box(
        modifier = modifier.fillMaxWidth(),
        contentAlignment = Alignment.TopCenter
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp)
                .padding(horizontal = 32.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = minText,
                color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.bodySmall
            )
            Text(
                text = maxText,
                color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.bodySmall
            )
        }

        val surfaceColor = MaterialTheme.colorScheme.surface
        BoxWithConstraints(
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
                .padding(top = 48.dp)
                .fillMaxWidth()
                .height(200.dp),
            contentAlignment = Alignment.TopStart // Explicit alignment fixes coordinate mismatch
        ) {
            val paddingPx = with(density) { 32.dp.toPx() }
            val trackWidthPx = constraints.maxWidth - 2 * paddingPx
            val indicatorBaseYPx =
                with(density) { 40.dp.toPx() } // Moved to top for downward scrolling
            val indicatorSizeDp = 32.dp

            fun getX(v: Float): Float = paddingPx + ((v - min) / (max - min)) * trackWidthPx
            fun getY(spawn: Float): Float = indicatorBaseYPx + (scrollOffset - spawn) // Flows down

            // History Canvas
            Canvas(modifier = Modifier
                .graphicsLayer {
                    rotationY = 180f
                }
                .fillMaxSize().clipToBounds()) {

                val canvasWidth = size.width
                val canvasHeight = size.height

                // Draw Grid Background
                val gridSize = 15.dp.toPx()
                var x = 0f
                while (x < canvasWidth) {
                    drawLine(
                        Color.DarkGray.copy(alpha = 0.3f),
                        Offset(x, 0f),
                        Offset(x, canvasHeight),
                        1f
                    )
                    x += gridSize
                }
                var y = 0f
                while (y < canvasHeight) {
                    drawLine(
                        Color.DarkGray.copy(alpha = 0.3f),
                        Offset(0f, y),
                        Offset(canvasWidth, y),
                        1f
                    )
                    y += gridSize
                }

                // Horizontal Scale / Track line
                drawLine(
                    color = Color.DarkGray.copy(alpha = 0.3f),
                    start = Offset(paddingPx, indicatorBaseYPx),
                    end = Offset(size.width - paddingPx, indicatorBaseYPx),
                    strokeWidth = 4f,
                    cap = StrokeCap.Round
                )

                if (min < 0 && max > 0) {
                    val centerX = getX(0f)
                    drawLine(
                        color = Color.DarkGray.copy(alpha = 0.3f),
                        start = Offset(centerX, indicatorBaseYPx - 8f),
                        end = Offset(centerX, indicatorBaseYPx + 8f),
                        strokeWidth = 4f,
                        cap = StrokeCap.Round
                    )
                }

                if (historyList.isNotEmpty()) {
                    for (i in 0 until historyList.size - 1) {
                        val p1 = historyList[i]
                        val p2 = historyList[i + 1]
                        val x1 = getX(p1.value)
                        val y1 = getY(p1.spawnScroll)
                        val x2 = getX(p2.value)
                        val y2 = getY(p2.spawnScroll)

                        val segmentColor = when (p1.state) {
                            SliderState.GOOD -> colors.goodColor
                            SliderState.WARNING -> colors.warningColor
                            SliderState.BAD -> colors.badColor
                            SliderState.IDLE -> Color.Transparent
                        }

                        val path = Path().apply {
                            moveTo(x1, y1)
                            cubicTo(x1, (y1 + y2) / 2, x2, (y1 + y2) / 2, x2, y2)
                        }
                        drawPath(
                            path = path,
                            color = segmentColor,
                            style = Stroke(
                                width = 8f,
                                cap = StrokeCap.Round,
                                join = StrokeJoin.Round
                            )
                        )
                    }

                    if (currentValue != null) {
                        val pLast = historyList.last()
                        val x1 = getX(pLast.value)
                        val y1 = getY(pLast.spawnScroll)
                        val x2 = getX(animatedValue)
                        val y2 = indicatorBaseYPx

                        val segmentColor = when (pLast.state) {
                            SliderState.GOOD -> colors.goodColor
                            SliderState.WARNING -> colors.warningColor
                            SliderState.BAD -> colors.badColor
                            SliderState.IDLE -> Color.Transparent
                        }

                        val path = Path().apply {
                            moveTo(x1, y1)
                            cubicTo(x1, (y1 + y2) / 2, x2, (y1 + y2) / 2, x2, y2)
                        }
                        drawPath(
                            path = path,
                            color = segmentColor,
                            style = Stroke(
                                width = 8f,
                                cap = StrokeCap.Round,
                                join = StrokeJoin.Round
                            )
                        )
                    }
                }
            }

            // Animated Indicator Layer
            val animatedXPx = getX(animatedValue)
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(indicatorSizeDp)
                    .offset {
                        IntOffset(
                            x = (animatedXPx - indicatorSizeDp.toPx() / 2).roundToInt(),
                            y = (indicatorBaseYPx - indicatorSizeDp.toPx() / 2 + bounceAnimatable.value).roundToInt()
                        )
                    }
                    .background(indicatorColor, CircleShape)
            ) {
                AnimatedVisibility(
                    visible = currentValue != null,
                    enter = fadeIn(),
                    exit = fadeOut()
                ) {
                    val displayVal = animatedValue.roundToInt()
                    val prefix = if (displayVal > 0) "+" else ""
                    Text(
                        text = "$prefix$displayVal",
                        color = when (currentState) {
                            SliderState.GOOD -> colors.goodTextColor
                            SliderState.WARNING -> colors.warningTextColor
                            SliderState.BAD -> colors.badTextColor
                            SliderState.IDLE -> Color.Transparent
                        },
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

// --- 3. Demo Screen ---

@Composable
fun DemoScreen() {
    var currentValue by remember { mutableStateOf<Float?>(0f) }
    var sliderRawValue by remember { mutableStateOf(0f) }
    val componentColors = sliderColors()

    Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            ValueHistorySlider(
                currentValue = currentValue,
                min = -50f,
                max = 50f,
                goodThreshold = 5f,
                warningThreshold = 15f,
                minText = "-50",
                maxText = "+50",
                title = "Live Quality Assessment"
            )

            Spacer(modifier = Modifier.height(48.dp))

            Text("Current Value: ${currentValue ?: "Idle"}", fontWeight = FontWeight.Bold)
            Slider(
                value = sliderRawValue,
                onValueChange = {
                    sliderRawValue = it
                    currentValue = it
                },
                valueRange = -50f..50f,
                modifier = Modifier.padding(horizontal = 32.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Button(
                    onClick = { sliderRawValue = 3f; currentValue = sliderRawValue },
                    colors = ButtonDefaults.buttonColors(containerColor = componentColors.goodColor)
                ) { Text("Good") }

                Button(
                    onClick = { sliderRawValue = 12f; currentValue = sliderRawValue },
                    colors = ButtonDefaults.buttonColors(containerColor = componentColors.warningColor)
                ) { Text("Warn") }

                Button(
                    onClick = { sliderRawValue = -34f; currentValue = sliderRawValue },
                    colors = ButtonDefaults.buttonColors(containerColor = componentColors.badColor)
                ) { Text("Bad") }

                Button(
                    onClick = { currentValue = null },
                    colors = ButtonDefaults.buttonColors(containerColor = componentColors.idleColor)
                ) { Text("Idle") }
            }
        }
    }
}
