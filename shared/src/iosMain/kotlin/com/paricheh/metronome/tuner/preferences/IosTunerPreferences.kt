package com.paricheh.metronome.tuner.preferences

import com.paricheh.metronome.tuner.data.preferences.TunerPreferences
import com.paricheh.metronome.tuner.ui.utils.instrument.Guitar6String
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import platform.Foundation.NSUserDefaults

class IosTunerPreferences : TunerPreferences {
    private val userDefaults = NSUserDefaults.standardUserDefaults
    private val _selectedInstrumentKey = MutableStateFlow(
        userDefaults.stringForKey(KEY_SELECTED_INSTRUMENT) ?: Guitar6String.KEY
    )

    override val selectedInstrumentKey: Flow<String> = _selectedInstrumentKey.asStateFlow()

    override suspend fun setSelectedInstrumentKey(key: String) {
        userDefaults.setObject(key, KEY_SELECTED_INSTRUMENT)
        _selectedInstrumentKey.value = key
    }

    companion object {
        private const val KEY_SELECTED_INSTRUMENT = "selected_instrument_key"
    }
}
