package com.joeloewi.croissant.service

import android.content.Intent
import android.widget.RemoteViewsService
import com.joeloewi.croissant.util.ListRemoteViewsFactory
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RemoteViewsFactoryService : RemoteViewsService() {
    override fun onGetViewFactory(p0: Intent?): RemoteViewsFactory =
        ListRemoteViewsFactory(
            context = applicationContext,
            intent = p0
        )
}