package com.paricheh.metronome.tuner.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.paricheh.metronome.tuner.data.preferences.TunerPreferences
import com.paricheh.metronome.tuner.ui.utils.instrument.Guitar6String
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(
    name = "tuner_preferences"
)

class TunerPreferencesImpl(
    context: Context,
) : TunerPreferences {

    private val dataStore = context.dataStore

    override val selectedInstrumentKey: Flow<String> = dataStore.data
        .map { it[SELECTED_INSTRUMENT] ?: Guitar6String.KEY }

    override suspend fun setSelectedInstrumentKey(key: String) {
        dataStore.edit {
            it[SELECTED_INSTRUMENT] = key
        }
    }

    companion object {
        private val SELECTED_INSTRUMENT = stringPreferencesKey(
            "selected-instrument-key"
        )
    }
}