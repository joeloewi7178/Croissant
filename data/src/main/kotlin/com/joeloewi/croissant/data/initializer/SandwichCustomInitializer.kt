/*
 *    Copyright 2024. joeloewi
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

package com.joeloewi.croissant.data.initializer

import android.content.Context
import androidx.startup.Initializer
import com.joeloewi.croissant.data.di.NetworkEntryPoint
import com.skydoves.sandwich.SandwichInitializer
import kotlinx.coroutines.CoroutineScope
import javax.inject.Inject

class SandwichCustomInitializer : Initializer<Unit> {

    @set:Inject
    internal lateinit var coroutineScope: CoroutineScope

    override fun create(context: Context) {
        NetworkEntryPoint.resolve(context).inject(this)

        SandwichInitializer.apply {
            sandwichScope = coroutineScope
        }
    }

    override fun dependencies(): MutableList<Class<out Initializer<*>>> = mutableListOf()
}