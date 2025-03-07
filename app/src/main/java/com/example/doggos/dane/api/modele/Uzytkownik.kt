package com.example.doggos.dane.api.modele

data class Uzytkownik(
    val id: Int,
    val name: String,
    val username: String,
    val email: String,
    val address: Adres,
    val phone: String,
    val website: String,
    val company: Firma
)

data class Adres(
    val street: String,
    val suite: String,
    val city: String,
    val zipcode: String,
    val geo: Geo
)

data class Geo(
    val lat: String,
    val lng: String
)

data class Firma(
    val name: String,
    val catchPhrase: String,
    val bs: String
)