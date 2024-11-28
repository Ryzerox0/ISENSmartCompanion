package fr.isen.curiecadet.isensmartcompanion.api

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "interactions")
data class Interaction(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val question: String,
    val response: String,
    val timestamp: Long = System.currentTimeMillis()  // Stocke la date sous forme de timestamp
)
