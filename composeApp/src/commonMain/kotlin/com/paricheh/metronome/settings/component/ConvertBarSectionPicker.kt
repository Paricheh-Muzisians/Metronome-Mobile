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

@Composable
fun <T> ConvertBarSectionPicker(
    state: ModalBottomSheetState,
    title: String,
    items: List<T>,
    description: String? = null,
    onDismiss: () -> Unit,
    provideValue: () -> T,
    onValueChange: (T) -> Unit,
    modifier: Modifier = Modifier,
    label: (T) -> String = { it.toString() },
    onConfirm: () -> Unit,
) {
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
                        text = title,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    description?.let {
                        Spacer(Modifier.height(8.dp))
                        Text(
                            text = it,
                            modifier = Modifier.padding(horizontal = 20.dp),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Spacer(Modifier.height(16.dp))

                    HorizontalWheelPicker(
                        items = items,
                        provideValue = provideValue,
                        onValueChange = onValueChange,
                        modifier = Modifier.fillMaxWidth(),
                        label = label,
                        dividersColor = Color.Transparent,
                        textStyle = NonCommonTypography.musicFontLarge,
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
                            onClick = onConfirm,
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
