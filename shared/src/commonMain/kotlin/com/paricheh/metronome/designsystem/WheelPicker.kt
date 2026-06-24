package com.paricheh.metronome.designsystem

import androidx.annotation.FloatRange
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationResult
import androidx.compose.animation.core.AnimationVector1D
import androidx.compose.animation.core.DecayAnimationSpec
import androidx.compose.animation.core.calculateTargetValue
import androidx.compose.animation.core.exponentialDecay
import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.NonRestartableComposable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.composables.core.ModalBottomSheet
import com.composables.core.ModalBottomSheetState
import com.composables.core.Scrim
import com.composables.core.Sheet
import kotlinx.coroutines.launch
import kotlin.math.abs
import kotlin.math.roundToInt

@Composable
fun <T> VerticalWheelPicker(
    items: List<T>,
    provideValue: () -> T,
    onValueChange: (T) -> Unit,
    modifier: Modifier = Modifier,
    label: (T) -> String = { it.toString() },
    textStyle: TextStyle = MaterialTheme.typography.labelLarge,
    dividersColor: Color = MaterialTheme.colorScheme.outlineVariant,
    @FloatRange(from = 1.0, to = 2.0) selectedTextScale: Float = 1f,
) {
    val verticalMargin = 8.dp
    val numbersColumnHeight = 80.dp
    val halfNumbersColumnHeight = numbersColumnHeight / 2
    val halfNumbersColumnHeightPx = with(LocalDensity.current) { halfNumbersColumnHeight.toPx() }

    val coroutineScope = rememberCoroutineScope()

    val animatedOffset = remember { Animatable(0f) }
        .apply {
            val index = items.indexOf(provideValue.invoke())
            val offsetRange = remember(provideValue.invoke(), items) {
                -((items.count() - 1) - index) * halfNumbersColumnHeightPx to
                    index * halfNumbersColumnHeightPx
            }
            updateBounds(offsetRange.first, offsetRange.second)
        }

    val coercedAnimatedOffset by remember(animatedOffset.value) {
        derivedStateOf {
            animatedOffset.value % halfNumbersColumnHeightPx
        }
    }

    val indexOfElement = remember(
        items,
        provideValue.invoke(),
        animatedOffset.value
    ) {
        getItemIndexForOffset(
            items,
            provideValue.invoke(),
            animatedOffset.value,
            halfNumbersColumnHeightPx
        )
    }

    var dividerWidth by remember { mutableStateOf(0.dp) }

    Layout(
        modifier = modifier
            .draggable(
                orientation = Orientation.Vertical,
                state = rememberDraggableState { deltaY ->
                    coroutineScope.launch {
                        animatedOffset.snapTo(animatedOffset.value + deltaY)
                    }
                },
                onDragStopped = { velocity ->
                    coroutineScope.launch {
                        val endValue = animatedOffset.fling(
                            initialVelocity = velocity,
                            animationSpec = exponentialDecay(frictionMultiplier = 15f),
                            adjustTarget = { target ->
                                val coercedTarget = target % halfNumbersColumnHeightPx
                                val coercedAnchors =
                                    listOf(
                                        -halfNumbersColumnHeightPx,
                                        0f,
                                        halfNumbersColumnHeightPx
                                    )
                                val coercedPoint =
                                    coercedAnchors.minByOrNull { abs(it - coercedTarget) }
                                val base =
                                    halfNumbersColumnHeightPx * (target / halfNumbersColumnHeightPx).toInt()
                                (coercedPoint ?: 0f) + base
                            }
                        ).endState.value

                        val result = items.elementAt(
                            getItemIndexForOffset(
                                items,
                                provideValue.invoke(),
                                endValue,
                                halfNumbersColumnHeightPx
                            )
                        )
                        onValueChange(result)
                        animatedOffset.snapTo(0f)
                    }
                }
            )
            .clip(RectangleShape)
            .padding(vertical = numbersColumnHeight / 3 + verticalMargin * 2),
        content = {
            Box(
                modifier
                    .width(dividerWidth)
                    .height(1.dp)
                    .background(color = dividersColor)
            )
            Box(
                modifier = Modifier
                    .padding(vertical = verticalMargin, horizontal = 20.dp)
                    .offset { IntOffset(x = 0, y = coercedAnimatedOffset.roundToInt()) }
            ) {
                val baseLabelModifier = Modifier.align(Alignment.Center)
                ProvideTextStyle(textStyle) {
                    if (indexOfElement >= 2) {
                        Label(
                            text = label(items.elementAt(indexOfElement - 2)),
                            modifier = baseLabelModifier
                                .offset {
                                    IntOffset(
                                        x = 0,
                                        y = -halfNumbersColumnHeight.roundToPx() * 2
                                    )
                                }
                                .alpha(MINIMUM_ALPHA)
                        )
                    }
                    if (indexOfElement >= 1) {
                        Label(
                            text = label(items.elementAt(indexOfElement - 1)),
                            modifier = baseLabelModifier
                                .offset {
                                    IntOffset(
                                        x = 0,
                                        y = -halfNumbersColumnHeight.roundToPx()
                                    )
                                }
                                .alpha(
                                    maxOf(
                                        MINIMUM_ALPHA,
                                        coercedAnimatedOffset / halfNumbersColumnHeightPx
                                    )
                                )
                        )
                    }
                    Label(
                        text = label(items.elementAt(indexOfElement)),
                        modifier = baseLabelModifier
                            .scale(selectedTextScale)
                            .alpha(
                                maxOf(
                                    MINIMUM_ALPHA,
                                    1 - abs(coercedAnimatedOffset) / halfNumbersColumnHeightPx
                                )
                            )
                    )
                    if (indexOfElement < items.size - 1) {
                        Label(
                            text = label(items.elementAt(indexOfElement + 1)),
                            modifier = baseLabelModifier
                                .offset {
                                    IntOffset(
                                        x = 0,
                                        y = halfNumbersColumnHeight.roundToPx()
                                    )
                                }
                                .alpha(
                                    maxOf(
                                        MINIMUM_ALPHA,
                                        -coercedAnimatedOffset / halfNumbersColumnHeightPx
                                    )
                                )
                        )
                    }
                    if (indexOfElement < items.size - 2) {
                        Label(
                            text = label(items.elementAt(indexOfElement + 2)),
                            modifier = baseLabelModifier
                                .offset {
                                    IntOffset(
                                        x = 0,
                                        y = halfNumbersColumnHeight.roundToPx() * 2
                                    )
                                }
                                .alpha(MINIMUM_ALPHA)
                        )
                    }
                }
            }
            Box(
                modifier
                    .width(dividerWidth)
                    .height(1.dp)
                    .background(color = dividersColor)
            )
        }
    ) { measurables, constraints ->
        // Don't constrain child views further, measure them with given constraints
        // List of measured children
        val placeables = measurables.map { measurable ->
            // Measure each child
            measurable.measure(constraints)
        }

        dividerWidth = placeables
            .drop(1)
            .first()
            .width
            .toDp()

        // Set the size of the layout as big as it can
        layout(
            width = dividerWidth.toPx().toInt(),
            height = placeables.sumOf { it.height }
        ) {
            // Track the y co-ord we have placed children up to
            var yPosition = 0

            // Place children in the parent layout
            placeables.forEach { placeable ->

                // Position item on the screen
                placeable.placeRelative(x = 0, y = yPosition)

                // Record the y co-ord placed up to
                yPosition += placeable.height
            }
        }
    }
}

