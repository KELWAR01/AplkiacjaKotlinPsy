package com.example.doggos.dane.baza.encje

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.doggos.modele.Pies

@Entity(tableName = "pieski")
data class PiesEncja(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val nazwa: String,
    val rasa: String,
    val urlZdjecia: String,
    val jestUlubiony: Boolean,
    val wlascicielId: Int?,
    val wlascicielImie: String?
)

fun PiesEncja.naPies(): Pies {
    return Pies(
        id = id,
        nazwa = nazwa,
        rasa = rasa,
        urlZdjecia = urlZdjecia,
        jestUlubiony = jestUlubiony,
        wlascicielId = wlascicielId,
        wlascicielImie = wlascicielImie
    )
}

fun Pies.naEncje(): PiesEncja {
    return PiesEncja(
        id = id,
        nazwa = nazwa,
        rasa = rasa,
        urlZdjecia = urlZdjecia,
        jestUlubiony = jestUlubiony,
        wlascicielId = wlascicielId,
        wlascicielImie = wlascicielImie
    )
}