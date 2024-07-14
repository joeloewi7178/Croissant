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
import com.joeloewi.croissant.initializer.CoilInitializer
import com.joeloewi.croissant.initializer.NotificationChannelInitializer
import com.joeloewi.croissant.initializer.WorkManagerInitializer
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.components.SingletonComponent

@EntryPoint
@InstallIn(SingletonComponent::class)
interface InitializerEntryPoint {
    fun inject(coilInitializer: CoilInitializer)
    fun inject(initializer: NotificationChannelInitializer)
    fun inject(workManagerInitializer: WorkManagerInitializer)

    companion object {
        fun resolve(context: Context): InitializerEntryPoint {
            val appContext = context.applicationContext ?: throw IllegalStateException(
                "applicationContext was not found in InitializerEntryPoint",
            )
            return EntryPointAccessors.fromApplication(
                appContext,
                InitializerEntryPoint::class.java,
            )
        }
    }
}