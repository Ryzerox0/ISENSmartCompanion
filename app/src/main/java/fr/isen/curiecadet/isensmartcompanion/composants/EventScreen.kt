package fr.isen.curiecadet.isensmartcompanion.composants

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.NotificationsOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import fr.isen.curiecadet.isensmartcompanion.EventsDetailActivity
import fr.isen.curiecadet.isensmartcompanion.R
import fr.isen.curiecadet.isensmartcompanion.api.PreferencesNotification
import fr.isen.curiecadet.isensmartcompanion.api.RetrofitInstance
import fr.isen.curiecadet.isensmartcompanion.api.cancelNotification
import fr.isen.curiecadet.isensmartcompanion.api.scheduleNotification
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.Serializable




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
    var events by remember { mutableStateOf<List<Event>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        isLoading = true
        errorMessage = null

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
        Text(
            text = "Événements",
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

        errorMessage?.let {
            Text(
                text = it,
                color = Color.Red,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(bottom = 16.dp)
            )
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 100.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(events) { event ->
                EventItem(event) {
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

        if (isLoading) {
            Text(text = "Chargement en cours...", style = MaterialTheme.typography.bodyMedium)
        }
    }
}

@Composable
fun EventItem(event: Event, onClick: () -> Unit) {
    val context = LocalContext.current
    var isNotificationEnabled by remember { mutableStateOf(PreferencesNotification.get(context, event.id)) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
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

            IconButton(onClick = {
                isNotificationEnabled = !isNotificationEnabled
                PreferencesNotification.save(context, event.id, isNotificationEnabled)

                if (isNotificationEnabled) {
                    scheduleNotification(context, event)
                    showNotification(context, event, true)
                } else {
                    cancelNotification(context, event.id)
                    showNotification(context, event, false)
                }
            }) {
                Icon(
                    imageVector = if (isNotificationEnabled) Icons.Filled.Notifications else Icons.Filled.NotificationsOff,
                    contentDescription = "Notification",
                    tint = if (isNotificationEnabled) Color.Green else Color.Gray
                )
            }
        }
    }
}

fun showNotification(context: Context, event: Event, isEnabled: Boolean) {
    if (ActivityCompat.checkSelfPermission(
            context,
            Manifest.permission.POST_NOTIFICATIONS
        ) != PackageManager.PERMISSION_GRANTED
    ) {
        Toast.makeText(context, "Permission non accordée pour envoyer des notifications.", Toast.LENGTH_LONG).show()
        return
    }

    val channelId = "default_channel"
    val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager


    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val channel = NotificationChannel(
            channelId,
            "Notifications d'événements",
            NotificationManager.IMPORTANCE_DEFAULT
        ).apply {
            description = "Canal pour les notifications d'événements"
        }
        notificationManager.createNotificationChannel(channel)
    }

    val notificationTitle = if (isEnabled) "Notification activée" else "Notification désactivée"
    val notificationMessage = "La notification pour l'événement '${event.title}' a été ${if (isEnabled) "activée" else "désactivée"}."

    val notification = NotificationCompat.Builder(context, channelId)
        .setSmallIcon(R.drawable.ic_launcher_foreground)
        .setContentTitle(notificationTitle)
        .setContentText(notificationMessage)
        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
        .setAutoCancel(true)
        .build()

    NotificationManagerCompat.from(context).notify(event.id.hashCode(), notification)

    Toast.makeText(context, notificationMessage, Toast.LENGTH_SHORT).show()
}

fun fetchEvents(
    onSuccess: (List<Event>) -> Unit,
    onFailure: (String) -> Unit
) {
    RetrofitInstance.API.getEvents().enqueue(object : Callback<List<Event>> {
        override fun onResponse(call: Call<List<Event>>, response: Response<List<Event>>) {
            if (response.isSuccessful) {
                onSuccess(response.body() ?: emptyList())
            } else {
                onFailure("Erreur de chargement des événements. Code: ${response.code()}")
            }
        }

        override fun onFailure(call: Call<List<Event>>, t: Throwable) {
            onFailure("Erreur de réseau: ${t.localizedMessage}")
        }
    })
}
