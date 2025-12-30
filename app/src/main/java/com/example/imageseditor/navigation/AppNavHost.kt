package com.example.imageseditor.navigation


import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.imageseditor.screen.ImageEditScreen
import com.example.imageseditor.screen.ImagePreviewScreen
import com.example.imageseditor.screen.SinglePickerScreen

@Composable
fun AppNavHost() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "picker"
    ) {

        composable("picker") {
            SinglePickerScreen { uri ->
                navController.navigate("preview/${Uri.encode(uri.toString())}")
            }
        }

        composable(
            route = "preview/{uri}",
            arguments = listOf(
                navArgument("uri") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val uriString = backStackEntry.arguments?.getString("uri")!!
            val uri = Uri.parse(uriString)

            ImagePreviewScreen(
                imageUri = uri,
                onBack = { navController.popBackStack() },
                onEditClick = { encodedUri ->
                    navController.navigate("edit/$encodedUri")
                }
            )

        }
        composable(
            route = "edit/{uri}",
            arguments = listOf(
                navArgument("uri") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val uriString = backStackEntry.arguments?.getString("uri")!!
            val uri = Uri.parse(uriString)

            ImageEditScreen(
                imageUri = uri,
                onBack = { navController.popBackStack() }
            )
        }

    }
}
