package com.joeloewi.croissant.ui.navigation.reminders

sealed class RemindersDestination(val route: String) {
    object RemindersScreen : RemindersDestination(route = "remindersScreen")
}