@Composable
fun <T> HorizontalWheelPicker(
    items: List<T>,
    provideValue: () -> T,
    onValueChange: (T) -> Unit,
    modifier: Modifier = Modifier,
    label: (T) -> String = { it.toString() },
    textStyle: TextStyle = MaterialTheme.typography.labelLarge,
    dividersColor: Color = MaterialTheme.colorScheme.outlineVariant,
    numbersColumnWidth : Dp = 120.dp,
    @FloatRange(from = 1.0, to = 2.0) selectedTextScale: Float = 1f,
) {
    val horizontalMargin = 8.dp
    val halfNumbersColumnWidth = numbersColumnWidth / 2
    val halfNumbersColumnWidthPx = with(LocalDensity.current) { halfNumbersColumnWidth.toPx() }

    val coroutineScope = rememberCoroutineScope()

    val animatedOffset = remember { Animatable(0f) }
        .apply {
            val index = items.indexOf(provideValue.invoke())
            val offsetRange = remember(provideValue.invoke(), items) {
                -((items.count() - 1) - index) * halfNumbersColumnWidthPx to
                    index * halfNumbersColumnWidthPx
            }
            updateBounds(offsetRange.first, offsetRange.second)
        }

    val coercedAnimatedOffset by remember(animatedOffset.value) {
        derivedStateOf {
            animatedOffset.value % halfNumbersColumnWidthPx
        }
    }

    val indexOfElement = remember(
        items,
        provideValue.invoke(),
        animatedOffset.value
    ) {
        getItemIndexForOffset(
            items,
            provideValue.invoke(),
            animatedOffset.value,
            halfNumbersColumnWidthPx
        )
    }

    // Now we need a vertical divider height; measure content height later
    var dividerHeight by remember { mutableStateOf(0.dp) }

    Layout(
        modifier = modifier
            .draggable(
                orientation = Orientation.Horizontal,
                state = rememberDraggableState { deltaX ->
                    coroutineScope.launch {
                        animatedOffset.snapTo(animatedOffset.value - deltaX)
                    }
                },
                onDragStopped = { velocity ->
                    coroutineScope.launch {
                        val endValue = animatedOffset.fling(
                            initialVelocity = velocity,
                            animationSpec = exponentialDecay(frictionMultiplier = 15f),
                            adjustTarget = { target ->
                                val coercedTarget = target % halfNumbersColumnWidthPx
                                val coercedAnchors =
                                    listOf(
                                        -halfNumbersColumnWidthPx,
                                        0f,
                                        halfNumbersColumnWidthPx
                                    )
                                val coercedPoint =
                                    coercedAnchors.minByOrNull { abs(it - coercedTarget) }
                                val base =
                                    halfNumbersColumnWidthPx * (target / halfNumbersColumnWidthPx).toInt()
                                (coercedPoint ?: 0f) + base
                            }
                        ).endState.value

                        val result = items.elementAt(
                            getItemIndexForOffset(
                                items,
                                provideValue.invoke(),
                                endValue,
                                halfNumbersColumnWidthPx
                            )
                        )
                        onValueChange(result)
                        animatedOffset.snapTo(0f)
                    }
                }
            )
            .clip(RectangleShape)
            .padding(horizontal = numbersColumnWidth / 3 + horizontalMargin * 2),
        content = {
            // Left divider (vertical line)
            Box(
                Modifier
                    .height(dividerHeight)
                    .width(1.dp)
                    .background(color = dividersColor)
            )
            // Scrollable content
            Box(
                modifier = Modifier
                    .padding(horizontal = horizontalMargin, vertical = 20.dp)
                    .offset { IntOffset(x = coercedAnimatedOffset.roundToInt(), y = 0) }
            ) {
                val baseLabelModifier = Modifier.align(Alignment.Center)
                ProvideTextStyle(textStyle) {
                    if (indexOfElement >= 2) {
                        Label(
                            text = label(items.elementAt(indexOfElement - 2)),
                            modifier = baseLabelModifier
                                .offset {
                                    IntOffset(
                                        x = -halfNumbersColumnWidth.roundToPx() * 2,
                                        y = 0
                                    )
                                }
                                .alpha(MINIMUM_ALPHA)
                        )
                    }
                    if (indexOfElement >= 1) {
                        Label(
                            text = label(items.elementAt(indexOfElement - 1)),
                            modifier = baseLabelModifier
                                .offset {
                                    IntOffset(
                                        x = -halfNumbersColumnWidth.roundToPx(),
                                        y = 0
                                    )
                                }
                                .alpha(
                                    maxOf(
                                        MINIMUM_ALPHA,
                                        coercedAnimatedOffset / halfNumbersColumnWidthPx
                                    )
                                )
                        )
                    }
                    Label(
                        text = label(items.elementAt(indexOfElement)),
                        modifier = baseLabelModifier
                            .scale(selectedTextScale)
                            .alpha(
                                maxOf(
                                    MINIMUM_ALPHA,
                                    1 - abs(coercedAnimatedOffset) / halfNumbersColumnWidthPx
                                )
                            )
                    )
                    if (indexOfElement < items.size - 1) {
                        Label(
                            text = label(items.elementAt(indexOfElement + 1)),
                            modifier = baseLabelModifier
                                .offset {
                                    IntOffset(
                                        x = halfNumbersColumnWidth.roundToPx(),
                                        y = 0
                                    )
                                }
                                .alpha(
                                    maxOf(
                                        MINIMUM_ALPHA,
                                        -coercedAnimatedOffset / halfNumbersColumnWidthPx
                                    )
                                )
                        )
                    }
                    if (indexOfElement < items.size - 2) {
                        Label(
                            text = label(items.elementAt(indexOfElement + 2)),
                            modifier = baseLabelModifier
                                .offset {
                                    IntOffset(
                                        x = halfNumbersColumnWidth.roundToPx() * 2,
                                        y = 0
                                    )
                                }
                                .alpha(MINIMUM_ALPHA)
                        )
                    }
                }
            }
            // Right divider (vertical line)
            Box(
                Modifier
                    .height(dividerHeight)
                    .width(1.dp)
                    .background(color = dividersColor)
            )
        }
    ) { measurables, constraints ->
        val placeables = measurables.map { it.measure(constraints) }

        // Height is determined by the tallest child (content area)
        val contentHeight = placeables.drop(1).first().height.toDp()
        dividerHeight = contentHeight

        layout(
            width = placeables.sumOf { it.width },
            height = contentHeight.toPx().toInt()
        ) {
            var xPosition = 0
            placeables.forEach { placeable ->
                placeable.placeRelative(x = xPosition, y = 0)
                xPosition += placeable.width
            }
        }
    }
}


