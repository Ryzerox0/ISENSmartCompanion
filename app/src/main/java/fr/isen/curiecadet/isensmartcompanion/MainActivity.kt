package fr.isen.curiecadet.isensmartcompanion  // Déclare le package dans lequel cette classe se trouve.

import android.os.Bundle  // Permet d'utiliser la classe Bundle pour la gestion d'état de l'application.
import android.widget.Toast  // Permet d'afficher des messages temporaires à l'utilisateur.

import androidx.activity.ComponentActivity  // Composant de base pour les activités Android utilisant Jetpack Compose.
import androidx.activity.compose.setContent  // Permet de définir le contenu de l'activité avec Jetpack Compose.

import androidx.compose.foundation.Image  // Permet d'afficher des images.
import androidx.compose.foundation.layout.*  // Permet d'utiliser des layouts flexibles pour les composants.
import androidx.compose.foundation.lazy.LazyColumn  // Permet d'afficher une liste qui se charge de manière paresseuse.
import androidx.compose.foundation.lazy.items  // Permet de générer des éléments de liste dans un LazyColumn.

import androidx.compose.material3.*  // Fournit des composants visuels de la bibliothèque Material Design 3.
import androidx.compose.runtime.*  // Permet d'utiliser des états et des effets réactifs dans les composables.
import androidx.compose.ui.Alignment  // Permet d'aligner les éléments dans les layouts.
import androidx.compose.ui.Modifier  // Permet de modifier les propriétés des composants.
import androidx.compose.ui.graphics.Color  // Permet de manipuler les couleurs.
import androidx.compose.ui.res.painterResource  // Permet de charger une ressource d'image.
import androidx.compose.ui.tooling.preview.Preview  // Permet de générer des aperçus des interfaces dans l'éditeur.
import androidx.compose.ui.unit.dp  // Permet de définir des dimensions en "dp" (density-independent pixels).
import androidx.compose.ui.text.input.TextFieldValue  // Permet de manipuler le texte dans un champ de saisie.
import androidx.compose.ui.platform.LocalContext  // Permet d'accéder au contexte local, comme pour afficher un Toast.
import androidx.compose.ui.focus.FocusRequester  // Permet de demander ou contrôler le focus d'un champ.
import androidx.compose.ui.focus.focusRequester  // Permet d'associer un FocusRequester à un champ.
import androidx.compose.ui.focus.onFocusChanged  // Permet de détecter les changements de focus.
import fr.isen.curiecadet.isensmartcompanion.ui.theme.ISENSmartCompanionTheme  // Importation du thème de l'application.
import java.util.Calendar  // Permet de travailler avec la date et l'heure actuelles.

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {  // Définit le contenu de l'activité avec Jetpack Compose.
            ISENSmartCompanionTheme {  // Applique le thème de l'application.
                MainScreen { userInput ->
                    getAssistantResponse(userInput)

                } // Affiche l'écran principal de l'application.
            }
        }
    }


    // Fonction qui génère une réponse de l'assistant en fonction de la saisie de l'utilisateur.
    private fun getAssistantResponse(userInput: String): String {
        return when {
            userInput.contains(
                "bonjour",
                ignoreCase = true
            ) -> "Assistant: Bonjour ! Comment puis-je vous aider ?"

            userInput.contains("heure", ignoreCase = true) -> {
                val calendar = Calendar.getInstance()
                val hour = calendar.get(Calendar.HOUR_OF_DAY)
                val minute = calendar.get(Calendar.MINUTE)
                "Assistant: Il est ${hour}:${minute}."
            }

            userInput.contains(
                "nom",
                ignoreCase = true
            ) -> "Assistant: Je suis votre assistant intelligent !"

            else -> "Assistant: Merci de votre question, je vais chercher tout de suite."
        }
    }
}

