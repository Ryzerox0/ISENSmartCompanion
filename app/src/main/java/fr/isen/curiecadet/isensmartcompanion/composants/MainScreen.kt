package fr.isen.curiecadet.isensmartcompanion.composants

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import fr.isen.curiecadet.isensmartcompanion.R
import java.util.Calendar

// Définir la fonction getAssistantResponse en dehors de la composable
fun getAssistantResponse(userInput: String): String {
    return when {
        userInput.contains("bonjour", ignoreCase = true) -> "Assistant: Bonjour ! Comment puis-je vous aider ?"
        userInput.contains("heure", ignoreCase = true) -> {
            val calendar = Calendar.getInstance()
            val hour = calendar.get(Calendar.HOUR_OF_DAY)
            val minute = calendar.get(Calendar.MINUTE)
            "Assistant: Il est ${hour}:${minute}."
        }
        userInput.contains("nom", ignoreCase = true) -> "Assistant: Je suis votre assistant intelligent !"
        else -> "Assistant: Merci de votre question, je vais chercher tout de suite."
    }
}

@Composable
fun MainScreen() {
    var textState by remember { mutableStateOf(TextFieldValue("")) }
    var textsList by remember { mutableStateOf(listOf<String>()) }
    val context = LocalContext.current
    var hasGreeted by remember { mutableStateOf(false) }

    // Si le message de bienvenue n'a pas encore été montré, on l'ajoute à la liste des messages
    if (!hasGreeted) {
        textsList = textsList + "Assistant: Bonjour ! Comment puis-je vous aider ?"
        hasGreeted = true
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Top
    ) {
        Image(
            painter = painterResource(id = R.drawable.isen),
            contentDescription = "Logo",
            modifier = Modifier
                .fillMaxWidth()
                .height(250.dp)
                .padding(top = 32.dp, bottom = 16.dp)
                .align(Alignment.CenterHorizontally)
        )

        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            items(textsList) { text ->
                val isUserMessage = text.startsWith("Question:")
                BubbleMessage(
                    text = text,
                    isUserMessage = isUserMessage
                )
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 100.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            val focusRequester = remember { FocusRequester() }
            var isFocused by remember { mutableStateOf(false) }

            TextField(
                value = textState,
                onValueChange = {
                    textState = it
                },
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 8.dp)
                    .focusRequester(focusRequester)
                    .onFocusChanged { focusState ->
                        isFocused = focusState.isFocused
                    },
                label = { Text("Avez-vous une question ?") }
            )

            Button(
                onClick = {
                    if (textState.text.isNotEmpty()) {
                        textsList = textsList + "Question: ${textState.text}"
                        // Appel de la fonction getAssistantResponse pour obtenir la réponse
                        val assistantResponse = getAssistantResponse(textState.text)
                        textsList = textsList + assistantResponse
                        textState = TextFieldValue("")
                        Toast.makeText(context, "Question envoyée", Toast.LENGTH_SHORT).show()
                    }
                },
                modifier = Modifier.align(Alignment.CenterVertically)
            ) {
                Text(text = "Envoyer")
            }
        }
    }
}

@Composable
fun BubbleMessage(text: String, isUserMessage: Boolean) {
    val backgroundColor = if (isUserMessage) Color(0xFFDCF8C6) else Color(0xFFEFEFEF)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(4.dp),
        horizontalArrangement = if (isUserMessage) Arrangement.End else Arrangement.Start
    ) {
        Surface(
            modifier = Modifier
                .padding(8.dp)
                .fillMaxWidth(0.8f),
            color = backgroundColor,
            shape = MaterialTheme.shapes.medium,
            shadowElevation = 4.dp
        ) {
            Text(
                text = text,
                modifier = Modifier.padding(12.dp),
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}
