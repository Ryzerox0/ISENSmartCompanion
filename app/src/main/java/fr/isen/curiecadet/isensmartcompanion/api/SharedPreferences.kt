package fr.isen.curiecadet.isensmartcompanion.api

import android.content.Context
import android.content.SharedPreferences

fun saveNotificationPreference(context: Context, eventId: Int, shouldNotify: Boolean) {
    val sharedPreferences: SharedPreferences = context.getSharedPreferences("user_preferences", Context.MODE_PRIVATE)
    val editor = sharedPreferences.edit()
    editor.putBoolean("notify_event_$eventId", shouldNotify)
    editor.apply()
}

// Fonction pour récupérer la préférence de notification
fun getNotificationPreference(context: Context, eventId: Int): Boolean {
    val sharedPreferences: SharedPreferences = context.getSharedPreferences("user_preferences", Context.MODE_PRIVATE)
    return sharedPreferences.getBoolean("notify_event_$eventId", false)
}
