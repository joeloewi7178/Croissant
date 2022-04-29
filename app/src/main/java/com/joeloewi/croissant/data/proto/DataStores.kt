package com.joeloewi.croissant.data.proto

import android.content.Context
import androidx.datastore.dataStore

val Context.settingsDataStore by dataStore(
    fileName = "settings.pb",
    serializer = SettingsSerializer
)

val Context.widgetDataStore by dataStore(
    fileName = "widget.pb",
    serializer = WidgetSerializer
)