package com.joeloewi.croissant.initializer

import android.content.Context
import androidx.startup.Initializer
import coil.Coil
import coil.ImageLoader
import com.joeloewi.croissant.R

class CoilInitializer : Initializer<Unit> {
    override fun create(context: Context) {
        Coil.setImageLoader {
            ImageLoader.Builder(context)
                .crossfade(true)
                .placeholder(R.drawable.image_placeholder)
                .build()
        }
    }

    override fun dependencies(): MutableList<Class<out Initializer<*>>> = mutableListOf()
}