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
import androidx.hilt.work.HiltWorkerFactory
import androidx.startup.Initializer
import androidx.work.Configuration
import androidx.work.WorkManager
import com.joeloewi.croissant.data.di.DefaultDispatcherExecutor
import com.joeloewi.croissant.di.InitializerEntryPoint
import java.util.concurrent.Executor
import javax.inject.Inject

class WorkManagerInitializer : Initializer<WorkManager> {

    @set:Inject
    internal lateinit var hiltWorkerFactory: HiltWorkerFactory

    @Inject
    @DefaultDispatcherExecutor
    lateinit var executor: Executor

    override fun create(context: Context): WorkManager {
        InitializerEntryPoint.resolve(context).injectWorkManagerInitializer(this)

        WorkManager.initialize(
            context,
            Configuration.Builder()
                .setWorkerFactory(hiltWorkerFactory)
                .setExecutor(executor)
                .setTaskExecutor(executor)
                .build()
        )

        return WorkManager.getInstance(context)
    }

    override fun dependencies(): MutableList<Class<out Initializer<*>>> = mutableListOf()
}