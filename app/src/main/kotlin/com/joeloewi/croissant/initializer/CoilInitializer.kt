/*
 *    Copyright 2022 joeloewi
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.joeloewi.croissant.initializer

import android.content.Context
import androidx.startup.Initializer
import coil.Coil
import coil.ImageLoader
import coil.imageLoader
import com.joeloewi.croissant.di.InitializerEntryPoint
import javax.inject.Inject

class CoilInitializer : Initializer<ImageLoader> {

    @set:Inject
    internal lateinit var imageLoader: ImageLoader

    override fun create(context: Context): ImageLoader {
        InitializerEntryPoint.resolve(context).injectCoilInitializer(this)

        Coil.setImageLoader { imageLoader }

        return context.imageLoader
    }

    override fun dependencies(): MutableList<Class<out Initializer<*>>> = mutableListOf()
}