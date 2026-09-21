package com.paricheh.metronome.tuner.ui.tuner.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.paricheh.metronome.tuner.model.NoteInfo
import com.paricheh.metronome.tuner.ui.utils.instrument.Guitar6String
import metronome.shared.generated.resources.Res
import metronome.shared.generated.resources.acoustic_guitar
import org.jetbrains.compose.resources.painterResource


@Composable
fun Guitar6StringInstrumentSection(
    currentInstrument: Guitar6String,
    selectedNote: NoteInfo?,
    onSelectNote: (NoteInfo?) -> Unit,
) {

    Row(modifier = Modifier.fillMaxWidth()) {
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            currentInstrument.notes.take(3).forEach {
                OutlinedButton(
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = if (selectedNote == it) {
                            MaterialTheme.colorScheme.secondaryContainer
                        } else {
                            Color.Transparent
                        },
                        contentColor = if (selectedNote == it) {
                            MaterialTheme.colorScheme.onSecondaryContainer
                        } else {
                            MaterialTheme.colorScheme.onSurfaceVariant
                        },
                    ),
                    onClick = {
                        onSelectNote(it)
                    }
                ) {
                    Text(text = it.note.displayName)
                }
            }
        }

        Image(
            painter = painterResource(Res.drawable.acoustic_guitar),
            contentDescription = null,
            modifier = Modifier.weight(1f)
        )

        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            currentInstrument.notes.takeLast(3).forEach {
                OutlinedButton(
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = if (selectedNote == it) {
                            MaterialTheme.colorScheme.secondaryContainer
                        } else {
                            Color.Transparent
                        },
                        contentColor = if (selectedNote == it) {
                            MaterialTheme.colorScheme.onSecondaryContainer
                        } else {
                            MaterialTheme.colorScheme.onSurfaceVariant
                        },
                    ),
                    onClick = {
                        onSelectNote(it)
                    }
                ) {
                    Text(text = it.note.displayName)
                }
            }
        }
    }
}