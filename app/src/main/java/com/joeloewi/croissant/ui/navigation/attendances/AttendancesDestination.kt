package com.joeloewi.croissant.ui.navigation.attendances

sealed class AttendancesDestination(val route: String) {
    object AttendancesScreen : AttendancesDestination(route = "attendancesScreen")
    object CreateAttendanceScreen : AttendancesDestination(route = "createAttendanceScreen")
    object LoginHoYoLabScreen : AttendancesDestination(route = "loginHoYoLabScreen")
}
