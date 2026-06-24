package com.paricheh.metronome.metronome.ui.setting.component

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
import androidx.compose.runtime.mutableIntStateOf
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
import com.paricheh.metronome.designsystem.NonCommonTypography
import com.paricheh.metronome.designsystem.HorizontalWheelPicker
import com.paricheh.metronome.core.Note
import com.paricheh.metronome.core.TimeSignature
import metronome.shared.generated.resources.Res
import metronome.shared.generated.resources.confirm
import metronome.shared.generated.resources.custom_time_signature
import metronome.shared.generated.resources.denominator
import metronome.shared.generated.resources.numerator
import org.jetbrains.compose.resources.stringResource

@Composable
fun CustomTimeSignaturePicker(
    state: ModalBottomSheetState,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    onConfirm: (TimeSignature?) -> Unit,
) {
    var selectedNumerator by remember { mutableIntStateOf(4) }
    var selectedDenominator by remember { mutableIntStateOf(4) }

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
                        text = stringResource(Res.string.custom_time_signature),
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Spacer(Modifier.height(24.dp))

                    Text(
                        modifier = Modifier.padding(horizontal = 20.dp),
                        text = stringResource(Res.string.numerator),
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    HorizontalWheelPicker(
                        items = List(12) { it + 1 },
                        provideValue = {
                            selectedNumerator
                        },
                        onValueChange = {
                            selectedNumerator = it
                        },
                        modifier = Modifier.fillMaxWidth(),
                        label = {
                            it.toString()
                        },
                        dividersColor = Color.Transparent,
                        textStyle = NonCommonTypography.PersianSonatiNumber,
                    )

                    Spacer(Modifier.height(16.dp))

                    Text(
                        modifier = Modifier.padding(horizontal = 20.dp),
                        text = stringResource(Res.string.denominator),
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    HorizontalWheelPicker(
                        items = Note.timeSignatureUnits.toList(),
                        numbersColumnWidth = 150.dp,
                        provideValue = {
                            selectedDenominator
                        },
                        onValueChange = {
                            selectedDenominator = it
                        },
                        modifier = Modifier.fillMaxWidth(),
                        label = {
                            "${Note(it).getUnitCharByUnit()} $it"
                        },
                        dividersColor = Color.Transparent,
                        textStyle = NonCommonTypography.PersianSonatiNumber,
                    )

                    Spacer(Modifier.height(16.dp))

                    Row(
                        modifier = modifier
                            .padding(horizontal = 20.dp)
                            .fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Button(
                            modifier = Modifier.weight(1f),
                            onClick = {
                                onConfirm(
                                    TimeSignature(
                                        selectedNumerator,
                                        selectedDenominator
                                    )
                                )
                            },
                        ) {
                            Text(stringResource(Res.string.confirm))
                        }

                        OutlinedButton(
                            modifier = Modifier
                                .weight(1f),
                            onClick = onDismiss
                        ) {
                            Text(stringResource(Res.string.confirm))
                        }
                    }
                }
            }
        }
    }
}
