package fr.isen.curiecadet.isensmartcompanion

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Checklist
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import fr.isen.curiecadet.isensmartcompanion.composants.EventScreen
import fr.isen.curiecadet.isensmartcompanion.composants.MainScreen
import fr.isen.curiecadet.isensmartcompanion.composants.TabView
import fr.isen.curiecadet.isensmartcompanion.ui.theme.ISENSmartCompanionTheme


data class TabBarItem(
    val title: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val badgeAmount: Int? = null
)

class MainActivity : ComponentActivity() {
    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {

            // Utilisation des icônes intégrées de Jetpack Compose
            val mainTab = TabBarItem(
                title = "main",
                selectedIcon = Icons.Filled.Home, // Icône maison pour l'état sélectionné
                unselectedIcon = Icons.Filled.Home // Icône maison pour l'état non sélectionné
            )
            val eventsTab = TabBarItem(
                title = "events",
                selectedIcon = Icons.Filled.Event, // Icône événement pour l'état sélectionné
                unselectedIcon = Icons.Filled.Event // Icône événement pour l'état non sélectionné
            )
            val agendaTab = TabBarItem(
                title = "Agenda",
                selectedIcon = Icons.Filled.Checklist, // Icône liste pour l'état sélectionné
                unselectedIcon = Icons.Filled.Checklist // Icône liste pour l'état non sélectionné
            )
            val historyTab = TabBarItem(
                title = "History",
                selectedIcon = Icons.Filled.History, // Icône historique pour l'état sélectionné
                unselectedIcon = Icons.Filled.History // Icône historique pour l'état non sélectionné
            )

            val tabBarItems = listOf(mainTab, eventsTab, agendaTab, historyTab)

            val navController = rememberNavController()

            ISENSmartCompanionTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Scaffold(bottomBar = { TabView(tabBarItems, navController) }) {
                        NavHost(navController = navController, startDestination = mainTab.title) {
                            composable(mainTab.title) {
                                MainScreen()
                            }
                            composable(eventsTab.title) {
                                EventScreen()
                            }
                            composable(agendaTab.title) {
                                Text(agendaTab.title)
                            }
                            composable(historyTab.title) {
                                Text(historyTab.title)
                            }
                        }
                    }
                }
            }
        }
    }
}





@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    ISENSmartCompanionTheme {
        MainScreen()}}


