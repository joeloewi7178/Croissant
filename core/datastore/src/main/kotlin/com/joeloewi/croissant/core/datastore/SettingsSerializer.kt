package com.joeloewi.croissant.core.datastore

import androidx.datastore.core.Serializer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.InputStream
import java.io.OutputStream

class SettingsSerializer : Serializer<Settings> {
    override val defaultValue: Settings = Settings.newBuilder()
        .setDarkThemeEnabled(false)
        .setIsFirstLaunch(true)
        .setNotifyMigrateToAlarmManager(true)
        .build()

    override suspend fun readFrom(input: InputStream): Settings = withContext(Dispatchers.IO) {
        Settings.parseFrom(input)
    }

    override suspend fun writeTo(t: Settings, output: OutputStream) = withContext(Dispatchers.IO) {
        t.writeTo(output)
    }
}
