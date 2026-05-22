package com.paricheh.metronome.settings

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.composables.core.SheetDetent
import com.composables.core.rememberModalBottomSheetState
import com.paricheh.metronome.data.MetronomePreferences
import com.paricheh.metronome.settings.component.ConvertBarPicker
import com.paricheh.metronome.settings.component.CustomTimeSignaturePicker
import com.paricheh.metronome.theme.MetronomeTheme
import com.paricheh.metronome.theme.NonCommonTypography
import com.paricheh.metronome.utils.MusicSymbols
import com.paricheh.metronome.utils.Note
import com.paricheh.metronome.utils.TimeSignature
import com.paricheh.metronome.utils.TimeSignatureType
import kotlinx.coroutines.launch
import metronome.composeapp.generated.resources.Res
import metronome.composeapp.generated.resources.bpm_format
import metronome.composeapp.generated.resources.cd_back
import metronome.composeapp.generated.resources.cd_decrease_tempo
import metronome.composeapp.generated.resources.choose_unit_note
import metronome.composeapp.generated.resources.compound_time_signature
import metronome.composeapp.generated.resources.convert_bar
import metronome.composeapp.generated.resources.convert_bar_message
import metronome.composeapp.generated.resources.custom
import metronome.composeapp.generated.resources.decrement
import metronome.composeapp.generated.resources.detect_tempo
import metronome.composeapp.generated.resources.detect_tempo_message
import metronome.composeapp.generated.resources.increment
import metronome.composeapp.generated.resources.irregular_time_signature
import metronome.composeapp.generated.resources.message_compound_time_signature_warning
import metronome.composeapp.generated.resources.message_irregular_time_signature_warning
import metronome.composeapp.generated.resources.settings_title
import metronome.composeapp.generated.resources.tap_here
import metronome.composeapp.generated.resources.tempo
import metronome.composeapp.generated.resources.tempo_max
import metronome.composeapp.generated.resources.tempo_min
import metronome.composeapp.generated.resources.time_signature
import metronome.composeapp.generated.resources.time_signature_hint
import metronome.composeapp.generated.resources.unselected
import metronome.composeapp.generated.resources.vibration
import metronome.composeapp.generated.resources.vibration_desc
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    navController: NavController,
    viewModel: SettingsViewModel = koinViewModel(),
) {
    val scope = rememberCoroutineScope()
    val preferences by viewModel.preferences.collectAsStateWithLifecycle()
    val selectedTimeSignature = preferences?.selectedTimeSignature

    val barStructurePickerSheetState = rememberModalBottomSheetState(SheetDetent.Hidden)
    val customTimeSignaturePickerSheetState = rememberModalBottomSheetState(SheetDetent.Hidden)

    CustomTimeSignaturePicker(
        state = customTimeSignaturePickerSheetState,
        onDismiss = {
            scope.launch {
                customTimeSignaturePickerSheetState.animateTo(SheetDetent.Hidden)
            }
        },
        onConfirm = {
            scope.launch {
                viewModel.updateTimeSignature(it)
                customTimeSignaturePickerSheetState.animateTo(SheetDetent.Hidden)
            }
        }
    )

    ConvertBarPicker(
        state = barStructurePickerSheetState,
        currentConvertedUnit = preferences?.selectedBarStructure
            ?.lastOrNull(),
        currentTimeSignatureUnit = selectedTimeSignature?.defaultBarsStructure
            ?.lastOrNull()
            ?: Note(4),
        onDismiss = {
            scope.launch {
                barStructurePickerSheetState.animateTo(SheetDetent.Hidden)
            }
        },
        modifier = Modifier.fillMaxWidth(),
        onConfirm = {
            scope.launch {
                if (it == selectedTimeSignature?.defaultBarsStructure?.lastOrNull()) {
                    viewModel.convertBarStructure(null)
                } else {
                    viewModel.convertBarStructure(it)
                }
                barStructurePickerSheetState.animateTo(SheetDetent.Hidden)
            }
        }
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(Res.string.settings_title)) },
                navigationIcon = {
                    IconButton(
                        onClick = { navController.navigateUp() }
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(Res.string.cd_back)
                        )
                    }
                },
            )
        }
    ) { paddingValues ->
        AnimatedContent(
            modifier = Modifier.padding(paddingValues),
            targetState = preferences == null,
            transitionSpec = { fadeIn() togetherWith fadeOut() }
        ) { isLoading ->
            if (isLoading) {
                Box(Modifier.fillMaxSize()) {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
            } else preferences?.let { preferences ->
                ScreenContent(
                    preferences = preferences,
                    onUpdateTempo = viewModel::updateTempo,
                    onUpdateTimeSignature = viewModel::updateTimeSignature,
                    onUpdateVibrationEnabled = viewModel::updateVibrationEnabled,
                    onOpenConvertBarStructurePicker = {
                        scope.launch {
                            barStructurePickerSheetState.animateTo(SheetDetent.FullyExpanded)
                        }
                    },
                    onOpenCustomTimeSignaturePicker = {
                        scope.launch {
                            customTimeSignaturePickerSheetState.animateTo(SheetDetent.FullyExpanded)
                        }
                    },
                    onSetConvertBarStructureReset = {
                        viewModel.convertBarStructure(null)
                    }
                )
            }
        }
    }
}

