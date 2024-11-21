package fr.isen.curiecadet.isensmartcompanion.composants

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import fr.isen.curiecadet.isensmartcompanion.EventsDetailActivity


data class Event(val name: String, val date: String, val description: String)

@Composable
fun EventScreen() {
    val context = LocalContext.current


    val events = listOf(
        Event("Soirée BDE", "15 décembre 2024", "Rejoignez-nous pour une soirée festive avec des jeux et de la musique."),
        Event("Gala de fin d'année", "20 décembre 2024", "Célébration de la fin de l'année avec un dîner et une soirée dansante."),
        Event("Journée de cohésion", "10 janvier 2025", "Activités pour renforcer les liens entre les étudiants et le corps enseignant."),
    )

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Text(
            text = "Liste des événements de l'ISEN",
            modifier = Modifier.padding(bottom = 16.dp)
        )
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(events) { event ->
                EventCard(event = event, context = context)
            }
        }
    }
}

@Composable
fun EventCard(event: Event, context: Context =  LocalContext.current) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        shape = MaterialTheme.shapes.medium,

    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                text = event.name,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            Text(
                text = event.date,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            Text(
                text = event.description,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            Button(onClick = {
                // Lancer une activité de détail d'événement
                val intent = Intent(context, EventsDetailActivity::class.java)
                intent.putExtra("EVENT_NAME", event.name)
                intent.putExtra("EVENT_DATE", event.date)
                intent.putExtra("EVENT_DESCRIPTION", event.description)
                context.startActivity(intent)
            }) {
                Text("Voir les détails")
            }
        }
    }
}
