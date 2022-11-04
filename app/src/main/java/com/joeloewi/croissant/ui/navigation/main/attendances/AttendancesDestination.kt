package com.joeloewi.croissant.ui.navigation.main.attendances

import androidx.navigation.NavType
import com.joeloewi.domain.common.LoggableWorker

sealed class AttendancesDestination {
    abstract val arguments: List<Pair<String, NavType<*>>>
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

    object AttendancesScreen : AttendancesDestination() {
        override val arguments: List<Pair<String, NavType<*>>> = listOf()
        override val plainRoute = "attendancesScreen"
    }

    object CreateAttendanceScreen : AttendancesDestination() {
        override val arguments: List<Pair<String, NavType<*>>> = listOf()
        override val plainRoute = "createAttendanceScreen"
    }

    object LoginHoYoLabScreen : AttendancesDestination() {
        override val arguments: List<Pair<String, NavType<*>>> = listOf()
        override val plainRoute = "loginHoYoLabScreen"
    }

    class AttendanceDetailScreen : AttendancesDestination() {
        companion object {
            const val ATTENDANCE_ID = "attendanceId"
        }

        override val arguments: List<Pair<String, NavType<*>>> = listOf(
            ATTENDANCE_ID to NavType.LongType
        )

        override val plainRoute: String = "attendanceDetailScreen"

        fun generateRoute(attendanceId: Long) = "${plainRoute}/${attendanceId}"
    }

    class AttendanceLogsCalendarScreen : AttendancesDestination() {
        companion object {
            const val ATTENDANCE_ID = "attendanceId"
            const val LOGGABLE_WORKER = "loggableWorker"
        }

        override val arguments: List<Pair<String, NavType<*>>> = listOf(
            ATTENDANCE_ID to NavType.LongType,
            LOGGABLE_WORKER to NavType.EnumType(LoggableWorker::class.java)
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

        override val arguments: List<Pair<String, NavType<*>>> = listOf(
            ATTENDANCE_ID to NavType.LongType,
            LOGGABLE_WORKER to NavType.EnumType(LoggableWorker::class.java),
            LOCAL_DATE to NavType.StringType
        )

        override val plainRoute: String = "attendanceLogsDayScreen"

        fun generateRoute(
            attendanceId: Long,
            loggableWorker: LoggableWorker,
            localDate: String
        ) = "${plainRoute}/${attendanceId}/${loggableWorker}/${localDate}"
    }
}
