package com.paricheh.metronome.settings

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
import com.paricheh.metronome.data.MetronomePreferences
import com.paricheh.metronome.theme.MetronomeTheme
import com.paricheh.metronome.theme.NonCommonTypography
import com.paricheh.metronome.utils.MusicSymbols
import com.paricheh.metronome.utils.TimeSignature
import com.paricheh.metronome.utils.TimeSignatureType
import metronome.composeapp.generated.resources.Res
import metronome.composeapp.generated.resources.bpm_format
import metronome.composeapp.generated.resources.cd_back
import metronome.composeapp.generated.resources.cd_decrease_tempo
import metronome.composeapp.generated.resources.compund_time_signature
import metronome.composeapp.generated.resources.decrement
import metronome.composeapp.generated.resources.detect_tempo
import metronome.composeapp.generated.resources.detect_tempo_message
import metronome.composeapp.generated.resources.increment
import metronome.composeapp.generated.resources.message_irregular_time_signature_warning
import metronome.composeapp.generated.resources.settings_title
import metronome.composeapp.generated.resources.tab_here
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
    val preferences by viewModel.preferences.collectAsStateWithLifecycle()

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
                }
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
                )
            }
        }
    }
}

@Composable
@Preview
private fun ScreenContentPreview() {
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        MetronomeTheme {
            Box(Modifier.background(MaterialTheme.colorScheme.surface)) {
                ScreenContent(
                    preferences = MetronomePreferences(
                        timeSignatureBeats = 6,
                        timeSignatureBeatUnit = 8
                    ),
                    onUpdateTempo = {},
                    onUpdateTimeSignature = { _, _ -> },
                    onUpdateVibrationEnabled = {}
                )
            }
        }
    }
}

@Composable
private fun ScreenContent(
    preferences: MetronomePreferences,
    onUpdateTempo: (Int) -> Unit,
    onUpdateTimeSignature: (Int, Int) -> Unit,
    onUpdateVibrationEnabled: (Boolean) -> Unit,
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

        val selectedTimeSignature by remember(
            preferences.timeSignatureBeats,
            preferences.timeSignatureBeatUnit
        ) {
            derivedStateOf {
                TimeSignature(
                    preferences.timeSignatureBeats,
                    preferences.timeSignatureBeatUnit
                ).takeIf { it.denominator != -1 || it.numerator != -1 }
            }
        }

        TimeSignatureSection(
            selectedTimeSignature = selectedTimeSignature,
            onTimeSignatureChange = onUpdateTimeSignature
        )

        SwitchSetting(
            title = stringResource(Res.string.vibration),
            description = stringResource(Res.string.vibration_desc),
            checked = preferences.vibrationEnabled,
            onCheckedChange = onUpdateVibrationEnabled
        )

        HorizontalDivider()

    }
}

@Composable
private fun TempoSection(
    tempo: Int,
    onTempoChange: (Int) -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(Res.string.tempo),
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurface
            )

            Text(
                text = stringResource(Res.string.bpm_format, tempo),
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.primary
            )
        }

        HorizontalDivider()

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

        Text(
            text = stringResource(Res.string.detect_tempo),
            style = MaterialTheme.typography.titleSmall,
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
                    text = stringResource(Res.string.tab_here),
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
    onTimeSignatureChange: (Int, Int) -> Unit,
) {
    var showCustomPicker by remember { mutableStateOf(false) }
    val isCustom = remember(selectedTimeSignature) {
        selectedTimeSignature?.isCommon() ?: false
    }
    val numeratorText = selectedTimeSignature?.numerator
        ?.toString()
        .orEmpty()
    val denominatorText = selectedTimeSignature?.denominator
        ?.toString()
        .orEmpty()

    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                modifier = Modifier.weight(1f),
                text = stringResource(Res.string.time_signature),
                color = MaterialTheme.colorScheme.onSurface,
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
                        lineHeight = 17.sp,
                        style = NonCommonTypography.musicFont,
                        color = MaterialTheme.colorScheme.primary
                    )

                    Text(
                        text = MusicSymbols.G_CLEF,
                        lineHeight = 18.sp,
                        style = NonCommonTypography.musicFontLarge,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }

        HorizontalDivider()

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
//            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            FilterChip(
                selected = selectedTimeSignature == null,
                onClick = {
                    onTimeSignatureChange(-1, -1)
                    showCustomPicker = false
                },
                label = { Text(stringResource(Res.string.unselected)) }
            )

            TimeSignature.commonTimeSignatures.forEach { (b, u) ->
                FilterChip(
                    selected = selectedTimeSignature?.numerator == b
                        && selectedTimeSignature.denominator == u
                        && !showCustomPicker,
                    onClick = {
                        onTimeSignatureChange(b, u)
                        showCustomPicker = false
                    },
                    label = { Text("$b/$u") }
                )
            }

