package com.paricheh.metronome.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.paricheh.metronome.settings.SettingsViewModel
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    modifier: Modifier = Modifier,
    viewModel: SettingsViewModel = koinViewModel(),
    onNavigateBack: () -> Unit = {}
) {
    val preferences by viewModel.preferences.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // Tempo Section
            TempoSection(
                tempo = preferences.tempo,
                onTempoChange = { viewModel.updateTempo(it) }
            )

            HorizontalDivider()

            // Time Signature Section
            TimeSignatureSection(
                beats = preferences.timeSignatureBeats,
                beatUnit = preferences.timeSignatureBeatUnit,
                onTimeSignatureChange = { beats, unit ->
                    viewModel.updateTimeSignature(beats, unit)
                }
            )

            HorizontalDivider()

            // Accent First Beat
            SwitchSetting(
                title = "Accent First Beat",
                description = "Play a different sound on the first beat",
                checked = preferences.accentFirstBeat,
                onCheckedChange = { viewModel.updateAccentFirstBeat(it) }
            )

            HorizontalDivider()

            // Sound Enabled
            SwitchSetting(
                title = "Sound",
                description = "Enable metronome click sound",
                checked = preferences.soundEnabled,
                onCheckedChange = { viewModel.updateSoundEnabled(it) }
            )

            HorizontalDivider()

            // Vibration Enabled
            SwitchSetting(
                title = "Vibration",
                description = "Enable haptic feedback on each beat",
                checked = preferences.vibrationEnabled,
                onCheckedChange = { viewModel.updateVibrationEnabled(it) }
            )
        }
    }
}

