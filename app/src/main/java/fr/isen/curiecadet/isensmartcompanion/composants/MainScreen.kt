package fr.isen.curiecadet.isensmartcompanion.composants

import android.annotation.SuppressLint
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalContext
import fr.isen.curiecadet.isensmartcompanion.R
import fr.isen.curiecadet.isensmartcompanion.api.AppDatabase
import fr.isen.curiecadet.isensmartcompanion.api.Interaction
import fr.isen.curiecadet.isensmartcompanion.ia.generateText
import kotlinx.coroutines.launch

@SuppressLint("SimpleDateFormat")
@Composable
fun MainScreen() {
    var userInput by remember { mutableStateOf("") } // Stocke la question posée par l'utilisateur
    val context = LocalContext.current
    val db = AppDatabase.getDatabase(context) // Accéder à la base de données
    val interactionDao = db.interactionDao()
    val questionsAndResponses = remember { mutableStateListOf<Interaction>() } // Historique des interactions

    // Utilisation de rememberCoroutineScope pour accéder à un CoroutineScope
    val coroutineScope = rememberCoroutineScope()

    // Charger l'historique des interactions
    LaunchedEffect(Unit) {
        questionsAndResponses.clear()
        questionsAndResponses.addAll(interactionDao.getAllInteractions()) // Charger l'historique
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Image en haut avec le nom "Isen"
        val isenImage: Painter = painterResource(id = R.drawable.isen) // Assurez-vous que l'image isen est dans le dossier res/drawable
        Image(
            painter = isenImage,
            contentDescription = "Isen",
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp) // Taille de l'image en haut
                .padding(bottom = 16.dp)
        )

        // Affichage de l'historique des interactions
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .weight(1f) // Prendre tout l'espace disponible au-dessus de la zone de texte et du bouton
        ) {
            questionsAndResponses.forEach { interaction ->
                Column(modifier = Modifier.padding(vertical = 8.dp)) {
                    // Affichage de la date au-dessus des questions/réponses
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
                }
            }
        }

        // Zone de texte pour entrer une question et bouton pour envoyer la question
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.CenterHorizontally),
            horizontalArrangement = Arrangement.spacedBy(16.dp) // Espacement entre le TextField et le Button
        ) {
            // Zone de texte pour entrer une question
            TextField(
                value = userInput,
                onValueChange = { userInput = it },
                label = { Text("Posez une question ?") },
                modifier = Modifier
                    .weight(1f) // Laisser le TextField prendre tout l'espace disponible
            )

            // Bouton pour envoyer la question et générer une réponse
            Button(
                onClick = {
                    if (userInput.isNotBlank()) {
                        // Générer la réponse
                        coroutineScope.launch {
                            try {
                                val response = generateText(userInput)
                                val interaction = Interaction(
                                    question = userInput,
                                    response = response
                                )
                                // Sauvegarder dans la base de données
                                interactionDao.insert(interaction)
                                questionsAndResponses.add(0, interaction) // Ajouter à l'historique en tête
                                userInput = "" // Réinitialiser le champ de texte
                            } catch (e: Exception) {
                                Toast.makeText(context, "Erreur : ${e.message}", Toast.LENGTH_SHORT).show()
                            }
                        }
                    } else {
                        Toast.makeText(context, "Veuillez entrer une question.", Toast.LENGTH_SHORT).show()
                    }
                },
                modifier = Modifier.align(Alignment.CenterVertically).padding(bottom = 100.dp)
            ) {
                Text("Envoyer")
            }
        }

        // Ajouter un espace pour que les éléments ne se touchent pas
        Spacer(modifier = Modifier.height(16.dp)) // Ajouter un peu d'espace sous le bouton
    }
}
