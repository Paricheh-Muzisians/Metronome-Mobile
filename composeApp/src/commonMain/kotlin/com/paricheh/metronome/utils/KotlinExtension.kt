package com.paricheh.metronome.utils

fun String.toPersianNumbers(): String {
    val persianDigits = listOf('۰', '۱', '۲', '۳', '۴', '۵', '۶', '۷', '۸', '۹')
    return this.map { char ->
        when (char) {
            in '0'..'9' -> persianDigits[char - '0']
            // Also handle Arabic-Indic digits (٠١٢٣٤٥٦٧٨٩)
            in '٠'..'٩' -> persianDigits[char - '٠']
            else -> char
        }
    }.joinToString("")
}
