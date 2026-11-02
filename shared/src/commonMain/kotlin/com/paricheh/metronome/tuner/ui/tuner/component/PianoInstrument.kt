package com.paricheh.metronome.tuner.ui.tuner.component

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateColor
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateDp
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.paricheh.metronome.tuner.model.MusicalNote
import com.paricheh.metronome.tuner.model.NoteInfo
import com.paricheh.metronome.tuner.model.isSameNote
import com.paricheh.metronome.tuner.ui.utils.instrument.Instrument
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import metronome.shared.generated.resources.Res
import metronome.shared.generated.resources.select_piano_key
import org.jetbrains.compose.resources.stringResource
import kotlin.math.roundToInt

private val WhiteKeyWidth = 48.dp
private val WhiteKeyHeight = 200.dp
private val BlackKeyWidth = 30.dp
private val BlackKeyHeight = 120.dp

private data class PianoKeyModel(
    val noteInfo: NoteInfo,
    val isBlack: Boolean,
    val whiteIndex: Int,
    val leftWhiteIndex: Int,
)

private fun MusicalNote.isBlack(): Boolean = when (this) {
    MusicalNote.DoSharp,
    MusicalNote.ReSharp,
    MusicalNote.FaSharp,
    MusicalNote.SolSharp,
    MusicalNote.LaSharp,
        -> true

    else -> false
}

private fun Int.toSubscript(): String = when (this) {
    0 -> "₀"
    1 -> "₁"
    2 -> "₂"
    3 -> "₃"
    4 -> "₄"
    5 -> "₅"
    6 -> "₆"
    7 -> "₇"
    8 -> "₈"
    else -> toString()
}

