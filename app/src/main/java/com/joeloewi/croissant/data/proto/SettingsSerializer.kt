package com.joeloewi.croissant.data.proto

import androidx.datastore.core.Serializer
import com.joeloewi.croissant.Settings
import java.io.InputStream
import java.io.OutputStream

object SettingsSerializer : Serializer<Settings> {
    override val defaultValue: Settings = Settings.getDefaultInstance()

    override suspend fun readFrom(input: InputStream): Settings = Settings.parseFrom(input)

    override suspend fun writeTo(t: Settings, output: OutputStream) = t.writeTo(output)
}