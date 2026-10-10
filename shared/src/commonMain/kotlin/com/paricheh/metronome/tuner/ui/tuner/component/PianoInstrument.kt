package com.paricheh.metronome.tuner.ui.tuner.component

import androidx.compose.animation.animateColor
import androidx.compose.animation.core.animateDp
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.paricheh.metronome.tuner.model.NoteInfo
import com.paricheh.metronome.tuner.model.isSameNote
import com.paricheh.metronome.tuner.ui.utils.instrument.Instrument
import kotlin.math.roundToInt

@Composable
internal fun PianoInstrument(
    currentInstrument: Instrument,
    selectedNote: NoteInfo?,
    onSelectNote: (NoteInfo?) -> Unit,
) {
    val surfaceColor = MaterialTheme.colorScheme.surface
    val lazyState = rememberLazyListState()

    val selectedIndex = remember(selectedNote, currentInstrument.notes) {
        if (selectedNote == null) -1
        else currentInstrument.notes.indexOfFirst { it.isSameNote(selectedNote) }
    }

    LaunchedEffect(selectedIndex) {
        if (selectedIndex >= 0) {
            lazyState.animateScrollToItem(selectedIndex)
        }
    }

    LazyColumn(
        state = lazyState,
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
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        stickyHeader {
            Column(
                modifier = Modifier.fillMaxWidth()
                    .background(
                        brush = Brush.verticalGradient(
                            listOf(
                                surfaceColor,
                                surfaceColor,
                                Color.Transparent,
                            )
                        ),
                    )
                    .padding(vertical = 24.dp)
            ) {
                HorizontalDivider()

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "انتخاب کلاویه",
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onSurface,
                    textAlign = TextAlign.Start
                )
            }
        }

        itemsIndexed(currentInstrument.notes) { index, note ->
            val isSelectedTransition = updateTransition(note.isSameNote(selectedNote))
            val backgroundColor by isSelectedTransition.animateColor {
                if (it) {
                    MaterialTheme.colorScheme.primaryContainer
                } else {
                    MaterialTheme.colorScheme.secondaryContainer
                }
            }

            val textColor by isSelectedTransition.animateColor {
                if (it) {
                    MaterialTheme.colorScheme.onPrimaryContainer
                } else {
                    MaterialTheme.colorScheme.onSecondaryContainer
                }
            }

            val verticalPadding by isSelectedTransition.animateDp {
                if (it) {
                    20.dp
                } else {
                    16.dp
                }
            }

            Row(
                modifier = Modifier
                    .padding(bottom = 16.dp)
                    .clip(MaterialTheme.shapes.large)
                    .background(backgroundColor)
                    .clickable {
                        onSelectNote(note)
                    }
                    .padding(vertical = verticalPadding)
                    .padding(horizontal = 16.dp)
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Text(
                    text = (index + 1).toString(),
                    style = MaterialTheme.typography.titleSmall,
                    color = textColor
                )

                Text(
                    text = note.note.displayName,
                    style = MaterialTheme.typography.titleSmall,
                    color = textColor
                )

                Text(
                    text = "${note.frequency.roundToInt()} Hz",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
