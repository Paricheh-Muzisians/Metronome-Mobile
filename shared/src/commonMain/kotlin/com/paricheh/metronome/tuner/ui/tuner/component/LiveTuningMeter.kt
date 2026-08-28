package com.paricheh.metronome.tuner.ui.tuner.component

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.paricheh.metronome.tuner.data.theory.TuningStatus
import kotlin.math.roundToInt

@Composable
fun LiveTuningMeter(
    cents: Float,
    modifier: Modifier = Modifier,
    status: TuningStatus,
) {
    val animatedCents by animateFloatAsState(
        targetValue = cents.coerceIn(-50f, 50f),
        animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
        label = "cents_animation"
    )

    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Status Text
        Text(
            text = getStatusLabel(cents, status),
            color = status.toColor(),
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(80.dp)
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val width = size.width
                val height = size.height
                val centerY = height / 2

                // Draw background track
                drawRoundRect(
                    color = ColorGridLine,
                    topLeft = Offset(0f, centerY - 2.dp.toPx()),
                    size = Size(width, 4.dp.toPx()),
                    cornerRadius = CornerRadius(2.dp.toPx())
                )

                // Draw Ticks (-50, -25, 0, 25, 50)
                val ticks = listOf(-50, -25, 0, 25, 50)
                ticks.forEach { tick ->
                    val x = width * ((tick + 50f) / 100f)
                    val isCenter = tick == 0
                    val tickHeight = if (isCenter) 24.dp.toPx() else 12.dp.toPx()
                    val tickColor = if (isCenter) ColorTextPrimary else ColorTextSecondary

                    drawLine(
                        color = tickColor,
                        start = Offset(x, centerY - tickHeight / 2),
                        end = Offset(x, centerY + tickHeight / 2),
                        strokeWidth = if (isCenter) 3.dp.toPx() else 1.5.dp.toPx()
                    )
                }

                // Calculate indicator position
                val indicatorX = width * ((animatedCents + 50f) / 100f)
                val indicatorColor = status.toColor()

                // Draw Indicator Needle
                drawLine(
                    color = indicatorColor,
                    start = Offset(indicatorX, 0f),
                    end = Offset(indicatorX, height),
                    strokeWidth = 4.dp.toPx()
                )
            }

            // Numeric Label attached to the needle
            BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
                val indicatorPercent = (animatedCents + 50f) / 100f
                val sign = if (animatedCents > 0) "+" else ""
                val textValue = "$sign${animatedCents.roundToInt()}¢"

                Text(
                    text = textValue,
                    color = ColorTextPrimary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .offset(
                            x = (maxWidth * indicatorPercent) - 16.dp, // Center offset approximation
                            y = (-20).dp
                        )
                )
            }

            // Static labels for bounds
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .offset(y = 24.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("-50", color = ColorTextSecondary, fontSize = 12.sp)
                Text("0", color = ColorTextPrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                Text("+50", color = ColorTextSecondary, fontSize = 12.sp)
            }
        }
    }
}