@Composable
fun MainScreen(getAssistantResponse: (String) -> String) {
    // Déclare un état mutable pour le texte du champ de saisie.
    var textState by remember { mutableStateOf(TextFieldValue("")) }
    // Déclare un état mutable pour la liste des textes affichés.
    var textsList by remember { mutableStateOf(listOf<String>()) }
    // Récupère le contexte local pour afficher un Toast.
    val context = LocalContext.current

    // Garde une trace de si le message de bienvenue a déjà été montré.
    var hasGreeted by remember { mutableStateOf(false) }


    // Si le message de bienvenue n'a pas encore été montré, on l'ajoute à la liste des messages.
    if (!hasGreeted) {
        textsList = textsList + "Assistant: Bonjour ! Comment puis-je vous aider ?"
        hasGreeted = true  // Marque que le message de bienvenue a été montré.
    }

    // Définition de la mise en page de l'écran.
    Column(
        modifier = Modifier
            .fillMaxSize()  // Remplit tout l'espace disponible.
            .padding(16.dp)  // Ajoute une marge intérieure de 16 dp.
    ) {
        // Affichage de l'image en haut de l'écran.
        Image(
            painter = painterResource(id = R.drawable.isen),  // Charge l'image depuis les ressources.
            contentDescription = "Logo",  // Description de l'image pour l'accessibilité.
            modifier = Modifier
                .fillMaxWidth()  // L'image occupe toute la largeur de l'écran.
                .height(250.dp)  // Définit la hauteur de l'image à 250 dp.
                .padding(top = 32.dp, bottom = 16.dp)  // Ajoute des marges verticales.
                .align(Alignment.CenterHorizontally)  // Centre l'image horizontalement.
        )

        // Liste défilante (LazyColumn) pour afficher les messages.
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()  // La liste occupe toute la largeur.
                .weight(1f),  // La liste prend toute la place disponible restante.
            horizontalAlignment = Alignment.CenterHorizontally,  // Centre les éléments horizontalement.
            verticalArrangement = Arrangement.Center  // Centre les éléments verticalement.
        ) {
            // Parcours la liste de textes et affiche chaque élément.
            items(textsList) { text ->
                val isUserMessage =
                    text.startsWith("Question:")  // Vérifie si le message vient de l'utilisateur.
                BubbleMessage(
                    text = text,
                    isUserMessage = isUserMessage
                )  // Affiche chaque message dans une bulle.
            }
        }

        // Ligne contenant le champ de texte et le bouton d'envoi.
        Row(
            modifier = Modifier
                .fillMaxWidth()  // Remplit toute la largeur.
                .padding(bottom = 16.dp),  // Ajoute une marge en bas.
            horizontalArrangement = Arrangement.SpaceBetween  // Espace entre les éléments dans la ligne.
        ) {
            // FocusRequester pour suivre l'état du focus du champ de texte.
            val focusRequester = remember { FocusRequester() }
            var isFocused by remember { mutableStateOf(false) }  // Suit l'état du focus du champ.

            // Champ de texte pour la saisie de la question.
            TextField(
                value = textState,  // La valeur du champ de texte.
                onValueChange = {
                    textState = it
                },  // Met à jour l'état lorsque l'utilisateur tape.
                modifier = Modifier
                    .weight(1f)  // Le champ de texte prend la place restante.
                    .padding(end = 8.dp)  // Ajoute une marge à droite.
                    .focusRequester(focusRequester)  // Associe le FocusRequester au champ.
                    .onFocusChanged { focusState ->  // Surveille les changements de focus.
                        isFocused = focusState.isFocused
                    },
                label = { Text("Avez-vous une question ?") }  // Texte du label du champ de texte.
            )

            // Bouton pour envoyer la question.
            Button(
                onClick = {
                    if (textState.text.isNotEmpty()) {  // Si le champ n'est pas vide.
                        // Ajoute la question de l'utilisateur à la liste des textes.
                        textsList = textsList + "Question: ${textState.text}"
                        // Obtient la réponse de l'assistant.
                        val assistantResponse = getAssistantResponse(textState.text)
                        textsList = textsList + assistantResponse  // Ajoute la réponse à la liste.
                        // Vide le champ de texte.
                        textState = TextFieldValue("")

                        // Affiche un Toast pour indiquer que la question a été envoyée.
                        Toast.makeText(context, "Question envoyée", Toast.LENGTH_SHORT).show()
                    }
                },
                modifier = Modifier.align(Alignment.CenterVertically)  // Centre verticalement le bouton.
            ) {
                Text(text = "Envoyer")  // Texte du bouton.
            }
        }
    }
}

// Composant pour afficher un message dans une bulle.
@Composable
fun BubbleMessage(text: String, isUserMessage: Boolean) {
    // Détermine la couleur de fond de la bulle en fonction du type de message.
    val backgroundColor = if (isUserMessage) Color(0xFFDCF8C6) else Color(0xFFEFEFEF)

    Row(
        modifier = Modifier
            .fillMaxWidth()  // Remplit toute la largeur.
            .padding(4.dp),  // Ajoute une marge autour du message.
        horizontalArrangement = if (isUserMessage) Arrangement.End else Arrangement.Start  // Aligne le message à droite pour l'utilisateur et à gauche pour l'assistant.
    ) {
        Surface(
            modifier = Modifier
                .padding(8.dp)  // Ajoute une marge autour de la surface.
                .fillMaxWidth(0.8f),  // Remplie 80% de la largeur.
            color = backgroundColor,  // Utilise la couleur de fond calculée.
            shape = MaterialTheme.shapes.medium,  // Forme arrondie pour la bulle.
            shadowElevation = 4.dp  // Ajoute une ombre sous la bulle.
        ) {
            Text(
                text = text,  // Le texte du message.
                modifier = Modifier.padding(12.dp),  // Ajoute un padding intérieur au texte.
                style = MaterialTheme.typography.bodyLarge  // Applique un style de texte large.
            )
        }
    }
}

// Fonction pour afficher un aperçu de l'interface.
@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    ISENSmartCompanionTheme {  // Applique le thème.
        MainScreen() { return@MainScreen "" } // Affiche l'écran principal dans l'aperçu.
    }
}
