package com.example.doggos.dane.api

import com.example.doggos.dane.api.modele.Uzytkownik
import retrofit2.http.GET

interface UzytkownikApi {
    @GET("users")
    suspend fun pobierzUzytkownikow(): List<Uzytkownik>
}