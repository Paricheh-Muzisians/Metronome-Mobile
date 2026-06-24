package com.paricheh.metronome.designsystem

import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.font.toFontFamily
import androidx.compose.ui.text.style.BaselineShift
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDirection
import metronome.shared.generated.resources.Res
import metronome.shared.generated.resources.*
import org.jetbrains.compose.resources.Font

val gulzarFont: FontFamily
    @Composable
    get() = Font(
        Res.font.gulzar_regular
    ).toFontFamily()

val peyda_bold: FontFamily
    @Composable
    get() = Font(
        Res.font.peyda_bold
    ).toFontFamily()

val peyda_regular: FontFamily
    @Composable
    get() = Font(
        Res.font.peyda_regular
    ).toFontFamily()

val natoMusicFont: FontFamily
    @Composable
    get() = Font(
        Res.font.noto_music_regular
    ).toFontFamily()


object NonCommonTypography {
    val PersianSonatiHeader
        @Composable
        get() = Typography().titleMedium.copy(
            fontFamily = gulzarFont,
            textAlign = TextAlign.Center,
            baselineShift = BaselineShift.Subscript,
            fontWeight = FontWeight.Bold
        )
    val PersianSonatiNumber
        @Composable
        get() = Typography().headlineSmall.copy(
            fontFamily = gulzarFont,
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Bold
        )
    val PersianSonatiLabel
        @Composable
        get() = Typography().labelSmall.copy(
            fontFamily = gulzarFont,
            textAlign = TextAlign.Center,
        )
    val EnglishSontatiHeader
        @Composable
        get() = Typography().headlineSmall.copy(
            fontFamily = gulzarFont
        )
    val musicFont
        @Composable
        get() = Typography().headlineSmall.copy(
            fontFamily = natoMusicFont
        )
    val musicFont2
        @Composable
        get() = Typography().titleMedium.copy(
            fontFamily = natoMusicFont
        )
    val musicFontLarge
        @Composable
        get() = Typography().headlineSmall.copy(
            fontFamily = natoMusicFont
        )
    val musicFontXLarge
        @Composable
        get() = Typography().headlineLarge.copy(
            fontFamily = natoMusicFont
        )

}

@Composable
fun metronomeTypography(): Typography {
    return Typography(
        displayLarge = Typography().displayLarge.copy(
            fontFamily = peyda_bold,
            textDirection = TextDirection.ContentOrLtr
        ),
        displayMedium = Typography().displayMedium.copy(
            fontFamily = peyda_bold,
            textDirection = TextDirection.ContentOrLtr
        ),
        displaySmall = Typography().displaySmall.copy(
            fontFamily = peyda_bold,
            textDirection = TextDirection.ContentOrLtr
        ),
        headlineLarge = Typography().headlineLarge.copy(
            fontFamily = peyda_bold,
            textDirection = TextDirection.ContentOrLtr
        ),
        headlineMedium = Typography().headlineMedium.copy(
            fontFamily = peyda_bold,
            textDirection = TextDirection.ContentOrLtr
        ),
        headlineSmall = Typography().headlineSmall.copy(
            fontFamily = peyda_bold,
            textDirection = TextDirection.ContentOrLtr
        ),
        titleLarge = Typography().titleLarge.copy(
            fontFamily = peyda_bold,
            textDirection = TextDirection.ContentOrLtr
        ),
        titleMedium = Typography().titleMedium.copy(
            fontFamily = peyda_bold,
            textDirection = TextDirection.ContentOrLtr
        ),
        titleSmall = Typography().titleSmall.copy(
            fontFamily = peyda_bold,
            textDirection = TextDirection.ContentOrLtr
        ),
        bodyLarge = Typography().bodyLarge.copy(
            fontFamily = peyda_regular,
            textDirection = TextDirection.ContentOrLtr
        ),
        bodyMedium = Typography().bodyMedium.copy(
            fontFamily = peyda_regular,
            textDirection = TextDirection.ContentOrLtr
        ),
        bodySmall = Typography().bodySmall.copy(
            fontFamily = peyda_regular,
            textDirection = TextDirection.ContentOrLtr
        ),
        labelLarge = Typography().labelLarge.copy(
            fontFamily = peyda_regular,
            textDirection = TextDirection.ContentOrLtr
        ),
        labelMedium = Typography().labelMedium.copy(
            fontFamily = peyda_regular,
            textDirection = TextDirection.ContentOrLtr
        ),
        labelSmall = Typography().labelSmall.copy(
            fontFamily = peyda_regular,
            textDirection = TextDirection.ContentOrLtr
        ),
    )
}
