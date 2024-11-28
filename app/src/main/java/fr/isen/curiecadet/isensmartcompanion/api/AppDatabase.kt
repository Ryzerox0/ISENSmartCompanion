package fr.isen.curiecadet.isensmartcompanion.api

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase



@Database(entities = [Interaction::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun interactionDao(): InteractionDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "interaction_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
