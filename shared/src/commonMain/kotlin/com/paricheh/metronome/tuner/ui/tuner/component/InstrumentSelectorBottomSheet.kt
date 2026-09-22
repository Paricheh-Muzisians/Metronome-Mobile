package com.paricheh.metronome.tuner.ui.tuner.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.composeunstyled.ModalBottomSheetState
import com.composeunstyled.Scrim
import com.composeunstyled.Sheet
import com.composeunstyled.UnstyledModalBottomSheet
import com.paricheh.metronome.tuner.ui.utils.instrument.Instrument
import com.paricheh.metronome.tuner.ui.utils.instrument.getTitle
import com.paricheh.metronome.tuner.ui.utils.instrument.getTypeText
import metronome.shared.generated.resources.Res
import metronome.shared.generated.resources.choose_note
import org.jetbrains.compose.resources.stringResource


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InstrumentSelectorBottomSheet(
    state: ModalBottomSheetState,
    instruments: List<Instrument>,
    modifier: Modifier = Modifier,
    onDismiss: () -> Unit,
    onInstrumentSelected: (Instrument) -> Unit,
) {
    UnstyledModalBottomSheet(
        state = state,
        onDismiss = onDismiss,
        overlay = { Scrim() }
    ) {
        Sheet {
            CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                Column(
                    modifier = modifier
                        .clip(
                            MaterialTheme.shapes.extraLarge.copy(
                                bottomEnd = CornerSize(0.dp),
                                bottomStart = CornerSize(0.dp)
                            )
                        )
                        .background(MaterialTheme.colorScheme.surfaceContainer)
                        .navigationBarsPadding()
                        .padding(vertical = 16.dp)
                ) {
                    Text(
                        modifier = Modifier.padding(horizontal = 20.dp),
                        text = "انتخاب ساز",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    instruments.forEachIndexed { index, instrument ->
                        Row(
                            modifier = Modifier
                                .clickable(
                                    onClick = {
                                        onInstrumentSelected(instrument)
                                    }
                                )
                                .padding(
                                    horizontal = 24.dp,
                                    vertical = 16.dp
                                )
                                .fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = stringResource(instrument.getTitle()),
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onSurface
                            )

                            Text(
                                text = stringResource(instrument.getTypeText()),
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        if (index != instruments.lastIndex) {
                            HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
                        }
                    }
                }
            }
        }
    }
}