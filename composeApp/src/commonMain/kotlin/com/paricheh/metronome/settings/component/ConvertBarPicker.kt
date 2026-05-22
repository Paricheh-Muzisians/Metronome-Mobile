package com.paricheh.metronome.settings.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.composables.core.ModalBottomSheet
import com.composables.core.ModalBottomSheetState
import com.composables.core.Scrim
import com.composables.core.Sheet
import com.paricheh.metronome.theme.NonCommonTypography
import com.paricheh.metronome.utils.Note
import metronome.composeapp.generated.resources.Res
import metronome.composeapp.generated.resources.choose_note
import metronome.composeapp.generated.resources.convert_time_signature_message
import org.jetbrains.compose.resources.stringResource

@Composable
fun ConvertBarPicker(
    state: ModalBottomSheetState,
    currentConvertedUnit: Note?,
    currentTimeSignatureUnit: Note,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    onConfirm: (Note) -> Unit,
) {
    var selectedNote by remember {
        mutableStateOf(
            currentConvertedUnit ?: currentTimeSignatureUnit
        )
    }

    ModalBottomSheet(
        state = state,
        onDismiss = onDismiss
    ) {
        Scrim()

        Sheet(
            backgroundColor = MaterialTheme.colorScheme.surfaceContainer,
            shape = MaterialTheme.shapes.extraLarge
        ) {
            CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                Column(
                    modifier = modifier
                        .navigationBarsPadding()
                        .padding(vertical = 16.dp),
                ) {
                    Text(
                        modifier = Modifier.padding(horizontal = 20.dp),
                        text = stringResource(Res.string.choose_note),
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Spacer(Modifier.height(8.dp))

                    Text(
                        text = stringResource(Res.string.convert_time_signature_message),
                        modifier = Modifier.padding(horizontal = 20.dp),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(Modifier.height(16.dp))

                    HorizontalWheelPicker(
                        items = Note.validNoteWeights
                            .filter { it >= currentTimeSignatureUnit.weight }
                            .map { Note(it) }
                            .toList(),
                        provideValue = { selectedNote },
                        onValueChange = { selectedNote = it },
                        modifier = Modifier.fillMaxWidth(),
                        label = { it.getUnitCharByUnit() },
                        dividersColor = Color.Transparent,
                        textStyle = NonCommonTypography.PersianSonatiNumber,
                    )

                    Spacer(Modifier.height(24.dp))

                    Row(
                        modifier = modifier
                            .padding(horizontal = 20.dp)
                            .fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Button(
                            modifier = Modifier
                                .weight(1f),
                            onClick = { onConfirm(selectedNote) },
                        ) {
                            Text("تایید")
                        }

                        OutlinedButton(
                            modifier = Modifier
                                .weight(1f),
                            onClick = onDismiss
                        ) {
                            Text("انصراف")
                        }
                    }
                }
            }
        }
    }
}
