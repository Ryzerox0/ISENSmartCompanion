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


data class Event(
    val id: String,
    val title: String,
    val description: String
)


fun isNotificationPermissionGranted(context: Context): Boolean {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {

        ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.POST_NOTIFICATIONS
        ) == PackageManager.PERMISSION_GRANTED
    } else {

        true
    }
}


fun requestNotificationPermission(activity: Activity) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        if (ActivityCompat.shouldShowRequestPermissionRationale(
                activity,
                Manifest.permission.POST_NOTIFICATIONS
            )) {

            Toast.makeText(activity, "Cette permission est nécessaire pour recevoir des notifications.", Toast.LENGTH_LONG).show()
        }


        ActivityCompat.requestPermissions(
            activity,
            arrayOf(Manifest.permission.POST_NOTIFICATIONS),
            1
        )
    }
}


fun scheduleNotification(context: Context, event: Event) {
    if (!isNotificationPermissionGranted(context)) {
        if (context is Activity) {

            requestNotificationPermission(context)
        }
        return
    }

    val delay = 30_000L
    val intent = Intent(context, NotificationReceiver::class.java).apply {
        putExtra("eventTitle", event.title)
        putExtra("eventDescription", event.description)
    }


    val pendingIntent = PendingIntent.getBroadcast(
        context,
        event.id.hashCode(),
        intent,
        PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
    )

    val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager
    alarmManager?.setExact(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + delay, pendingIntent)
}


fun cancelNotification(context: Context, eventId: String) {
    val intent = Intent(context, NotificationReceiver::class.java)
    val pendingIntent = PendingIntent.getBroadcast(
        context,
        eventId.hashCode(),
        intent,
        PendingIntent.FLAG_IMMUTABLE
    )

    val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager
    alarmManager?.cancel(pendingIntent)
}


class NotificationReceiver : BroadcastReceiver() {

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onReceive(context: Context, intent: Intent) {
        val eventTitle = intent.getStringExtra("eventTitle")
        val eventDescription = intent.getStringExtra("eventDescription")


        if (eventTitle != null && eventDescription != null) {


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


            val notification = NotificationCompat.Builder(context, channelId)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle(eventTitle)
                .setContentText(eventDescription)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .build()


            if (ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED) {


                val notificationManager = NotificationManagerCompat.from(context)
                notificationManager.notify(eventTitle.hashCode(), notification)

            } else {

                if (context is Activity) {
                    ActivityCompat.requestPermissions(
                        context,
                        arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                        1
                    )
                }
            }
        }
    }
}


