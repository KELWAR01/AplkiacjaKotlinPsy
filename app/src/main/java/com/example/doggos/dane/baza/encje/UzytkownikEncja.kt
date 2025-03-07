package com.example.doggos.dane.baza.encje

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.doggos.dane.api.modele.Adres
import com.example.doggos.dane.api.modele.Firma
import com.example.doggos.dane.api.modele.Geo
import com.example.doggos.dane.api.modele.Uzytkownik

@Entity(tableName = "uzytkownicy")
data class UzytkownikEncja(
    @PrimaryKey
    val id: Int,
    val name: String,
    val username: String,
    val email: String,
    @Embedded(prefix = "adres_")
    val address: AdresEncja,
    val phone: String,
    val website: String,
    @Embedded(prefix = "firma_")
    val company: FirmaEncja
)

data class AdresEncja(
    val street: String,
    val suite: String,
    val city: String,
    val zipcode: String,
    @Embedded(prefix = "geo_")
    val geo: GeoEncja
)

data class GeoEncja(
    val lat: String,
    val lng: String
)

data class FirmaEncja(
    val name: String,
    val catchPhrase: String,
    val bs: String
)

// Funkcje konwersji
fun Uzytkownik.naEncje(): UzytkownikEncja {
    return UzytkownikEncja(
        id = id,
        name = name,
        username = username,
        email = email,
        address = AdresEncja(
            street = address.street,
            suite = address.suite,
            city = address.city,
            zipcode = address.zipcode,
            geo = GeoEncja(
                lat = address.geo.lat,
                lng = address.geo.lng
            )
        ),
        phone = phone,
        website = website,
        company = FirmaEncja(
            name = company.name,
            catchPhrase = company.catchPhrase,
            bs = company.bs
        )
    )
}

fun UzytkownikEncja.naModel(): Uzytkownik {
    return Uzytkownik(
        id = id,
        name = name,
        username = username,
        email = email,
        address = Adres(
            street = address.street,
            suite = address.suite,
            city = address.city,
            zipcode = address.zipcode,
            geo = Geo(
                lat = address.geo.lat,
                lng = address.geo.lng
            )
        ),
        phone = phone,
        website = website,
        company = Firma(
            name = company.name,
            catchPhrase = company.catchPhrase,
            bs = company.bs
        )
    )
}