@Composable
private fun TempoSection(
    tempo: Int,
    onTempoChange: (Int) -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Tempo",
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = "$tempo BPM",
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
                text = "20",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                IconButton(
                    onClick = { if (tempo > 20) onTempoChange(tempo - 1) },
                    enabled = tempo > 20
                ) {
                    Icon(
                        imageVector = Icons.Default.Remove,
                        contentDescription = "Decrease tempo"
                    )
                }

                IconButton(
                    onClick = { if (tempo < 240) onTempoChange(tempo + 1) },
                    enabled = tempo < 240
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Increase tempo"
                    )
                }
            }

            Text(
                text = "240",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TimeSignatureSection(
    beats: Int,
    beatUnit: Int,
    onTimeSignatureChange: (Int, Int) -> Unit
) {
    val commonSignatures = listOf(
        2 to 4,
        3 to 4,
        4 to 4,
        5 to 4,
        6 to 8,
        7 to 8,
        9 to 8,
        12 to 8
    )

    var showCustomPicker by remember { mutableStateOf(false) }
    val currentSignature = beats to beatUnit
    val isCustom = currentSignature !in commonSignatures

    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "Time Signature",
            style = MaterialTheme.typography.titleMedium
        )

        // Common signatures grid
        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            commonSignatures.forEach { (b, u) ->
                FilterChip(
                    selected = beats == b && beatUnit == u && !showCustomPicker,
                    onClick = {
                        onTimeSignatureChange(b, u)
                        showCustomPicker = false
                    },
                    label = { Text("$b/$u") }
                )
            }

            // Custom option
            FilterChip(
                selected = showCustomPicker || (isCustom && !showCustomPicker),
                onClick = { showCustomPicker = !showCustomPicker },
                label = {
                    Text(if (isCustom && !showCustomPicker) "$beats/$beatUnit" else "Custom")
                }
            )
        }

        // Custom picker with animation
        AnimatedVisibility(
            visible = showCustomPicker,
            enter = expandVertically(
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessLow
                )
            ) + fadeIn(
                animationSpec = tween(300)
            ),
            exit = shrinkVertically(
                animationSpec = tween(300)
            ) + fadeOut(
                animationSpec = tween(200)
            )
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainerHighest
                ),
                elevation = CardDefaults.cardElevation(
                    defaultElevation = 2.dp
                ),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Custom Time Signature",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.SemiBold
                        )

                        Text(
                            text = "$beats/$beatUnit",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Beats per measure
                        Column(
                            modifier = Modifier.weight(1f),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = "Beats",
                                style = MaterialTheme.typography.labelLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            var expanded by remember { mutableStateOf(false) }

                            ExposedDropdownMenuBox(
                                expanded = expanded,
                                onExpandedChange = { expanded = it }
                            ) {
                                OutlinedTextField(
                                    value = beats.toString(),
                                    onValueChange = {},
                                    readOnly = true,
                                    textStyle = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.SemiBold,
                                        textAlign = TextAlign.Center
                                    ),
                                    trailingIcon = {
                                        ExposedDropdownMenuDefaults.TrailingIcon(
                                            expanded = expanded
                                        )
                                    },
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                                        unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
                                    ),
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier
                                        .menuAnchor()
                                        .fillMaxWidth()
                                )

                                ExposedDropdownMenu(
                                    expanded = expanded,
                                    onDismissRequest = { expanded = false },
                                    modifier = Modifier.heightIn(max = 300.dp)
                                ) {
                                    (1..12).forEach { beat ->
                                        DropdownMenuItem(
                                            text = {
                                                Text(
                                                    beat.toString(),
                                                    style = MaterialTheme.typography.bodyLarge,
                                                    fontWeight = if (beat == beats) FontWeight.Bold else FontWeight.Normal
                                                )
                                            },
                                            onClick = {
                                                onTimeSignatureChange(beat, beatUnit)
                                                expanded = false
                                            },
                                            leadingIcon = if (beat == beats) {
                                                {
                                                    Icon(
                                                        imageVector = Icons.Default.Check,
                                                        contentDescription = null,
                                                        tint = MaterialTheme.colorScheme.primary
                                                    )
                                                }
                                            } else null,
                                            colors = MenuDefaults.itemColors(
                                                textColor = if (beat == beats)
                                                    MaterialTheme.colorScheme.primary
                                                else
                                                    MaterialTheme.colorScheme.onSurface
                                            )
                                        )
                                    }
                                }
                            }
                        }

                        // Beat unit
                        Column(
                            modifier = Modifier.weight(1f),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = "Unit",
                                style = MaterialTheme.typography.labelLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            var expanded by remember { mutableStateOf(false) }
                            val beatUnits = listOf(2, 4, 8, 16)

                            ExposedDropdownMenuBox(
                                expanded = expanded,
                                onExpandedChange = { expanded = it }
                            ) {
                                OutlinedTextField(
                                    value = beatUnit.toString(),
                                    onValueChange = {},
                                    readOnly = true,
                                    textStyle = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.SemiBold,
                                        textAlign = TextAlign.Center
                                    ),
                                    trailingIcon = {
                                        ExposedDropdownMenuDefaults.TrailingIcon(
                                            expanded = expanded
                                        )
                                    },
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                                        unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
                                    ),
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier
                                        .menuAnchor()
                                        .fillMaxWidth()
                                )

                                ExposedDropdownMenu(
                                    expanded = expanded,
                                    onDismissRequest = { expanded = false }
                                ) {
                                    beatUnits.forEach { unit ->
                                        DropdownMenuItem(
                                            text = {
                                                Text(
                                                    unit.toString(),
                                                    style = MaterialTheme.typography.bodyLarge,
                                                    fontWeight = if (unit == beatUnit) FontWeight.Bold else FontWeight.Normal
                                                )
                                            },
                                            onClick = {
                                                onTimeSignatureChange(beats, unit)
                                                expanded = false
                                            },

                                            colors = MenuDefaults.itemColors(
                                                textColor = if (unit == beatUnit)
                                                    MaterialTheme.colorScheme.primary
                                                else
                                                    MaterialTheme.colorScheme.onSurface
                                            )
                                        )
                                    }
                                }
                            }
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
    onCheckedChange: (Boolean) -> Unit
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
