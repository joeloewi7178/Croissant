package com.joeloewi.croissant.ui.navigation.main.attendances

import androidx.navigation.NavArgumentBuilder
import androidx.navigation.NavType
import com.joeloewi.croissant.domain.common.LoggableWorker

sealed class AttendancesDestination {
    abstract val arguments: List<Pair<String, NavArgumentBuilder.() -> Unit>>
    protected abstract val plainRoute: String
    val route: String
        get() = "${plainRoute}${
            arguments.map { it.first }.joinToString(
                separator = "/",
                prefix = if (arguments.isEmpty()) {
                    ""
                } else {
                    "/"
                }
            ) { "{$it}" }
        }"

    data object AttendancesScreen : AttendancesDestination() {
        override val arguments: List<Pair<String, NavArgumentBuilder.() -> Unit>> = listOf()
        override val plainRoute = "attendancesScreen"
    }

    data object CreateAttendanceScreen : AttendancesDestination() {
        override val arguments: List<Pair<String, NavArgumentBuilder.() -> Unit>> = listOf()
        override val plainRoute = "createAttendanceScreen"
    }

    data object LoginHoYoLabScreen : AttendancesDestination() {
        override val arguments: List<Pair<String, NavArgumentBuilder.() -> Unit>> = listOf()
        override val plainRoute = "loginHoYoLabScreen"
    }

    class AttendanceDetailScreen : AttendancesDestination() {
        companion object {
            const val ATTENDANCE_ID = "attendanceId"
            const val FROM_DEEPLINK = "fromDeeplink"
        }

        override val arguments: List<Pair<String, (NavArgumentBuilder.() -> Unit)>> = listOf(
            ATTENDANCE_ID to {
                type = NavType.LongType

            },
            FROM_DEEPLINK to {
                type = NavType.BoolType
                defaultValue = false
            }
        )

        override val plainRoute: String = "attendanceDetailScreen"

        fun generateRoute(
            attendanceId: Long,
            fromDeeplink: Boolean = false
        ) = "${plainRoute}/${attendanceId}/${fromDeeplink}"
    }

    class AttendanceLogsCalendarScreen : AttendancesDestination() {
        companion object {
            const val ATTENDANCE_ID = "attendanceId"
            const val LOGGABLE_WORKER = "loggableWorker"
        }

        override val arguments: List<Pair<String, NavArgumentBuilder.() -> Unit>> = listOf(
            ATTENDANCE_ID to {
                type = NavType.LongType
            },
            LOGGABLE_WORKER to {
                type = NavType.EnumType(LoggableWorker::class.java)
            }
        )

        override val plainRoute: String = "attendanceLogsCalendarScreen"

        fun generateRoute(attendanceId: Long, loggableWorker: LoggableWorker) =
            "${plainRoute}/${attendanceId}/${loggableWorker}"
    }

    class AttendanceLogsDayScreen : AttendancesDestination() {
        companion object {
            const val ATTENDANCE_ID = "attendanceId"
            const val LOGGABLE_WORKER = "loggableWorker"
            const val LOCAL_DATE = "localDate"
        }

        override val arguments: List<Pair<String, NavArgumentBuilder.() -> Unit>> = listOf(
            ATTENDANCE_ID to {
                type = NavType.LongType
            },
            LOGGABLE_WORKER to {
                type = NavType.EnumType(LoggableWorker::class.java)
            },
            LOCAL_DATE to {
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
