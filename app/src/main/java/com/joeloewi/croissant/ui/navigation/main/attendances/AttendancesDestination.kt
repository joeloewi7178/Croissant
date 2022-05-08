package com.joeloewi.croissant.ui.navigation.main.attendances

import androidx.navigation.NavType
import com.joeloewi.domain.common.LoggableWorker

sealed class AttendancesDestination(val route: String) {
    object AttendancesScreen : AttendancesDestination(route = "attendancesScreen")
    object CreateAttendanceScreen : AttendancesDestination(route = "createAttendanceScreen")
    object LoginHoYoLabScreen : AttendancesDestination(route = "loginHoYoLabScreen")
    data class AttendanceDetailScreen(
        val arguments: List<Pair<String, NavType<*>>> = listOf(
            ATTENDANCE_ID to NavType.LongType
        ),
        val plainRoute: String = "attendanceDetailScreen"
    ) : AttendancesDestination(
        route = "${plainRoute}${
            arguments.map { it.first }.joinToString(
                separator = "/",
                prefix = "/"
            ) { "{$it}" }
        }"
    ) {
        companion object {
            const val ATTENDANCE_ID = "attendanceId"
        }

        fun generateRoute(attendanceId: Long) = "${plainRoute}/${attendanceId}"
    }

    data class AttendanceLogsScreen(
        val arguments: List<Pair<String, NavType<*>>> = listOf(
            ATTENDANCE_ID to NavType.LongType,
            LOGGABLE_WORKER to NavType.EnumType(LoggableWorker::class.java)
        ),
        val plainRoute: String = "attendanceLogsScreen"
    ) : AttendancesDestination(
        route = "${plainRoute}${
            arguments.map { it.first }.joinToString(
                separator = "/",
                prefix = "/"
            ) { "{$it}" }
        }"
    ) {
        companion object {
            const val ATTENDANCE_ID = "attendanceId"
            const val LOGGABLE_WORKER = "loggableWorker"
        }

        fun generateRoute(attendanceId: Long, loggableWorker: LoggableWorker) =
            "${plainRoute}/${attendanceId}/${loggableWorker}"
    }
}
