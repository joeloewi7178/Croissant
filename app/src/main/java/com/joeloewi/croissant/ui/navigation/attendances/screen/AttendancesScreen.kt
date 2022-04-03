package com.joeloewi.croissant.ui.navigation.attendances.screen

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import com.joeloewi.croissant.ui.navigation.attendances.AttendancesDestination

@ExperimentalMaterial3Api
@Composable
fun AttendancesScreen(
    navController: NavController,
) {

    AttendancesContent(
        onCreateAttendanceClick = {
            navController.navigate(AttendancesDestination.CreateAttendanceScreen.route)
        }
    )
}

@ExperimentalMaterial3Api
@Composable
fun AttendancesContent(
    onCreateAttendanceClick: () -> Unit
) {

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = onCreateAttendanceClick
            ) {
                Icon(
                    imageVector = Icons.Outlined.Add,
                    contentDescription = Icons.Outlined.Add.name
                )
            }
        }
    ) {

    }
}