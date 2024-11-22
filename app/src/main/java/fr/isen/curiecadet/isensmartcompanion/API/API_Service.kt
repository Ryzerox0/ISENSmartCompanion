package fr.isen.curiecadet.isensmartcompanion.API


import fr.isen.curiecadet.isensmartcompanion.composants.Event
import retrofit2.Call
import retrofit2.http.GET


interface APIService {
    @GET("events.json")
    fun getEvents(): Call<List<Event>>
}
