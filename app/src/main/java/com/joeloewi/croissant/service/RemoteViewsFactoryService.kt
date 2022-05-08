package com.joeloewi.croissant.service

import android.content.Context
import android.content.Intent
import android.os.Parcelable
import android.util.Log
import android.widget.RemoteViews
import android.widget.RemoteViewsService
import com.joeloewi.croissant.R
import kotlinx.parcelize.Parcelize

class RemoteViewsFactoryService : RemoteViewsService() {
    override fun onGetViewFactory(p0: Intent?): RemoteViewsFactory =
        ListRemoteViewsFactory(
            context = applicationContext,
            intent = p0
        )

    class ListRemoteViewsFactory(
        private val context: Context,
        private val intent: Intent?
    ) : RemoteViewsFactory {
        private val resinStatuses = arrayListOf<ResinStatus>()

        private fun setResinStatuses(intent: Intent?) {
            resinStatuses.apply {
                addAll(
                    intent?.getParcelableArrayListExtra(RESIN_STATUSES)
                        ?: listOf()
                )
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
            RemoteViews(context.packageName, R.drawable.image_placeholder)

        override fun getViewTypeCount(): Int = 1

        override fun getItemId(p0: Int): Long = resinStatuses[p0].id

        override fun hasStableIds(): Boolean = false

        companion object {
            const val RESIN_STATUSES = "resinStatuses"
        }
    }

    @Parcelize
    data class ResinStatus(
        val id: Long = 0,
        val nickname: String = "",
        val currentResin: Int = 0,
        val maxResin: Int = 0
    ) : Parcelable
}