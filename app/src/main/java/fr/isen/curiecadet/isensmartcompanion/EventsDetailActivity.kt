package fr.isen.curiecadet.isensmartcompanion

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import fr.isen.curiecadet.isensmartcompanion.ui.theme.ISENSmartCompanionTheme

class EventsDetailActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Récupérer les données de l'Intent
        val eventName = intent.getStringExtra("EVENT_TITLE") ?: "Nom de l'événement inconnu"
        val eventDate = intent.getStringExtra("EVENT_DATE") ?: "Date inconnue"
        val eventDescription = intent.getStringExtra("EVENT_DESCRIPTION") ?: "Description inconnue"
        val eventCategory = intent.getStringExtra("EVENT_CATEGORY") ?: "Categorie inconnue"
        val eventid = intent.getStringExtra("EVENT_ID") ?: "Id inconnue"
        val eventlocation = intent.getStringExtra("EVENT_LOCATION") ?: "location inconnue"

        setContent {
            ISENSmartCompanionTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    EventDetailScreen(
                        eventtitle = eventName,
                        eventDate = eventDate,
                        eventDescription = eventDescription,
                        eventCategory = eventCategory,
                        eventid = eventid,
                        eventlocation = eventlocation,
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun EventDetailScreen(
    eventtitle: String,
    eventDate: String,
    eventDescription: String,
    eventCategory: String,
    eventid: String,
    eventlocation: String,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.padding(16.dp)) {
        Text(
            text = "Nom de l'événement: $eventtitle",
            modifier = Modifier.padding(bottom = 8.dp)
        )
        Text(
            text = "Date: $eventDate",
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        Text(
            text = "Description: $eventDescription",
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        Text(
            text = "Categorie: $eventCategory",
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        Text(
            text = "id: $eventid",
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        Text(
            text = "Location: $eventlocation",
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(bottom = 8.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun EventDetailScreenPreview() {
    ISENSmartCompanionTheme {
        EventDetailScreen(
            eventtitle = "Soirée BDE",
            eventDate = "15 décembre 2024",
            eventDescription = "Rejoignez-nous pour une soirée festive avec des jeux et de la musique.",
            eventCategory = "Soirée",
            eventid = "Marseille",
            eventlocation = "1"
        )
    }
}