@Composable
@NonRestartableComposable
private fun Label(
    modifier: Modifier = Modifier,
    text: String,
) {
    Text(
        modifier = modifier
            .pointerInput(Unit) {
                detectTapGestures(onLongPress = {})
            }
            .basicMarquee(),
        text = text,
        textAlign = TextAlign.Center,
        maxLines = 1,
        color = MaterialTheme.colorScheme.onSurface
    )
}

private fun <T> getItemIndexForOffset(
    range: List<T>,
    value: T,
    offset: Float,
    halfNumbersColumnHeightPx: Float,
): Int {
    val indexOf = range.indexOf(value) - (offset / halfNumbersColumnHeightPx).toInt()
    return maxOf(0, minOf(indexOf, range.count() - 1))
}

private suspend fun Animatable<Float, AnimationVector1D>.fling(
    initialVelocity: Float,
    animationSpec: DecayAnimationSpec<Float>,
    adjustTarget: ((Float) -> Float)?,
    block: (Animatable<Float, AnimationVector1D>.() -> Unit)? = null,
): AnimationResult<Float, AnimationVector1D> {
    val targetValue = animationSpec.calculateTargetValue(value, initialVelocity)
    val adjustedTarget = adjustTarget?.invoke(targetValue)
    return if (adjustedTarget != null) {
        animateTo(
            targetValue = adjustedTarget,
            initialVelocity = initialVelocity,
            block = block
        )
    } else {
        animateDecay(
            initialVelocity = initialVelocity,
            animationSpec = animationSpec,
            block = block
        )
    }
}

private const val MINIMUM_ALPHA = 0.3f
