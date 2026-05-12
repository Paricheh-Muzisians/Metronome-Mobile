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
import metronome.composeapp.generated.resources.irregular_time_signature
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
            onTimeSignatureChange = onUpdateTimeSignature
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
    onTimeSignatureChange: (TimeSignature?) -> Unit,
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
                        onTimeSignatureChange(
                            TimeSignature(b, u)
                        )
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
                            stringResource(Res.string.compund_time_signature)
                        } else {
                            stringResource(Res.string.irregular_time_signature)
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
        var shouldShowConvertTimeSignature by remember { mutableStateOf(false) }

        SwitchSetting(
            title = "تبدیل میزان",
            description = "کسر میزان انتخاب شده می تواند به حالت های زیر نیز نمایش داده شود.",
            checked = shouldShowConvertTimeSignature,
            onCheckedChange = { shouldShowConvertTimeSignature = it }
        )

        repeat(2) {
            AnimatedVisibility(shouldShowConvertTimeSignature && selectedTimeSignature != null) {
                LazyRow(
                    modifier = Modifier
                        .padding(16.dp)
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
                        items = selectedTimeSignature?.defaultBarsStructure
                            .orEmpty(),
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


//        selectedTimeSignature?.getUnitNote()

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
                )
            }
        }
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