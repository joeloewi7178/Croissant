/*
 *    Copyright 2023. joeloewi
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

package com.joeloewi.croissant.di

import android.content.Context
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.RunnableScheduler
import coil.ImageLoader
import com.joeloewi.croissant.data.di.DefaultDispatcherExecutor
import com.joeloewi.croissant.util.NotificationGenerator
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.components.SingletonComponent
import java.util.concurrent.Executor
import kotlin.reflect.KClass

@EntryPoint
@InstallIn(SingletonComponent::class)
interface InitializerEntryPoint {
    fun imageLoader(): ImageLoader
    fun hiltWorkerFactory(): HiltWorkerFactory

    @DefaultDispatcherExecutor
    fun executor(): Executor
    fun runnableScheduler(): RunnableScheduler
    fun notificationGenerator(): NotificationGenerator
}

inline fun <reified EntryPoint : Any> Context.entryPoints(): Lazy<EntryPoint> = EntryPointLazy(
    entryPointInterface = EntryPoint::class,
    context = this
)

class EntryPointLazy<EntryPoint : Any>(
    private val entryPointInterface: KClass<EntryPoint>,
    private val context: Context
) : Lazy<EntryPoint> {
    private var cached: EntryPoint? = null
    override val value: EntryPoint
        get() = EntryPointAccessors.fromApplication(context, entryPointInterface.java).also {
            cached = it
        }

    override fun isInitialized(): Boolean = cached != null
}