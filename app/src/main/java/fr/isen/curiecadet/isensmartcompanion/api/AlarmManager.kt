package fr.isen.curiecadet.isensmartcompanion.api

import android.Manifest
import android.app.Activity
import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import fr.isen.curiecadet.isensmartcompanion.R
import android.widget.Toast
import fr.isen.curiecadet.isensmartcompanion.composants.Event

// Assurer que la classe Event est définie comme suit (à ajouter dans ton code si nécessaire)
data class Event(
    val id: String,          // L'ID unique de l'événement
    val title: String,       // Le titre de l'événement
    val description: String  // La description de l'événement
)

// Vérification si la permission d'envoyer des notifications est accordée
fun isNotificationPermissionGranted(context: Context): Boolean {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        // Pour Android 13 et versions supérieures
        ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.POST_NOTIFICATIONS
        ) == PackageManager.PERMISSION_GRANTED
    } else {
        // Pour les versions antérieures, pas besoin de demander la permission
        true
    }
}

// Demande de la permission si nécessaire
fun requestNotificationPermission(activity: Activity) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        if (ActivityCompat.shouldShowRequestPermissionRationale(
                activity,
                Manifest.permission.POST_NOTIFICATIONS
            )) {
            // Si l'utilisateur a précédemment refusé, tu peux lui expliquer pourquoi
            Toast.makeText(activity, "Cette permission est nécessaire pour recevoir des notifications.", Toast.LENGTH_LONG).show()
        }

        // Demander la permission
        ActivityCompat.requestPermissions(
            activity,
            arrayOf(Manifest.permission.POST_NOTIFICATIONS),
            1 // Code de demande de permission
        )
    }
}

// Fonction pour planifier une notification
fun scheduleNotification(context: Context, event: Event) {
    if (!isNotificationPermissionGranted(context)) {
        if (context is Activity) {
            // Demander la permission si elle n'est pas accordée
            requestNotificationPermission(context)
        }
        return
    }

    val delay = 30_000L // 30 secondes après abonnement
    val intent = Intent(context, NotificationReceiver::class.java).apply {
        putExtra("eventTitle", event.title)
        putExtra("eventDescription", event.description)
    }

    // Utilisation de event.id comme identifiant unique
    val pendingIntent = PendingIntent.getBroadcast(
        context,
        event.id.hashCode(),  // Utilisation de hashCode() pour garantir une valeur unique
        intent,
        PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
    )

    val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager
    alarmManager?.setExact(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + delay, pendingIntent)
}

// Fonction pour annuler la notification
fun cancelNotification(context: Context, eventId: String) {
    val intent = Intent(context, NotificationReceiver::class.java)
    val pendingIntent = PendingIntent.getBroadcast(
        context,
        eventId.hashCode(),  // Utilisation de hashCode() pour garantir une valeur unique
        intent,
        PendingIntent.FLAG_IMMUTABLE
    )

    val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager
    alarmManager?.cancel(pendingIntent)
}

// Le receiver qui gère l'affichage de la notification
class NotificationReceiver : BroadcastReceiver() {

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onReceive(context: Context, intent: Intent) {
        val eventTitle = intent.getStringExtra("eventTitle")
        val eventDescription = intent.getStringExtra("eventDescription")

        // Vérification de la validité des données
        if (eventTitle != null && eventDescription != null) {

            // Créer un canal de notification (nécessaire pour Android O et supérieur)
            val channelId = "default_channel"
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val channel = NotificationChannel(
                    channelId,
                    "Default Channel",
                    NotificationManager.IMPORTANCE_DEFAULT
                ).apply {
                    description = "Channel for default notifications"
                }
                val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                notificationManager.createNotificationChannel(channel)
            }

            // Créer la notification
            val notification = NotificationCompat.Builder(context, channelId)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle(eventTitle)
                .setContentText(eventDescription)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .build()

            // Vérifier la permission de notification
            if (ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED) {

                // Utiliser NotificationManagerCompat pour afficher la notification
                val notificationManager = NotificationManagerCompat.from(context)
                notificationManager.notify(eventTitle.hashCode(), notification)

            } else {
                // Demander la permission si elle n'est pas accordée (seulement dans une activité)
                if (context is Activity) {
                    ActivityCompat.requestPermissions(
                        context,
                        arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                        1 // Code de demande de permission
                    )
                }
            }
        }
    }
}

// Ajout de onRequestPermissionsResult dans ton activité
fun handlePermissionResult(
    requestCode: Int,
    permissions: Array<String>,
    grantResults: IntArray,
    activity: Activity
) {
    if (requestCode == 1) {  // Utiliser un code de demande cohérent avec celui dans requestNotificationPermission
        if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            // La permission a été accordée, tu peux maintenant envoyer des notifications
            Toast.makeText(activity, "Permission accordée pour les notifications.", Toast.LENGTH_SHORT).show()
        } else {
            // La permission a été refusée
            Toast.makeText(activity, "Permission refusée pour les notifications.", Toast.LENGTH_SHORT).show()
        }
    }
}
