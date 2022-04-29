package com.joeloewi.croissant.data.proto

import androidx.datastore.core.Serializer
import com.joeloewi.croissant.Widget
import java.io.InputStream
import java.io.OutputStream

object WidgetSerializer : Serializer<Widget> {
    override val defaultValue: Widget = Widget.getDefaultInstance()

    override suspend fun readFrom(input: InputStream): Widget = Widget.parseFrom(input)

    override suspend fun writeTo(t: Widget, output: OutputStream) = t.writeTo(output)
}