package com.paricheh.metronome.tuner.ui.tuner.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.paricheh.metronome.tuner.model.NoteInfo
import com.paricheh.metronome.tuner.model.isSameNote
import com.paricheh.metronome.tuner.ui.utils.instrument.Instrument
import metronome.shared.generated.resources.Res
import metronome.shared.generated.resources.setar_headstock
import org.jetbrains.compose.resources.painterResource

@Composable
internal fun SetarInstrumentSection(
    currentInstrument: Instrument,
    selectedNote: NoteInfo?,
    onSelectNote: (NoteInfo?) -> Unit,
) {
    Row(
        modifier = Modifier
            .padding(horizontal = 8.dp)
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.Center
    ) {
        Column(
            modifier = Modifier.padding(top = 58.dp),
            verticalArrangement = Arrangement.spacedBy(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            currentInstrument.notes.take(2)
                .reversed()
                .forEach {
                    NoteButtons(
                        name = it.note.displayName,
                        isSelected = selectedNote.isSameNote(it),
                        onClick = {
                            onSelectNote(it)
                        }
                    )
                }
        }

        Image(
            painter = painterResource(Res.drawable.setar_headstock),
            contentDescription = null,
            modifier = Modifier.padding(horizontal = 24.dp)
        )

        Column(
            modifier = Modifier.padding(top = 42.dp),
            verticalArrangement = Arrangement.spacedBy(28.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            currentInstrument.notes
                .takeLast(2)
                .forEach {
                    NoteButtons(
                        name = it.note.displayName,
                        isSelected = selectedNote.isSameNote(it),
                        onClick = {
                            onSelectNote(it)
                        }
                    )
                }
        }
    }
}

@Composable
private fun NoteButtons(
    name: String,
    isSelected: Boolean,
    onClick: () -> Unit,
) {
    Box(
        modifier = Modifier
            .clip(CircleShape)
            .border(
                width = 1.dp,
                color = if (isSelected) {
                    MaterialTheme.colorScheme.onSecondaryContainer
                } else {
                    MaterialTheme.colorScheme.surfaceContainerHighest
                },
                shape = CircleShape
            )
            .clickable(
                onClick = onClick,
            )
            .size(48.dp)
            .background(
                if (isSelected) {
                    MaterialTheme.colorScheme.secondaryContainer
                } else {
                    Color.Transparent
                }
            ),
    ) {
        Text(
            modifier = Modifier.align(Alignment.Center),
            text = name,
            style = MaterialTheme.typography.labelMedium,
            color = if (isSelected) {
                MaterialTheme.colorScheme.onSecondaryContainer
            } else {
                MaterialTheme.colorScheme.onSurfaceVariant
            },
        )
    }
}
