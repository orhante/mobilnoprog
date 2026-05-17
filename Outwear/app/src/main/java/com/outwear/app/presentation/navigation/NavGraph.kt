package com.outwear.app.presentation.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import com.outwear.app.presentation.ui.screens.addreview.AddReviewScreen
import com.outwear.app.presentation.ui.screens.discover.DiscoverScreen
import com.outwear.app.presentation.ui.screens.home.HomeScreen
import com.outwear.app.presentation.ui.screens.product.ProductDetailScreen
import com.outwear.app.presentation.ui.screens.profile.ProfileScreen
import com.outwear.app.presentation.ui.screens.review.ReviewsScreen

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object Discover : Screen("discover")
    object Profile : Screen("profile")
    object ProductDetail : Screen("product/{itemId}") {
        fun createRoute(itemId: Long) = "product/$itemId"
    }
    object Reviews : Screen("reviews/{itemId}/{itemName}") {
        fun createRoute(itemId: Long, itemName: String) = "reviews/$itemId/${itemName.replace("/", "_")}"
    }
    object AddReview : Screen("add_review/{itemId}/{itemName}") {
        fun createRoute(itemId: Long, itemName: String) = "add_review/$itemId/${itemName.replace("/", "_")}"
    }
}

data class BottomNavItem(
    val screen: Screen,
    val label: String,
    val icon: ImageVector,
    val selectedIcon: ImageVector
)

val bottomNavItems = listOf(
    BottomNavItem(Screen.Home, "Home", Icons.Filled.Home, Icons.Filled.Home),
    BottomNavItem(Screen.Discover, "Discover", Icons.Filled.Search, Icons.Filled.Search),
    BottomNavItem(Screen.Profile, "Profile", Icons.Filled.Person, Icons.Filled.Person)
)

@Composable
fun OutwearNavGraph() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    val showBottomBar = currentDestination?.hierarchy?.any { dest ->
        bottomNavItems.any { it.screen.route == dest.route }
    } == true

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                NavigationBar {
                    bottomNavItems.forEach { item ->
                        val selected = currentDestination?.hierarchy?.any {
                            it.route == item.screen.route
                        } == true
                        NavigationBarItem(
                            selected = selected,
                            onClick = {
                                navController.navigate(item.screen.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            icon = {
                                Icon(
                                    imageVector = if (selected) item.selectedIcon else item.icon,
                                    contentDescription = item.label
                                )
                            },
                            label = { Text(item.label) }
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Home.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Home.route) {
                HomeScreen(
                    onItemClick = { itemId ->
                        navController.navigate(Screen.ProductDetail.createRoute(itemId))
                    }
                )
            }
            composable(Screen.Discover.route) {
                DiscoverScreen(
                    onItemClick = { itemId ->
                        navController.navigate(Screen.ProductDetail.createRoute(itemId))
                    }
                )
            }
            composable(Screen.Profile.route) {
                ProfileScreen(
                    onReviewClick = { itemId ->
                        navController.navigate(Screen.ProductDetail.createRoute(itemId))
                    }
                )
            }
            composable(
                route = Screen.ProductDetail.route,
                arguments = listOf(navArgument("itemId") { type = NavType.LongType })
            ) { backStackEntry ->
                val itemId = backStackEntry.arguments?.getLong("itemId") ?: return@composable
                ProductDetailScreen(
                    itemId = itemId,
                    onNavigateBack = { navController.popBackStack() },
                    onSeeAllReviews = { id, name ->
                        navController.navigate(Screen.Reviews.createRoute(id, name))
                    },
                    onWriteReview = { id, name ->
                        navController.navigate(Screen.AddReview.createRoute(id, name))
                    }
                )
            }
            composable(
                route = Screen.Reviews.route,
                arguments = listOf(
                    navArgument("itemId") { type = NavType.LongType },
                    navArgument("itemName") { type = NavType.StringType }
                )
            ) { backStackEntry ->
                val itemId = backStackEntry.arguments?.getLong("itemId") ?: return@composable
                val itemName = backStackEntry.arguments?.getString("itemName") ?: ""
                ReviewsScreen(
                    itemId = itemId,
                    itemName = itemName,
                    onNavigateBack = { navController.popBackStack() },
                    onWriteReview = {
                        navController.navigate(Screen.AddReview.createRoute(itemId, itemName))
                    }
                )
            }
            composable(
                route = Screen.AddReview.route,
                arguments = listOf(
                    navArgument("itemId") { type = NavType.LongType },
                    navArgument("itemName") { type = NavType.StringType }
                )
            ) { backStackEntry ->
                val itemId = backStackEntry.arguments?.getLong("itemId") ?: return@composable
                val itemName = backStackEntry.arguments?.getString("itemName") ?: ""
                AddReviewScreen(
                    itemId = itemId,
                    itemName = itemName,
                    onNavigateBack = { navController.popBackStack() },
                    onReviewSubmitted = {
                        navController.popBackStack()
                        navController.popBackStack()
                    }
                )
            }
        }
    }
}
