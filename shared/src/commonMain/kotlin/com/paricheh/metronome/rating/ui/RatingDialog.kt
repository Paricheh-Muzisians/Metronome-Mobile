package com.paricheh.metronome.rating.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material.icons.rounded.StarOutline
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import androidx.lifecycle.viewmodel.compose.rememberViewModelStoreOwner
import com.paricheh.metronome.core.analytics.LocalAnalyticsManager
import com.paricheh.metronome.core.platform.LocalPlatformActionHandler
import metronome.shared.generated.resources.Res
import metronome.shared.generated.resources.metronome_launcher_foreground
import metronome.shared.generated.resources.rating_dialog_description
import metronome.shared.generated.resources.rating_dialog_not_now
import metronome.shared.generated.resources.rating_dialog_submit
import metronome.shared.generated.resources.rating_dialog_title
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun Rating(
    onRatingVisibilityChange: (Boolean) -> Unit,
) {
    val viewModelStoreOwner = rememberViewModelStoreOwner()

    CompositionLocalProvider(LocalViewModelStoreOwner provides viewModelStoreOwner) {
        Rating(
            viewModel = koinViewModel(
                viewModelStoreOwner = viewModelStoreOwner
            ),
            onRatingVisibilityChange = onRatingVisibilityChange
        )
    }
}

@Composable
private fun Rating(
    viewModel: RatingViewModel,
    onRatingVisibilityChange: (Boolean) -> Unit,
) {
    val shouldShowRating by viewModel.shouldShowRating.collectAsStateWithLifecycle()
    var isRatingVisible by rememberSaveable {
        mutableStateOf(shouldShowRating)
    }
    val analyticsManager = LocalAnalyticsManager.current
    val platformActionHandler = LocalPlatformActionHandler.current

    LaunchedEffect(shouldShowRating) {
        isRatingVisible = shouldShowRating
    }

    LaunchedEffect(isRatingVisible) {
        onRatingVisibilityChange(isRatingVisible)
    }

    if (isRatingVisible) {
        RatingDialog(
            onMarkAsPrompt = viewModel::markAsPrompt,
            onRated = { rate ->
                analyticsManager.track(
                    event = "user-rate",
                    parameters = mapOf(
                        "rate" to rate
                    )
                )

                if (rate > 3) {
                    platformActionHandler.openRatingPage()
                } else {
                    platformActionHandler.showToast("❤️")
                }

                viewModel.markAsRated()
            },
            onDismiss = {
                isRatingVisible = false
            }
        )
    }
}

@Composable
private fun RatingDialog(
    onMarkAsPrompt: () -> Unit,
    onRated: (Int) -> Unit,
    onDismiss: () -> Unit,
) {
    var selectedRate by rememberSaveable { mutableStateOf(0) }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = true,
            usePlatformDefaultWidth = true
        ),
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
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Spacer(Modifier.height(4.dp))

            Image(
                modifier = Modifier.size(72.dp),
                painter = painterResource(Res.drawable.metronome_launcher_foreground),
                contentDescription = null,
            )

            Spacer(Modifier.height(8.dp))

            Text(
                text = stringResource(Res.string.rating_dialog_title),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(Modifier.height(16.dp))

            Text(
                text = stringResource(Res.string.rating_dialog_description),
                style = MaterialTheme.typography.bodySmall,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(Modifier.height(24.dp))

            CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(2.dp),
                ) {
                    repeat(5) { currentRateIndex ->
                        AnimatedContent(
                            modifier = Modifier
                                .clip(MaterialTheme.shapes.small)
                                .clickable {
                                    selectedRate = currentRateIndex + 1
                                }
                                .padding(2.dp),
                            targetState = selectedRate >= currentRateIndex + 1,
                            transitionSpec = { fadeIn() togetherWith fadeOut() }
                        ) {
                            if (it) {
                                Icon(
                                    modifier = Modifier.size(40.dp),
                                    imageVector = Icons.Rounded.Star,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            } else {
                                Icon(
                                    modifier = Modifier.size(40.dp),
                                    imageVector = Icons.Rounded.StarOutline,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                                        .copy(alpha = 0.6f)
                                )
                            }
                        }
                    }
                }
            }

            Spacer(Modifier.height(24.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    modifier = Modifier
                        .weight(2f),
                    enabled = selectedRate != 0,
                    onClick = {
                        onRated(selectedRate)
                    }
                ) {
                    Text(stringResource(Res.string.rating_dialog_submit))
                }

                OutlinedButton(
                    modifier = Modifier
                        .weight(1f),
                    onClick = {
                        onMarkAsPrompt()
                    }
                ) {
                    Text(stringResource(Res.string.rating_dialog_not_now))
                }
            }

            Spacer(Modifier.height(16.dp))
        }
    }
}