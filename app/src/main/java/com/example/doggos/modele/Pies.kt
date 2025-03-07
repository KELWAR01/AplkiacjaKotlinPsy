package com.example.doggos.modele

data class Pies(
    val id: Long = 0,
    val nazwa: String,
    val rasa: String = "",
    val urlZdjecia: String = "",
    val jestUlubiony: Boolean = false,
    val wlascicielId: Int? = null,
    val wlascicielImie: String? = null
)