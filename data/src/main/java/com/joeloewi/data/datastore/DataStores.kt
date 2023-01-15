package com.joeloewi.data.datastore

import android.content.Context
import androidx.datastore.dataStore

internal val Context.settingsDataStore by dataStore(
    fileName = "settings.pb",
    serializer = SettingsSerializer
)