package fr.isen.curiecadet.isensmartcompanion.composants

import android.content.Intent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import fr.isen.curiecadet.isensmartcompanion.API.RetrofitInstance
import fr.isen.curiecadet.isensmartcompanion.EventsDetailActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.Serializable

// Nouvelle classe Event avec id comme String
data class Event(
    val id: String,
    val title: String,
    val description: String,
    val date: String,
    val location: String,
    val category: String
) : Serializable

@Composable
fun EventScreen() {
    val context = LocalContext.current
    var events by remember { mutableStateOf<List<Event>>(emptyList()) } // État pour stocker les événements
    var isLoading by remember { mutableStateOf(true) } // Variable pour gérer l'état de chargement
    var errorMessage by remember { mutableStateOf<String?>(null) } // Message d'erreur

    // Charger les événements à partir de l'API
    LaunchedEffect(Unit) {
        // Mettre l'état de chargement à true
        isLoading = true
        errorMessage = null

        // Appel de la fonction fetchEvents
        fetchEvents(
            onSuccess = { fetchedEvents ->
                events = fetchedEvents
                isLoading = false
            },
            onFailure = { error ->
                errorMessage = error
                isLoading = false
            }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Ajouter un titre centré en haut de l'écran
        Text(
            text = "évènements",
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            style = MaterialTheme.typography.bodyLarge.copy(
                color = Color.Red,
                fontWeight = FontWeight.Bold,
                fontSize = 24.sp
            ),
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )

        // Afficher un message d'erreur s'il y en a
        errorMessage?.let {
            Text(
                text = it,
                color = Color.Red,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(bottom = 16.dp)
            )
        }

        // Liste d'événements
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 100.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(events) { event ->
                EventItem(event) {
                    // Navigation vers l'écran de détails
                    val intent = Intent(context, EventsDetailActivity::class.java).apply {
                        putExtra("eventId", event.id)
                        putExtra("eventTitle", event.title)
                        putExtra("eventDate", event.date)
                        putExtra("eventDescription", event.description)
                        putExtra("eventLocation", event.location)
                        putExtra("eventCategory", event.category)
                    }
                    context.startActivity(intent)
                }
            }
        }

        // Affichage d'un indicateur de chargement si nécessaire
        if (isLoading) {
            Text(text = "Chargement en cours...", style = MaterialTheme.typography.bodyMedium)
        }
    }
}

// Fonction fetchEvents() pour effectuer l'appel API de manière asynchrone
fun fetchEvents(
    onSuccess: (List<Event>) -> Unit,
    onFailure: (String) -> Unit
) {
    RetrofitInstance.API.getEvents().enqueue(object : Callback<List<Event>> {
        override fun onResponse(call: Call<List<Event>>, response: Response<List<Event>>) {
            if (response.isSuccessful) {
                // Appeler onSuccess avec les événements récupérés
                onSuccess(response.body() ?: emptyList())
            } else {
                // Appeler onFailure avec un message d'erreur
                onFailure("Erreur de chargement des événements. Code: ${response.code()}")
            }
        }

        override fun onFailure(call: Call<List<Event>>, t: Throwable) {
            // Appeler onFailure en cas de problème réseau
            onFailure("Erreur de réseau: ${t.localizedMessage}")
        }
    })
}

@Composable
fun EventItem(event: Event, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },

    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = event.title,
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
            )
            Text(
                text = event.date,
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = Color.Gray,
                    fontSize = 14.sp
                )
            )
            Text(
                text = "Lieu : ${event.location}",
                style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
                color = Color.DarkGray
            )
            Text(
                text = "Catégorie : ${event.category}",
                style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
                color = Color.DarkGray
            )
            Text(
                text = event.description,
                style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
                maxLines = 2
            )
        }
    }
}
