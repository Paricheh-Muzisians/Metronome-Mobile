package com.paricheh.metronome.theme

import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.font.toFontFamily
import androidx.compose.ui.text.style.BaselineShift
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDirection
import androidx.compose.ui.unit.sp
import metronome.composeapp.generated.resources.Res
import metronome.composeapp.generated.resources.dimatype_header
import metronome.composeapp.generated.resources.gulzar_regular
import metronome.composeapp.generated.resources.noto_sans_arabic
import org.jetbrains.compose.resources.Font

val gulzarFont: FontFamily
    @Composable
    get() = Font(
        Res.font.gulzar_regular
    ).toFontFamily()

val natoSansFont: FontFamily
    @Composable
    get() = Font(
        Res.font.noto_sans_arabic
    ).toFontFamily()

val natoMusicFont: FontFamily
    @Composable
    get() = Font(
        Res.font.noto_sans_arabic
    ).toFontFamily()


object NonCommonTypography {
    val PersianSonatiHeader
        @Composable
        get() = Typography().displaySmall.copy(
            fontFamily = gulzarFont,
            textAlign = TextAlign.Center,
            baselineShift = BaselineShift.Subscript,
            fontWeight = FontWeight.Bold
        )
    val PersianSonatiNumber
        @Composable
        get() = Typography().headlineLarge.copy(
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
        get() = Typography().titleMedium.copy(
            fontFamily = gulzarFont
        )
    val musicFont
        @Composable
        get() = Typography().titleMedium.copy(
            fontFamily = gulzarFont
        )

}

@Composable
fun metronomeTypography(): Typography {
    return Typography(
        displayLarge = Typography().displayLarge.copy(
            fontFamily = natoSansFont,
            textDirection = TextDirection.ContentOrLtr
        ),
        displayMedium = Typography().displayMedium.copy(
            fontFamily = natoSansFont,
            textDirection = TextDirection.ContentOrLtr
        ),
        displaySmall = Typography().displaySmall.copy(
            fontFamily = natoSansFont,
            textDirection = TextDirection.ContentOrLtr
        ),
        headlineLarge = Typography().headlineLarge.copy(
            fontFamily = natoSansFont,
            textDirection = TextDirection.ContentOrLtr
        ),
        headlineMedium = Typography().headlineMedium.copy(
            fontFamily = natoSansFont,
            textDirection = TextDirection.ContentOrLtr
        ),
        headlineSmall = Typography().headlineSmall.copy(
            fontFamily = natoSansFont,
            textDirection = TextDirection.ContentOrLtr
        ),
        titleLarge = Typography().titleLarge.copy(
            fontFamily = natoSansFont,
            textDirection = TextDirection.ContentOrLtr
        ),
        titleMedium = Typography().titleMedium.copy(
            fontFamily = natoSansFont,
            textDirection = TextDirection.ContentOrLtr
        ),
        titleSmall = Typography().titleSmall.copy(
            fontFamily = natoSansFont,
            textDirection = TextDirection.ContentOrLtr
        ),
        bodyLarge = Typography().bodyLarge.copy(
            fontFamily = natoSansFont,
            textDirection = TextDirection.ContentOrLtr
        ),
        bodyMedium = Typography().bodyMedium.copy(
            fontFamily = natoSansFont,
            textDirection = TextDirection.ContentOrLtr
        ),
        bodySmall = Typography().bodySmall.copy(
            fontFamily = natoSansFont,
            textDirection = TextDirection.ContentOrLtr
        ),
        labelLarge = Typography().labelLarge.copy(
            fontFamily = natoSansFont,
            textDirection = TextDirection.ContentOrLtr
        ),
        labelMedium = Typography().labelMedium.copy(
            fontFamily = natoSansFont,
            textDirection = TextDirection.ContentOrLtr
        ),
        labelSmall = Typography().labelSmall.copy(
            fontFamily = natoSansFont,
            textDirection = TextDirection.ContentOrLtr
        ),
    )
}
