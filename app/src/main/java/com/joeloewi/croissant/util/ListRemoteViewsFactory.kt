package com.joeloewi.croissant.util

import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import android.widget.RemoteViewsService

class ListRemoteViewsFactory(
    private val context: Context,
    private val intent: Intent?
) : RemoteViewsService.RemoteViewsFactory {
    private val resinStatuses = arrayListOf<ResinStatus>()

    private fun setResinStatuses(intent: Intent?) {
        resinStatuses.apply {
            clear()
            intent?.extras?.getBundle(BUNDLE)
                ?.getParcelable(RESIN_STATUSES, resinStatuses::class.java)?.let {
                    addAll(it)
                }
        }
    }

    override fun onCreate() {
        setResinStatuses(intent)
    }

    override fun onDataSetChanged() {
        setResinStatuses(intent)
    }

    override fun onDestroy() {
        resinStatuses.clear()
    }

    override fun getCount(): Int = resinStatuses.size

    override fun getViewAt(p0: Int): RemoteViews = with(resinStatuses[p0]) {
        RemoteViews(context.packageName, android.R.layout.two_line_list_item).apply {
            setTextViewText(android.R.id.text1, nickname)
            setTextViewText(android.R.id.text2, "$currentResin / $maxResin")
        }
    }

    override fun getLoadingView(): RemoteViews =
        RemoteViews(context.packageName, com.joeloewi.croissant.R.drawable.image_placeholder)

    override fun getViewTypeCount(): Int = 1

    override fun getItemId(p0: Int): Long = resinStatuses[p0].id

    override fun hasStableIds(): Boolean = false

    companion object {
        const val BUNDLE = "bundle"
        const val RESIN_STATUSES = "resinStatuses"
    }
}