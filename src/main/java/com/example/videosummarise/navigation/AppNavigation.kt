package com.example.videosummarise.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.videosummarise.ui.screens.HomeScreen
import com.example.videosummarise.ui.screens.ProcessingScreen
import com.example.videosummarise.ui.screens.ResultScreen
import com.example.videosummarise.ui.screens.SavedSummariesScreen
import com.example.videosummarise.ui.screens.VideoSelectionScreen

/**
 * Navigation destinations for Compose navigation
 * Note: This app currently uses Fragment-based navigation via nav_graph.xml
 * This file provides Compose navigation setup for future use if needed
 */
object AppDestinations {
    const val HOME = "home"
    const val VIDEO_SELECTION = "video_selection"
    const val PROCESSING = "processing"
    const val RESULT = "result"
    const val SAVED_SUMMARIES = "saved_summaries"
}

/**
 * Compose Navigation setup for the VideoSummarise app
 * Currently not in use - app uses Fragment navigation
 */
@Composable
fun AppNavigation(
    navController: NavHostController = rememberNavController(),
    startDestination: String = AppDestinations.HOME
) {
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        // Home Screen
        composable(AppDestinations.HOME) {
            HomeScreen(
                onSelectVideoClick = {
                    navController.navigate(AppDestinations.VIDEO_SELECTION)
                },
                onMySummariesClick = {
                    navController.navigate(AppDestinations.SAVED_SUMMARIES)
                }
            )
        }

        // Video Selection Screen
        composable(AppDestinations.VIDEO_SELECTION) {
            VideoSelectionScreen(
                onVideoSelected = { videoUri ->
                    navController.navigate("${AppDestinations.PROCESSING}/$videoUri") {
                        // Optional: Pop up to home to prevent deep back stack
                        // popUpTo(AppDestinations.HOME)
                    }
                }
            )
        }

        // Processing Screen with video URI argument
        composable(
            route = "${AppDestinations.PROCESSING}/{videoUri}",
            arguments = listOf(
                navArgument("videoUri") {
                    type = NavType.StringType
                    nullable = false
                }
            )
        ) { backStackEntry ->
            val videoUri = backStackEntry.arguments?.getString("videoUri") ?: ""
            ProcessingScreen(
                onProcessingComplete = { summaryId ->
                    navController.navigate("${AppDestinations.RESULT}/$summaryId") {
                        // Pop up to video selection so back button doesn't go to processing screen
                        popUpTo(AppDestinations.VIDEO_SELECTION) {
                            inclusive = true
                        }
                    }
                }
            )
        }

        // Result Screen with summary ID argument
        composable(
            route = "${AppDestinations.RESULT}/{summaryId}",
            arguments = listOf(
                navArgument("summaryId") {
                    type = NavType.StringType
                    nullable = false
                }
            )
        ) { backStackEntry ->
            val summaryId = backStackEntry.arguments?.getString("summaryId") ?: ""
            ResultScreen(
                onSaveClick = {
                    // Handle save action
                    // Could navigate back to home or show confirmation
                },
                onNewVideoClick = {
                    // Navigate back to video selection for new video
                    navController.navigate(AppDestinations.VIDEO_SELECTION) {
                        popUpTo(AppDestinations.HOME)
                    }
                }
            )
        }

        // Saved Summaries Screen
        composable(AppDestinations.SAVED_SUMMARIES) {
            SavedSummariesScreen(
                onSummaryClick = { summaryId ->
                    navController.navigate("${AppDestinations.RESULT}/$summaryId")
                },
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }
    }
}