@Composable
private fun ScreenContent(
    preferences: MetronomePreferences,
    onUpdateTempo: (Int) -> Unit,
    onUpdateTimeSignature: (TimeSignature?) -> Unit,
    onUpdateVibrationEnabled: (Boolean) -> Unit,
    onOpenConvertBarStructurePicker: () -> Unit,
    onOpenCustomTimeSignaturePicker: () -> Unit,
    onSetConvertBarStructureReset: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        TempoSection(
            tempo = preferences.tempo,
            onTempoChange = onUpdateTempo
        )

        TimeSignatureSection(
            selectedTimeSignature = preferences.selectedTimeSignature,
            onTimeSignatureChange = onUpdateTimeSignature,
            convertedBarStructure = preferences.selectedBarStructure,
            onOpenConvertBarStructurePicker = onOpenConvertBarStructurePicker,
            onOpenCustomTimeSignaturePicker = onOpenCustomTimeSignaturePicker,
            onSetConvertBarStructureReset = onSetConvertBarStructureReset
        )

        Column(
            modifier = Modifier
                .background(
                    color = MaterialTheme.colorScheme.surfaceContainerLowest,
                    shape = MaterialTheme.shapes.large
                )
                .padding(16.dp)
        ) {
            SwitchSetting(
                title = stringResource(Res.string.vibration),
                description = stringResource(Res.string.vibration_desc),
                checked = preferences.vibrationEnabled,
                onCheckedChange = onUpdateVibrationEnabled
            )
        }
    }
}

