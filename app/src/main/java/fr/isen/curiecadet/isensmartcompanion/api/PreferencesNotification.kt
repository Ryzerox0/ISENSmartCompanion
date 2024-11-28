package fr.isen.curiecadet.isensmartcompanion.api

import android.content.Context
import android.content.SharedPreferences

object PreferencesNotification {

    private const val PREFS_NAME = "notification_prefs"
    private const val KEY_NOTIFICATION_STATE = "notification_state_"

    // Récupère l'état de la notification pour un événement donné
    fun get(context: Context, eventId: String): Boolean {
        val sharedPreferences: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return sharedPreferences.getBoolean(KEY_NOTIFICATION_STATE + eventId, false)
    }

    // Enregistre l'état de la notification pour un événement donné
    fun save(context: Context, eventId: String, isActive: Boolean) {
        val sharedPreferences: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putBoolean(KEY_NOTIFICATION_STATE + eventId, isActive)
        editor.apply()
    }
}
