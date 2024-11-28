package fr.isen.curiecadet.isensmartcompanion.api


import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface InteractionDao {
    // Insérer une interaction dans la base de données
    @Insert
    suspend fun insert(interaction: Interaction)

    // Récupérer toutes les interactions
    @Query("SELECT * FROM interactions")
    suspend fun getAllInteractions(): List<Interaction>

    // Supprimer une interaction
    @Query("DELETE FROM interactions WHERE id = :id")
    suspend fun deleteById(id: Long)

    // Supprimer toutes les interactions
    @Query("DELETE FROM interactions")
    suspend fun deleteAllInteractions()
}
