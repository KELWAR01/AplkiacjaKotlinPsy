package com.example.doggos.dane.api

import com.example.doggos.dane.api.modele.OdpowiedzBreed
import com.example.doggos.dane.api.modele.OdpowiedzImage
import retrofit2.http.GET

interface DogApi {
    @GET("breeds/list/all")
    suspend fun pobierzRasy(): OdpowiedzBreed

    @GET("breeds/image/random")
    suspend fun pobierzLosoweZdjecie(): OdpowiedzImage
}