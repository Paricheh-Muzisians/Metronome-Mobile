package com.paricheh.metronome.tuner.ui.tuner.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Sensors
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import metronome.shared.generated.resources.Res
import metronome.shared.generated.resources.cd_back
import metronome.shared.generated.resources.microphone_permission_description
import metronome.shared.generated.resources.microphone_permission_title
import metronome.shared.generated.resources.open_settings
import org.jetbrains.compose.resources.stringResource

@Composable
fun PermissionDialog(
    onOpenSettings: () -> Unit,
    onBack: () -> Unit,
) {
    Dialog(
        onDismissRequest = { },
        properties = DialogProperties(
            dismissOnBackPress = false,
            dismissOnClickOutside = false,
            usePlatformDefaultWidth = false
        ),
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            val secondaryColor = MaterialTheme.colorScheme.secondaryContainer
            Column(
                modifier = Modifier
                    .drawBehind {
                        clipRect {
                            drawCircle(
                                brush = Brush.radialGradient(
                                    colors = listOf(secondaryColor, Color.Transparent),
                                    center = center.copy(y = 24.dp.toPx())
                                ),
                                center = center.copy(y = 24.dp.toPx())
                            )
                        }
                    }
                    .fillMaxWidth()
                    .clip(shape = MaterialTheme.shapes.extraLarge)
                    .border(
                        width = 1.dp,
                        color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f),
                        shape = MaterialTheme.shapes.extraLarge
                    )
                    .background(
                        MaterialTheme.colorScheme.surfaceContainerLow.copy(alpha = 0.6f)
                    )
                    .padding(horizontal = 16.dp, vertical = 20.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Icon(
                    modifier = Modifier.size(48.dp),
                    imageVector = Icons.Rounded.Sensors,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )

                Spacer(Modifier.height(12.dp))

                Text(
                    text = stringResource(Res.string.microphone_permission_title),
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(Modifier.height(12.dp))

                Text(
                    text = stringResource(Res.string.microphone_permission_description),
                    style = MaterialTheme.typography.bodySmall,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(Modifier.height(24.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        modifier = Modifier.weight(1f),
                        onClick = onOpenSettings
                    ) {
                        Text(stringResource(Res.string.open_settings))
                    }

                    OutlinedButton(
                        modifier = Modifier.weight(1f),
                        onClick = onBack
                    ) {
                        Text(stringResource(Res.string.cd_back))
                    }
                }
            }

        }
    }
}