@Composable
private fun TempoSection(
    tempo: Int,
    onTempoChange: (Int) -> Unit,
) {
    Column(
        modifier = Modifier
            .background(
                color = MaterialTheme.colorScheme.surfaceContainerLowest,
                shape = MaterialTheme.shapes.large
            )
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(Res.string.tempo),
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.primary
            )

            Text(
                text = stringResource(Res.string.bpm_format, tempo),
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.primary
            )
        }

        Slider(
            value = tempo.toFloat(),
            onValueChange = { onTempoChange(it.toInt()) },
            valueRange = 20f..240f,
            steps = 219,
            modifier = Modifier.fillMaxWidth()
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(Res.string.tempo_min),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Text(
                text = stringResource(Res.string.tempo_max),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedButton(
                modifier = Modifier.weight(1f),
                onClick = { if (tempo > 20) onTempoChange(tempo - 1) },
                enabled = tempo > 20,
            ) {
                Icon(
                    imageVector = Icons.Default.Remove,
                    contentDescription = stringResource(Res.string.cd_decrease_tempo)
                )

                Spacer(modifier = Modifier.width(8.dp))

                Text(stringResource(Res.string.decrement))
            }

            OutlinedButton(
                modifier = Modifier.weight(1f),
                onClick = { if (tempo < 240) onTempoChange(tempo + 1) },
                enabled = tempo < 240
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = stringResource(Res.string.cd_decrease_tempo)
                )

                Spacer(modifier = Modifier.width(8.dp))

                Text(stringResource(Res.string.increment))
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        HorizontalDivider()

        Text(
            text = stringResource(Res.string.detect_tempo),
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface
        )

        Text(
            text = stringResource(Res.string.detect_tempo_message),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(4f),
            onClick = {

            },
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.secondaryContainer,
            ),
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                Text(
                    modifier = Modifier.align(Alignment.Center),
                    text = stringResource(Res.string.tap_here),
                    style = MaterialTheme.typography.titleSmall
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TimeSignatureSection(
    selectedTimeSignature: TimeSignature?,
    convertedBarStructure: List<Note>?,
    onTimeSignatureChange: (TimeSignature?) -> Unit,
    onOpenConvertBarStructurePicker: () -> Unit,
    onOpenCustomTimeSignaturePicker: () -> Unit,
    onSetConvertBarStructureReset: () -> Unit,
) {
    val numeratorText = selectedTimeSignature?.numerator
        ?.toString()
        .orEmpty()
    val denominatorText = selectedTimeSignature?.denominator
        ?.toString()
        .orEmpty()

    Column(
        modifier = Modifier
            .background(
                color = MaterialTheme.colorScheme.surfaceContainerLowest,
                shape = MaterialTheme.shapes.large
            )
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                modifier = Modifier.weight(1f),
                text = stringResource(Res.string.time_signature),
                color = MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.titleLarge
            )

            AnimatedVisibility(
                selectedTimeSignature != null
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = "$numeratorText\n$denominatorText",
                        lineHeight = 12.sp,
                        style = NonCommonTypography.musicFont2,
                        color = MaterialTheme.colorScheme.primary
                    )

                    Text(
                        text = MusicSymbols.G_CLEF,
                        lineHeight = 14.sp,
                        style = NonCommonTypography.musicFontLarge,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = stringResource(Res.string.time_signature_hint),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(8.dp))

        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            FilterChip(
                selected = selectedTimeSignature == null,
                onClick = {
                    onTimeSignatureChange(null)
                },
                label = { Text(stringResource(Res.string.unselected)) }
            )

            TimeSignature.commonTimeSignatures.forEach { (b, u) ->
                FilterChip(
                    selected = selectedTimeSignature?.numerator == b
                        && selectedTimeSignature.denominator == u,
                    onClick = {
                        onTimeSignatureChange(
                            TimeSignature(b, u)
                        )
                    },
                    label = { Text("$b/$u") }
                )
            }


            FilterChip(
                selected = selectedTimeSignature != null &&
                    !TimeSignature.commonTimeSignatures.contains(
                        numeratorText.toIntOrNull() to denominatorText.toIntOrNull()
                    ),
                onClick = onOpenCustomTimeSignaturePicker,
                label = {
                    Text(stringResource(Res.string.custom))
                }
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        AnimatedVisibility(
            visible = selectedTimeSignature?.type in setOf(
                TimeSignatureType.Compound,
                TimeSignatureType.Irregular
            )
        ) {
            val warningText = buildAnnotatedString {
                withStyle(
                    MaterialTheme.typography
                        .titleSmall
                        .toSpanStyle()
                        .copy(fontWeight = FontWeight.Bold)
                ) {
                    append(
                        text = if (selectedTimeSignature?.type == TimeSignatureType.Compound) {
                            stringResource(Res.string.compound_time_signature)
                        } else {
                            stringResource(Res.string.irregular_time_signature)
                        },
                    )
                }
                appendLine()
                appendLine()
                append(
                    text = if (selectedTimeSignature?.type == TimeSignatureType.Compound) {
                        stringResource(Res.string.message_compound_time_signature_warning)
                    } else {
                        stringResource(Res.string.message_irregular_time_signature_warning)
                    },
                )
            }

            Text(
                modifier = Modifier.padding(bottom = 16.dp),
                text = warningText,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.tertiary
            )
        }

        AnimatedVisibility(
            selectedTimeSignature != null
        ) {
            LazyRow(
                modifier = Modifier
                    .padding(bottom = 16.dp)
                    .background(
                        MaterialTheme.colorScheme.surfaceContainerLow,
                        MaterialTheme.shapes.medium
                    )
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceEvenly,
                reverseLayout = true
            ) {
                items(
                    items = selectedTimeSignature?.defaultBarsStructure
                        .orEmpty(),
                ) { note ->
                    Text(
                        modifier = Modifier.animateItem(),
                        text = note.getUnitCharByUnit(),
                        style = NonCommonTypography.musicFontXLarge,
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                }
            }
        }

        HorizontalDivider()

        Spacer(Modifier.height(16.dp))

        var shouldShowConvertTimeSignature by remember {
            mutableStateOf(convertedBarStructure != null)
        }

        SwitchSetting(
            title = stringResource(Res.string.convert_bar),
            description = stringResource(Res.string.convert_bar_message),
            checked = shouldShowConvertTimeSignature,
            onCheckedChange = {
                if (!it) {
                    onSetConvertBarStructureReset()
                }
                shouldShowConvertTimeSignature = it
            }
        )

        AnimatedVisibility(shouldShowConvertTimeSignature) {
            val barStructureNoteUnit by remember(
                selectedTimeSignature,
                convertedBarStructure
            ) {
                derivedStateOf {
                    (convertedBarStructure ?: selectedTimeSignature?.defaultBarsStructure)
                        ?.lastOrNull()
                        ?.getUnitCharByUnit()
                        .orEmpty()
                }
            }

            Column {
                Spacer(Modifier.height(16.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(MaterialTheme.shapes.small)
                        .clickable {
                            onOpenConvertBarStructurePicker()
                        }
                        .border(
                            color = MaterialTheme.colorScheme.outlineVariant,
                            width = 1.dp,
                            shape = MaterialTheme.shapes.medium,
                        )
                        .padding(horizontal = 16.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Text(
                        text = stringResource(Res.string.choose_unit_note),
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )

                    Text(
                        text = barStructureNoteUnit,
                        style = NonCommonTypography.musicFont,
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                }

                AnimatedVisibility(convertedBarStructure != null) {
                    LazyRow(
                        modifier = Modifier
                            .padding(vertical = 16.dp)
                            .background(
                                MaterialTheme.colorScheme.secondaryContainer,
                                MaterialTheme.shapes.medium
                            )
                            .fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        reverseLayout = true
                    ) {
                        items(
                            items = convertedBarStructure.orEmpty(),
                        ) { note ->
                            Text(
                                modifier = Modifier.animateItem(),
                                text = note.getUnitCharByUnit(),
                                style = NonCommonTypography.musicFontXLarge,
                                color = MaterialTheme.colorScheme.onSecondaryContainer,
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SwitchSetting(
    title: String,
    description: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
@Preview
private fun ScreenContentPreview() {
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        MetronomeTheme {
            Box(Modifier.background(MaterialTheme.colorScheme.surface)) {
                ScreenContent(
                    preferences = MetronomePreferences(
                        tempo = 120,
                        selectedTimeSignature = TimeSignature(4, 4),
                        selectedBarStructure = listOf(),
                        vibrationEnabled = false
                    ),
                    onUpdateTempo = {},
                    onUpdateTimeSignature = { },
                    onUpdateVibrationEnabled = {},
                    onOpenConvertBarStructurePicker = {},
                    onOpenCustomTimeSignaturePicker = {}, {}
                )
            }
        }
    }
}
