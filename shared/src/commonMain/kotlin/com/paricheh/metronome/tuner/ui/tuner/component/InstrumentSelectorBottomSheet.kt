package com.paricheh.metronome.tuner.ui.tuner.component

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.automirrored.rounded.KeyboardArrowRight
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.composeunstyled.ModalBottomSheetState
import com.composeunstyled.Scrim
import com.composeunstyled.Sheet
import com.composeunstyled.SheetDetent
import com.composeunstyled.UnstyledModalBottomSheet
import com.paricheh.metronome.tuner.ui.utils.instrument.Guitar6String
import com.paricheh.metronome.tuner.ui.utils.instrument.Instrument
import com.paricheh.metronome.tuner.ui.utils.instrument.Piano88
import com.paricheh.metronome.tuner.ui.utils.instrument.Setar
import com.paricheh.metronome.tuner.ui.utils.instrument.getTypeText
import metronome.shared.generated.resources.Res
import metronome.shared.generated.resources.choose_instrument
import metronome.shared.generated.resources.guitar
import metronome.shared.generated.resources.piano
import metronome.shared.generated.resources.setar
import metronome.shared.generated.resources.tuning_count_format
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource

@Immutable
private data class InstrumentGroup(
    val titleRes: StringResource,
    val instruments: List<Instrument>,
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InstrumentSelectorBottomSheet(
    state: ModalBottomSheetState,
    instruments: List<Instrument>,
    modifier: Modifier = Modifier,
    currentInstrument: Instrument? = null,
    onDismiss: () -> Unit,
    onInstrumentSelected: (Instrument) -> Unit,
) {
    var selectedGroup by remember { mutableStateOf<InstrumentGroup?>(null) }

    // Reset view to main list when bottom sheet is dismissed
    LaunchedEffect(state.currentDetent) {
        if (state.currentDetent == SheetDetent.Hidden) {
            selectedGroup = null
        }
    }

    val groups = remember(instruments) {
        instruments.groupBy { instrument ->
            when (instrument) {
                is Setar -> Res.string.setar
                is Guitar6String -> Res.string.guitar
                is Piano88 -> Res.string.piano
            }
        }.map { (titleRes, groupInstruments) ->
            InstrumentGroup(titleRes, groupInstruments)
        }
    }

    UnstyledModalBottomSheet(
        state = state,
        onDismiss = {
            onDismiss()
        },
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
                    AnimatedContent(
                        targetState = selectedGroup,
                        transitionSpec = {
                            fadeIn() togetherWith fadeOut()
                        }
                    ) { group ->
                        if (group == null) {
                            MainInstrumentList(
                                groups = groups,
                                currentInstrument = currentInstrument,
                                onGroupSelected = { selectedGroup = it },
                                onSingleInstrumentSelected = {
                                    onInstrumentSelected(it)
                                }
                            )
                        } else {
                            SubtypeList(
                                group = group,
                                currentInstrument = currentInstrument,
                                onBackClick = { selectedGroup = null },
                                onSubtypeSelected = {
                                    onInstrumentSelected(it)
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun MainInstrumentList(
    groups: List<InstrumentGroup>,
    currentInstrument: Instrument?,
    onGroupSelected: (InstrumentGroup) -> Unit,
    onSingleInstrumentSelected: (Instrument) -> Unit,
) {
    Column {
        Text(
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp),
            text = stringResource(Res.string.choose_instrument),
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onSurface
        )

        Spacer(modifier = Modifier.height(8.dp))

        groups.forEachIndexed { index, group ->
            val isMultiVariant = group.instruments.size > 1
            val containsCurrent = currentInstrument?.let { curr ->
                group.instruments.any { it.key == curr.key }
            } == true

            Row(
                modifier = Modifier
                    .clickable {
                        if (isMultiVariant) {
                            onGroupSelected(group)
                        } else {
                            onSingleInstrumentSelected(group.instruments.first())
                        }
                    }
                    .padding(horizontal = 24.dp, vertical = 16.dp)
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    Text(
                        text = stringResource(group.titleRes),
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    val subtitleText = when {
                        !isMultiVariant -> stringResource(group.instruments.first().getTypeText())
                        containsCurrent -> stringResource(currentInstrument.getTypeText())
                        else -> stringResource(Res.string.tuning_count_format, group.instruments.size)
                    }

                    Text(
                        text = subtitleText,
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    if (!isMultiVariant && containsCurrent) {
                        Icon(
                            imageVector = Icons.Rounded.Check,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    if (isMultiVariant) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Rounded.KeyboardArrowRight,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }

            if (index != groups.lastIndex) {
                HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
            }
        }
    }
}

@Composable
private fun SubtypeList(
    group: InstrumentGroup,
    currentInstrument: Instrument?,
    onBackClick: () -> Unit,
    onSubtypeSelected: (Instrument) -> Unit,
) {
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            IconButton(onClick = onBackClick) {
                Icon(
                    imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }

            Text(
                text = stringResource(group.titleRes),
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        group.instruments.forEachIndexed { index, instrument ->
            val isSelected = currentInstrument?.key == instrument.key

            Row(
                modifier = Modifier
                    .clickable { onSubtypeSelected(instrument) }
                    .padding(horizontal = 24.dp, vertical = 14.dp)
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = stringResource(instrument.getTypeText()),
                        style = MaterialTheme.typography.titleMedium,
                        color = if (isSelected) {
                            MaterialTheme.colorScheme.primary
                        } else {
                            MaterialTheme.colorScheme.onSurface
                        }
                    )

                    Text(
                        text = instrument.notes.joinToString(", ") { it.note.displayName },
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                if (isSelected) {
                    Icon(
                        imageVector = Icons.Rounded.Check,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            if (index != group.instruments.lastIndex) {
                HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
            }
        }
    }
}