//            FilterChip(
//                selected = showCustomPicker || (isCustom && !showCustomPicker),
//                onClick = { showCustomPicker = !showCustomPicker },
//                label = {
//                    Text(
//                        if (isCustom && !showCustomPicker)
//                            "$numeratorText/$denominatorText"
//                        else stringResource(Res.string.custom)
//                    )
//                }
//            )
        }

        AnimatedVisibility(
            visible = selectedTimeSignature?.type in setOf(
                TimeSignatureType.Compound,
                TimeSignatureType.Irregular
            )
        ) {
            Column {
                Spacer(Modifier.height(16.dp))

                val warningText = buildAnnotatedString {
                    withStyle(
                        MaterialTheme.typography
                            .titleSmall
                            .toSpanStyle()
                            .copy(fontWeight = FontWeight.Bold)
                    ) {
                        append(
                            text = if (selectedTimeSignature?.type == TimeSignatureType.Compound) {
                                stringResource(Res.string.compund_time_signature)
                            } else {
                                stringResource(Res.string.compund_time_signature)
                            },
                        )
                    }
                    appendLine()
                    appendLine()
                    append(
                        stringResource(Res.string.message_irregular_time_signature_warning)
                    )
                }

                Text(
                    text = warningText,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.tertiary
                )

                LazyRow(
                    modifier = Modifier
                        .padding(16.dp)
                        .background(
                            MaterialTheme.colorScheme.tertiaryContainer,
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
                            style = NonCommonTypography.musicFontLarge,
                            color = MaterialTheme.colorScheme.onTertiaryContainer,
                        )
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


// Custom TimeSignatureSelector Section
//AnimatedVisibility(
//visible = showCustomPicker,
//enter = expandVertically() + fadeIn(),
//exit = shrinkVertically() + fadeOut()
//) {
//    Card(
//        modifier = Modifier
//            .fillMaxWidth()
//            .padding(top = 4.dp),
//        colors = CardDefaults.cardColors(
//            containerColor = MaterialTheme.colorScheme.surfaceContainerHighest
//        ),
//        elevation = CardDefaults.cardElevation(
//            defaultElevation = 2.dp
//        ),
//        shape = MaterialTheme.shapes.medium
//    ) {
//        Column(
//            modifier = Modifier
//                .fillMaxWidth()
//                .padding(20.dp),
//            verticalArrangement = Arrangement.spacedBy(16.dp)
//        ) {
//            Row(
//                modifier = Modifier.fillMaxWidth(),
//                horizontalArrangement = Arrangement.SpaceBetween,
//                verticalAlignment = Alignment.CenterVertically
//            ) {
//                Text(
//                    text = stringResource(Res.string.custom_time_signature),
//                    style = MaterialTheme.typography.titleSmall,
//                    fontWeight = FontWeight.SemiBold
//                )
//
//                Text(
//                    text = "/${selectedTimeSignature?.denominator}",
//                    style = MaterialTheme.typography.titleMedium,
//                    color = MaterialTheme.colorScheme.primary,
//                    fontWeight = FontWeight.Bold
//                )
//            }
//
//            Row(
//                modifier = Modifier.fillMaxWidth(),
//                horizontalArrangement = Arrangement.spacedBy(12.dp)
//            ) {
//                // Beats per measure
//                Column(
//                    modifier = Modifier.weight(1f),
//                    verticalArrangement = Arrangement.spacedBy(8.dp)
//                ) {
//                    Text(
//                        text = stringResource(Res.string.beats),
//                        style = MaterialTheme.typography.labelLarge,
//                        color = MaterialTheme.colorScheme.onSurfaceVariant
//                    )
//
//                    var expanded by remember { mutableStateOf(false) }
//
//                    ExposedDropdownMenuBox(
//                        expanded = expanded,
//                        onExpandedChange = { expanded = it }
//                    ) {
//                        OutlinedTextField(
//                            value = selectedTimeSignature.toString(),
//                            onValueChange = {},
//                            readOnly = true,
//                            textStyle = MaterialTheme.typography.titleMedium.copy(
//                                fontWeight = FontWeight.SemiBold,
//                                textAlign = TextAlign.Center
//                            ),
//                            trailingIcon = {
//                                ExposedDropdownMenuDefaults.TrailingIcon(
//                                    expanded = expanded
//                                )
//                            },
//                            colors = OutlinedTextFieldDefaults.colors(
//                                focusedBorderColor = MaterialTheme.colorScheme.primary,
//                                unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(
//                                    alpha = 0.5f
//                                )
//                            ),
//                            modifier = Modifier
//                                .menuAnchor()
//                                .fillMaxWidth()
//                        )
//
//                        ExposedDropdownMenu(
//                            expanded = expanded,
//                            onDismissRequest = { expanded = false },
//                            modifier = Modifier.heightIn(max = 300.dp)
//                        ) {
//                            (1..12).forEach { beat ->
//                                DropdownMenuItem(
//                                    text = {
//                                        Text(
//                                            beat.toString(),
//                                            style = MaterialTheme.typography.bodyLarge,
//                                            fontWeight = if (beat == selectedTimeSignature?.denominator)
//                                                FontWeight.Bold else FontWeight.Normal
//                                        )
//                                    },
//                                    onClick = {
//                                        onTimeSignatureChange(
//                                            beat,
//                                            selectedTimeSignature?.denominator ?: -1
//                                        )
//                                        expanded = false
//                                    },
//                                )
//                            }
//                        }
//                    }
//                }
//
//                // Beat unit
//                Column(
//                    modifier = Modifier.weight(1f),
//                    verticalArrangement = Arrangement.spacedBy(8.dp)
//                ) {
//                    Text(
//                        text = stringResource(Res.string.unit),
//                        style = MaterialTheme.typography.labelLarge,
//                        color = MaterialTheme.colorScheme.onSurfaceVariant
//                    )
//
//                    var expanded by remember { mutableStateOf(false) }
//                    val beatUnits = listOf(2, 4, 8, 16)
//
//                    ExposedDropdownMenuBox(
//                        expanded = expanded,
//                        onExpandedChange = { expanded = it }
//                    ) {
//                        OutlinedTextField(
//                            value = beatUnit.toString(),
//                            onValueChange = {},
//                            readOnly = true,
//                            textStyle = MaterialTheme.typography.titleMedium.copy(
//                                fontWeight = FontWeight.SemiBold,
//                                textAlign = TextAlign.Center
//                            ),
//                            trailingIcon = {
//                                ExposedDropdownMenuDefaults.TrailingIcon(
//                                    expanded = expanded
//                                )
//                            },
//                            colors = OutlinedTextFieldDefaults.colors(
//                                focusedBorderColor = MaterialTheme.colorScheme.primary,
//                                unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(
//                                    alpha = 0.5f
//                                )
//                            ),
//                            shape = RoundedCornerShape(12.dp),
//                            modifier = Modifier
//                                .menuAnchor()
//                                .fillMaxWidth()
//                        )
//
//                        ExposedDropdownMenu(
//                            expanded = expanded,
//                            onDismissRequest = { expanded = false }
//                        ) {
//                            beatUnits.forEach { unit ->
//                                DropdownMenuItem(
//                                    text = {
//                                        Text(
//                                            unit.toString(),
//                                            style = MaterialTheme.typography.bodyLarge,
//                                            fontWeight = if (unit == beatUnit) FontWeight.Bold else FontWeight.Normal
//                                        )
//                                    },
//                                    onClick = {
//                                        onTimeSignatureChange(beats, unit)
//                                        expanded = false
//                                    },
//
//                                    colors = MenuDefaults.itemColors(
//                                        textColor = if (unit == beatUnit)
//                                            MaterialTheme.colorScheme.primary
//                                        else
//                                            MaterialTheme.colorScheme.onSurface
//                                    )
//                                )
//                            }
//                        }
//                    }
//                }
//            }
//        }
//    }
//}