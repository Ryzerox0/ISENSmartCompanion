package fr.isen.curiecadet.isensmartcompanion.composants

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalContext
import fr.isen.curiecadet.isensmartcompanion.api.AppDatabase
import fr.isen.curiecadet.isensmartcompanion.api.Interaction
import kotlinx.coroutines.launch

@Composable
fun HistoryScreen() {
    val context = LocalContext.current
    val db = AppDatabase.getDatabase(context)
    val interactionDao = db.interactionDao()
    val interactions = remember { mutableStateListOf<Interaction>() }

    // Utilisation de rememberCoroutineScope pour avoir accès à un CoroutineScope
    val coroutineScope = rememberCoroutineScope()

    // Charger l'historique des interactions
    LaunchedEffect(Unit) {
        interactions.clear()
        interactions.addAll(interactionDao.getAllInteractions())
    }

    // Column enveloppée dans un scrollable pour permettre le défilement
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(scrollState) // Ajout du scroll vertical
    ) {
        interactions.forEach { interaction ->
            Column(modifier = Modifier.padding(vertical = 8.dp)) {
                // Affichage de la date au-dessus des questions et réponses
                Text(
                    text = java.text.SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(java.util.Date(interaction.timestamp)),
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(bottom = 4.dp)
                )

                // Bulle pour la question
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                        .background(Color.LightGray, shape = MaterialTheme.shapes.medium)
                        .padding(16.dp)
                ) {
                    Text(text = "Question: ${interaction.question}")
                }

                // Bulle pour la réponse
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                        .background(Color(0xFFDCF8C6), shape = MaterialTheme.shapes.medium) // Couleur typique pour les bulles de réponse
                        .padding(16.dp)
                ) {
                    Text(text = "Réponse: ${interaction.response}")
                }

                // Icône de suppression
                IconButton(onClick = {
                    // Supprimer l'interaction de la base de données
                    coroutineScope.launch {
                        interactionDao.deleteById(interaction.id)  // Supprimer via l'ID
                        interactions.remove(interaction)
                    }
                }) {
                    Icon(Icons.Filled.Delete, contentDescription = "Delete Interaction")
                }
            }
        }

        Button(
            onClick = {
                coroutineScope.launch {
                    interactionDao.deleteAllInteractions()
                    interactions.clear()
                }
            },
            modifier = Modifier.align(Alignment.CenterHorizontally).padding(bottom = 100.dp)
        ) {
            Text("Supprimer l'historique")
        }
    }
}