@Composable
internal fun PianoInstrument(
    currentInstrument: Instrument,
    selectedNote: NoteInfo?,
    onSelectNote: (NoteInfo?) -> Unit,
) {
    val coroutineScope = rememberCoroutineScope()
    val scrollState = rememberScrollState()
    val density = LocalDensity.current

    val whiteKeyWidthPx = with(density) { WhiteKeyWidth.toPx() }
    val blackKeyWidthPx = with(density) { BlackKeyWidth.toPx() }

    var containerWidthPx by remember { mutableStateOf(0) }
    var hasInitiallyScrolled by remember { mutableStateOf(false) }

    val keyModels = remember(currentInstrument.notes) {
        var currentWhiteIndex = 0
        currentInstrument.notes.map { noteInfo ->
            val isBlack = noteInfo.note.isBlack()
            if (isBlack) {
                PianoKeyModel(
                    noteInfo = noteInfo,
                    isBlack = true,
                    whiteIndex = -1,
                    leftWhiteIndex = currentWhiteIndex - 1
                )
            } else {
                val model = PianoKeyModel(
                    noteInfo = noteInfo,
                    isBlack = false,
                    whiteIndex = currentWhiteIndex,
                    leftWhiteIndex = -1
                )
                currentWhiteIndex++
                model
            }
        }
    }

    val whiteKeys = remember(keyModels) { keyModels.filter { !it.isBlack } }
    val blackKeys = remember(keyModels) { keyModels.filter { it.isBlack } }

    val activeKeyIndex = remember(keyModels, selectedNote) {
        if (selectedNote == null) -1
        else keyModels.indexOfFirst { it.noteInfo.isSameNote(selectedNote) }
    }

    val activeKeyModel = remember(keyModels, activeKeyIndex) {
        if (activeKeyIndex in keyModels.indices) keyModels[activeKeyIndex] else null
    }

    val availableOctaves = remember(keyModels) {
        keyModels.map { it.noteInfo.octave }.distinct().sorted()
    }

    val currentVisibleOctave by remember {
        derivedStateOf {
            activeKeyModel?.noteInfo?.octave ?: run {
                if (containerWidthPx > 0 && whiteKeyWidthPx > 0) {
                    val centerWhiteIdx =
                        ((scrollState.value + containerWidthPx / 2f) / whiteKeyWidthPx)
                            .toInt()
                            .coerceIn(0, (whiteKeys.size - 1).coerceAtLeast(0))
                    whiteKeys.getOrNull(centerWhiteIdx)?.noteInfo?.octave ?: 4
                } else 4
            }
        }
    }

    // Initial positioning: Center around Middle C (Do4) or selected note
    LaunchedEffect(containerWidthPx, scrollState.maxValue) {
        if (!hasInitiallyScrolled && containerWidthPx > 0 && scrollState.maxValue > 0) {
            val targetNote = selectedNote ?: keyModels.firstOrNull {
                it.noteInfo.note == MusicalNote.Do && it.noteInfo.octave == 4
            }?.noteInfo
            if (targetNote != null) {
                val key = keyModels.firstOrNull { it.noteInfo.isSameNote(targetNote) }
                if (key != null) {
                    val keyCenterX = if (key.isBlack) {
                        (key.leftWhiteIndex + 1f) * whiteKeyWidthPx
                    } else {
                        (key.whiteIndex + 0.5f) * whiteKeyWidthPx
                    }
                    val targetScroll = (keyCenterX - containerWidthPx / 2f)
                        .roundToInt()
                        .coerceIn(0, scrollState.maxValue)
                    scrollState.scrollTo(targetScroll)
                    hasInitiallyScrolled = true
                }
            }
        }
    }

    // Auto-scroll when selected note changes and is outside comfortable viewing area
    LaunchedEffect(selectedNote, containerWidthPx) {
        if (selectedNote != null && containerWidthPx > 0 && scrollState.maxValue > 0) {
            val key = keyModels.firstOrNull { it.noteInfo.isSameNote(selectedNote) }
            if (key != null) {
                val keyCenterX = if (key.isBlack) {
                    (key.leftWhiteIndex + 1f) * whiteKeyWidthPx
                } else {
                    (key.whiteIndex + 0.5f) * whiteKeyWidthPx
                }
                val visibleStart = scrollState.value
                val visibleEnd = scrollState.value + containerWidthPx
                val margin = with(density) { 48.dp.toPx() }

                if (keyCenterX < visibleStart + margin || keyCenterX > visibleEnd - margin) {
                    val targetScroll = (keyCenterX - containerWidthPx / 2f)
                        .roundToInt()
                        .coerceIn(0, scrollState.maxValue)
                    scrollState.animateScrollTo(
                        targetScroll,
                        animationSpec = tween(durationMillis = 380, easing = FastOutSlowInEasing)
                    )
                }
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp),
    ) {
        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
            // 1. HUD: Note Spotlight & Information
            PianoSpotlightHud(
                selectedNote = selectedNote,
                totalNotes = currentInstrument.notes.size,
                activeKeyIndex = activeKeyIndex,
            )

            // 2. 88-Key Minimap & Viewport Indicator (Always LTR from low to high pitch)
            PianoMinimap(
                whiteKeys = whiteKeys,
                blackKeys = blackKeys,
                activeKeyModel = activeKeyModel,
                scrollState = scrollState,
                containerWidthPx = containerWidthPx,
                whiteKeyWidthPx = whiteKeyWidthPx,
                coroutineScope = coroutineScope,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(40.dp)
            )

            // 3. Octave Quick-Jump Selector
            PianoOctaveSelector(
                octaves = availableOctaves,
                activeOctave = currentVisibleOctave,
                onSelectOctave = { targetOctave ->
                    val octaveKeys = keyModels.filter { it.noteInfo.octave == targetOctave }
                    if (octaveKeys.isNotEmpty()) {
                        val firstKey = octaveKeys.first()
                        val lastKey = octaveKeys.last()
                        val firstX = if (firstKey.isBlack) {
                            (firstKey.leftWhiteIndex + 1f) * whiteKeyWidthPx - (blackKeyWidthPx / 2f)
                        } else {
                            firstKey.whiteIndex * whiteKeyWidthPx
                        }
                        val lastX = if (lastKey.isBlack) {
                            (lastKey.leftWhiteIndex + 1f) * whiteKeyWidthPx + (blackKeyWidthPx / 2f)
                        } else {
                            (lastKey.whiteIndex + 1f) * whiteKeyWidthPx
                        }
                        val octaveCenterX = (firstX + lastX) / 2f
                        val targetScroll = (octaveCenterX - containerWidthPx / 2f)
                            .roundToInt()
                            .coerceIn(0, scrollState.maxValue)
                        coroutineScope.launch {
                            scrollState.animateScrollTo(
                                targetScroll,
                                animationSpec = tween(
                                    durationMillis = 380,
                                    easing = FastOutSlowInEasing
                                )
                            )
                        }
                    }
                }
            )

            // 4. Interactive Tactile Piano Keyboard (Always LTR)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(bottomStart = 8.dp, bottomEnd = 8.dp))
                    .onSizeChanged { containerWidthPx = it.width }
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(scrollState)
                ) {
                    // Layer 1: White Keys
                    Row {
                        whiteKeys.forEach { whiteKeyModel ->
                            val isSelected = whiteKeyModel.noteInfo.isSameNote(selectedNote)
                            PianoWhiteKey(
                                keyModel = whiteKeyModel,
                                isSelected = isSelected,
                                width = WhiteKeyWidth,
                                height = WhiteKeyHeight,
                                onClick = { onSelectNote(whiteKeyModel.noteInfo) }
                            )
                        }
                    }

                    // Layer 2: Black Keys (Drawn over white keys, with elevated depth)
                    blackKeys.forEach { blackKeyModel ->
                        val isSelected = blackKeyModel.noteInfo.isSameNote(selectedNote)
                        val offsetX =
                            (WhiteKeyWidth * (blackKeyModel.leftWhiteIndex + 1)) - (BlackKeyWidth / 2)
                        PianoBlackKey(
                            keyModel = blackKeyModel,
                            isSelected = isSelected,
                            width = BlackKeyWidth,
                            height = BlackKeyHeight,
                            offsetX = offsetX,
                            onClick = { onSelectNote(blackKeyModel.noteInfo) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun PianoSpotlightHud(
    selectedNote: NoteInfo?,
    totalNotes: Int,
    activeKeyIndex: Int,
) {
    val surfaceVariantColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
    AnimatedContent(
        modifier = Modifier
            .drawBehind {
                drawRoundRect(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color.Transparent,
                            surfaceVariantColor,
                        )
                    )
                )
            }
            .padding(bottom = 12.dp),
        targetState = selectedNote,
        transitionSpec = { fadeIn(tween(200)) togetherWith fadeOut(tween(200)) }
    ) { note ->
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .padding(horizontal = 20.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            if (note != null) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = "${note.note.displayName}${note.octave.toSubscript()}",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.8f))
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = "${(note.frequency * 10).roundToInt() / 10f} Hz",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                    }

                    if (activeKeyIndex >= 0) {
                        Text(
                            text = "${activeKeyIndex + 1} / $totalNotes",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun PianoMinimap(
    whiteKeys: List<PianoKeyModel>,
    blackKeys: List<PianoKeyModel>,
    activeKeyModel: PianoKeyModel?,
    scrollState: androidx.compose.foundation.ScrollState,
    containerWidthPx: Int,
    whiteKeyWidthPx: Float,
    coroutineScope: CoroutineScope,
    modifier: Modifier = Modifier,
) {
    val primaryColor = MaterialTheme.colorScheme.primary
    val viewportFillColor = remember(primaryColor) { primaryColor.copy(alpha = 0.16f) }
    val viewportBorderColor = remember(primaryColor) { primaryColor.copy(alpha = 0.85f) }
    val activeIndicatorColor = MaterialTheme.colorScheme.primary

    Canvas(
        modifier = modifier
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
            .padding(horizontal = 20.dp)
            .clip(RoundedCornerShape(6.dp))
            .background(Color(0xFF141414))
            .border(0.8.dp, Color(0xFF262626), RoundedCornerShape(6.dp))
            .pointerInput(scrollState.maxValue) {
                detectDragGestures(
                    onDragStart = { offset ->
                        val fraction = (offset.x / size.width).coerceIn(0f, 1f)
                        val target = (fraction * scrollState.maxValue).roundToInt()
                        coroutineScope.launch { scrollState.animateScrollTo(target) }
                    },
                    onDrag = { change, _ ->
                        change.consume()
                        val fraction = (change.position.x / size.width).coerceIn(0f, 1f)
                        val target = (fraction * scrollState.maxValue).roundToInt()
                        coroutineScope.launch { scrollState.scrollTo(target) }
                    }
                )
            }
    ) {
        val totalWhite = whiteKeys.size.coerceAtLeast(1)
        val miniWhiteWidth = size.width / totalWhite

        // White key dividers
        for (i in 0 until totalWhite) {
            val x = i * miniWhiteWidth
            drawLine(
                color = Color(0xFF242424),
                start = Offset(x, 0f),
                end = Offset(x, size.height),
                strokeWidth = 1f
            )
        }

        // Black key mini-bars
        val miniBlackWidth = (miniWhiteWidth * 0.72f).coerceAtLeast(1.5f)
        val miniBlackHeight = size.height * 0.60f
        blackKeys.forEach { blackKey ->
            val centerX = (blackKey.leftWhiteIndex + 1f) * miniWhiteWidth
            drawRoundRect(
                color = Color(0xFF383838),
                topLeft = Offset(centerX - miniBlackWidth / 2f, 0f),
                size = Size(miniBlackWidth, miniBlackHeight),
                cornerRadius = CornerRadius(1.5f, 1.5f)
            )
        }

        // Active note glowing indicator
        activeKeyModel?.let { activeKey ->
            val activeX = if (activeKey.isBlack) {
                (activeKey.leftWhiteIndex + 1f) * miniWhiteWidth
            } else {
                (activeKey.whiteIndex + 0.5f) * miniWhiteWidth
            }

            drawLine(
                color = activeIndicatorColor,
                start = Offset(activeX, 0f),
                end = Offset(activeX, size.height),
                strokeWidth = 2.dp.toPx()
            )
            drawCircle(
                color = activeIndicatorColor,
                radius = 3.dp.toPx(),
                center = Offset(activeX, size.height - 3.5.dp.toPx())
            )
        }

        // Viewport Window
        val totalContentWidthPx = totalWhite * whiteKeyWidthPx
        if (totalContentWidthPx > 0 && containerWidthPx > 0) {
            val viewportRatio = (containerWidthPx / totalContentWidthPx).coerceIn(0.05f, 1f)
            val viewportIndicatorWidth = size.width * viewportRatio
            val maxScroll = (totalContentWidthPx - containerWidthPx).coerceAtLeast(1f)
            val scrollProgress = (scrollState.value.toFloat() / maxScroll).coerceIn(0f, 1f)
            val indicatorLeft = scrollProgress * (size.width - viewportIndicatorWidth)

            drawRoundRect(
                color = viewportFillColor,
                topLeft = Offset(indicatorLeft, 1f),
                size = Size(viewportIndicatorWidth, size.height - 2f),
                cornerRadius = CornerRadius(4.dp.toPx(), 4.dp.toPx())
            )
            drawRoundRect(
                color = viewportBorderColor,
                topLeft = Offset(indicatorLeft, 1f),
                size = Size(viewportIndicatorWidth, size.height - 2f),
                cornerRadius = CornerRadius(4.dp.toPx(), 4.dp.toPx()),
                style = Stroke(width = 1.2.dp.toPx())
            )
        }
    }
}

@Composable
private fun PianoOctaveSelector(
    octaves: List<Int>,
    activeOctave: Int,
    onSelectOctave: (Int) -> Unit,
) {
    Row(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
            .padding(end = 20.dp)
            .padding(vertical = 20.dp)
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(
            modifier = Modifier
                .weight(1f)
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(6.dp, Alignment.CenterHorizontally),
            verticalAlignment = Alignment.CenterVertically
        ) {
            octaves.forEach { octave ->
                val isSelected = octave == activeOctave

                if (octaves.first() == octave) {
                    Spacer(Modifier.width(20.dp))
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(
                            if (isSelected) {
                                MaterialTheme.colorScheme.primaryContainer
                            } else {
                                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                            }
                        )
                        .border(
                            width = 1.dp,
                            color = if (isSelected) {
                                MaterialTheme.colorScheme.primary
                            } else {
                                MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f)
                            },
                            shape = RoundedCornerShape(8.dp)
                        )
                        .clickable { onSelectOctave(octave) }
                        .padding(horizontal = 12.dp, vertical = 4.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(3.dp)
                    ) {
                        Text(
                            text = "$octave",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                            ),
                            color = if (isSelected) {
                                MaterialTheme.colorScheme.onPrimaryContainer
                            } else {
                                MaterialTheme.colorScheme.onSurfaceVariant
                            }
                        )
                        if (octave == 4) {
                            Box(
                                modifier = Modifier
                                    .size(4.dp)
                                    .clip(CircleShape)
                                    .background(
                                        if (isSelected) MaterialTheme.colorScheme.primary
                                        else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                                    )
                            )
                        }
                    }
                }
            }
        }

        Text(
            text = "اُکتاو",
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun PianoWhiteKey(
    keyModel: PianoKeyModel,
    isSelected: Boolean,
    width: Dp,
    height: Dp,
    onClick: () -> Unit,
) {
    val transition = updateTransition(isSelected, label = "WhiteKeyTransition")
    val animatedBg by transition.animateColor(label = "bg") {
        if (it) MaterialTheme.colorScheme.primaryContainer else Color(0xFFECE7DE)
    }
    val animatedTextColor by transition.animateColor(label = "text") {
        if (it) MaterialTheme.colorScheme.onPrimaryContainer else Color(0xFF2C2C2C)
    }
    val animatedTranslationY by transition.animateDp(label = "depression") {
        if (it) 2.5.dp else 0.dp
    }

    Box(
        modifier = Modifier
            .width(width)
            .height(height)
            .graphicsLayer {
                translationY = animatedTranslationY.toPx()
            }
            .clip(RoundedCornerShape(bottomStart = 5.dp, bottomEnd = 5.dp))
            .background(
                Brush.verticalGradient(
                    listOf(
                        if (isSelected) MaterialTheme.colorScheme.primaryContainer else Color(
                            0xFFDED8CE
                        ),
                        animatedBg,
                        animatedBg,
                    )
                )
            )
            .border(
                width = 0.6.dp,
                color = Color(0xFF1E1E1E),
                shape = RoundedCornerShape(bottomStart = 5.dp, bottomEnd = 5.dp)
            )
            .clickable(onClick = onClick)
            .padding(bottom = 6.dp),
        contentAlignment = Alignment.BottomCenter
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            if (isSelected) {
                Box(
                    modifier = Modifier
                        .size(4.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary)
                )
            }
            Text(
                text = if (keyModel.noteInfo.note == MusicalNote.Do) {
                    "${keyModel.noteInfo.note.displayName}${keyModel.noteInfo.octave.toSubscript()}"
                } else {
                    keyModel.noteInfo.note.displayName
                },
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = if (isSelected || keyModel.noteInfo.note == MusicalNote.Do) {
                        FontWeight.Bold
                    } else {
                        FontWeight.Medium
                    }
                ),
                color = animatedTextColor
            )
        }
    }
}

@Composable
private fun PianoBlackKey(
    keyModel: PianoKeyModel,
    isSelected: Boolean,
    width: Dp,
    height: Dp,
    offsetX: Dp,
    onClick: () -> Unit,
) {
    val transition = updateTransition(isSelected, label = "BlackKeyTransition")
    val animatedTranslationY by transition.animateDp(label = "depression") {
        if (it) 2.dp else 0.dp
    }
    val borderColor by transition.animateColor(label = "border") {
        if (it) MaterialTheme.colorScheme.primary else Color(0xFF383838)
    }

    Box(
        modifier = Modifier
            .offset(x = offsetX)
            .width(width)
            .height(height)
            .graphicsLayer {
                translationY = animatedTranslationY.toPx()
            }
            .shadow(
                elevation = 4.dp,
                shape = RoundedCornerShape(bottomStart = 4.dp, bottomEnd = 4.dp)
            )
            .clip(RoundedCornerShape(bottomStart = 4.dp, bottomEnd = 4.dp))
            .background(
                Brush.verticalGradient(
                    if (isSelected) {
                        listOf(Color(0xFF3E3A33), Color(0xFF282520))
                    } else {
                        listOf(Color(0xFF2B2B2B), Color(0xFF161616), Color(0xFF0F0F0F))
                    }
                )
            )
            .border(
                width = if (isSelected) 1.2.dp else 0.5.dp,
                color = borderColor,
                shape = RoundedCornerShape(bottomStart = 4.dp, bottomEnd = 4.dp)
            )
            .clickable(onClick = onClick)
            .padding(bottom = 6.dp),
        contentAlignment = Alignment.BottomCenter
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            if (isSelected) {
                Box(
                    modifier = Modifier
                        .size(3.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary)
                )
            }
            Text(
                text = keyModel.noteInfo.note.displayName,
                style = MaterialTheme.typography.labelSmall.copy(
                    fontSize = 8.sp,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                ),
                color = if (isSelected) MaterialTheme.colorScheme.primary else Color(0xFF888888)
            )
        }
    }
}
