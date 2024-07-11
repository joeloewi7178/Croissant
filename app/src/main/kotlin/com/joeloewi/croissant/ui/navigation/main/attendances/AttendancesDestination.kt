package com.joeloewi.croissant.ui.navigation.main.attendances

import android.content.Context
import android.net.Uri
import androidx.compose.runtime.Immutable
import androidx.navigation.NamedNavArgument
import androidx.navigation.NavDeepLink
import androidx.navigation.NavType
import androidx.navigation.navArgument
import androidx.navigation.navDeepLink
import com.joeloewi.croissant.R
import com.joeloewi.croissant.core.data.model.LoggableWorker
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

@Immutable
sealed class AttendancesDestination {
    abstract val arguments: ImmutableList<NamedNavArgument>
    open val deepLinks: ImmutableList<NavDeepLink> = persistentListOf()
    protected abstract val plainRoute: String
    open val route: String
        get() = "${plainRoute}${
            arguments.map { it.name }.joinToString(
                separator = "/",
                prefix = if (arguments.isEmpty()) {
                    ""
                } else {
                    "/"
                }
            ) { "{$it}" }
        }"

    data object AttendancesScreen : AttendancesDestination() {
        override val arguments: ImmutableList<NamedNavArgument> = persistentListOf()
        override val plainRoute = "attendancesScreen"
    }

    data object CreateAttendanceScreen : AttendancesDestination() {
        override val arguments: ImmutableList<NamedNavArgument> = persistentListOf()
        override val plainRoute = "createAttendanceScreen"
    }

    data object LoginHoYoLabScreen : AttendancesDestination() {
        const val COOKIE = "cookie"

        override val arguments: ImmutableList<NamedNavArgument> = persistentListOf()
        override val plainRoute = "loginHoYoLabScreen"
    }

    data object AttendanceDetailScreen : AttendancesDestination() {
        const val ATTENDANCE_ID = "attendanceId"

        override val arguments: ImmutableList<NamedNavArgument> = persistentListOf(
            navArgument(ATTENDANCE_ID) {
                type = NavType.LongType
                defaultValue = -1L
            }
        )

        override val plainRoute: String = "attendanceDetailScreen"

        override val route: String
            get() = "${plainRoute}/{${ATTENDANCE_ID}}"

        override val deepLinks: ImmutableList<NavDeepLink> = persistentListOf(
            navDeepLink {
                uriPattern = "${
                    Uri.Builder()
                        .scheme("app")
                        .authority("com.joeloewi.croissant")
                        .build()
                }/$route"
            }
        )

        fun generateRoute(
            attendanceId: Long
        ) = "${plainRoute}/$attendanceId"

        fun generateDeepLinkUri(
            context: Context,
            attendanceId: Long
        ): Uri = Uri.Builder()
            .scheme(context.getString(R.string.deep_link_scheme))
            .authority(context.packageName)
            .appendEncodedPath(plainRoute)
            .appendPath("$attendanceId")
            .build()
    }

    data object AttendanceLogsCalendarScreen : AttendancesDestination() {
        const val ATTENDANCE_ID = "attendanceId"
        const val LOGGABLE_WORKER = "loggableWorker"

        override val arguments: ImmutableList<NamedNavArgument> = persistentListOf(
            navArgument(ATTENDANCE_ID) {
                type = NavType.LongType
            },
            navArgument(LOGGABLE_WORKER) {
                type = NavType.EnumType(LoggableWorker::class.java)
            }
        )

        override val plainRoute: String = "attendanceLogsCalendarScreen"

        fun generateRoute(attendanceId: Long, loggableWorker: LoggableWorker) =
            "${plainRoute}/${attendanceId}/${loggableWorker}"
    }

    data object AttendanceLogsDayScreen : AttendancesDestination() {
        const val ATTENDANCE_ID = "attendanceId"
        const val LOGGABLE_WORKER = "loggableWorker"
        const val LOCAL_DATE = "localDate"

        override val arguments: ImmutableList<NamedNavArgument> = persistentListOf(
            navArgument(ATTENDANCE_ID) {
                type = NavType.LongType
            },
            navArgument(LOGGABLE_WORKER) {
                type = NavType.EnumType(LoggableWorker::class.java)
            },
            navArgument(LOCAL_DATE) {
                type = NavType.StringType
            }
        )

        override val plainRoute: String = "attendanceLogsDayScreen"

        fun generateRoute(
            attendanceId: Long,
            loggableWorker: LoggableWorker,
            localDate: String
        ) = "${plainRoute}/${attendanceId}/${loggableWorker}/${localDate}"
    }
}
