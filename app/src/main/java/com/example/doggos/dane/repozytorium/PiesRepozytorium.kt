package com.example.doggos.dane.repozytorium

import com.example.doggos.dane.api.DogApi
import com.example.doggos.dane.api.UzytkownikApi
import com.example.doggos.dane.api.modele.Uzytkownik
import com.example.doggos.dane.baza.PiesDao
import com.example.doggos.dane.baza.UzytkownikDao
import com.example.doggos.dane.baza.encje.UzytkownikEncja
import com.example.doggos.dane.baza.encje.naEncje
import com.example.doggos.dane.baza.encje.naModel
import com.example.doggos.dane.baza.encje.naPies
import com.example.doggos.dane.baza.encje.naEncje as piesNaEncje
import com.example.doggos.modele.Pies
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PiesRepozytorium @Inject constructor(
    private val piesDao: PiesDao,
    private val uzytkownikDao: UzytkownikDao,
    private val dogApi: DogApi,
    private val uzytkownikApi: UzytkownikApi
) {
    // Metody dla Piesków
    fun pobierzWszystkiePieski(): Flow<List<Pies>> {
        return piesDao.pobierzWszystkiePieski().map { encje ->
            encje.map { it.naPies() }
        }
    }

    suspend fun szukajPieski(zapytanie: String): List<Pies> = withContext(Dispatchers.IO) {
        return@withContext piesDao.szukajPieski("%$zapytanie%").map { it.naPies() }
    }

    suspend fun dodajPieska(pies: Pies): Long = withContext(Dispatchers.IO) {
        return@withContext piesDao.dodajPieska(pies.piesNaEncje())
    }

    suspend fun aktualizujPieska(pies: Pies) = withContext(Dispatchers.IO) {
        piesDao.aktualizujPieska(pies.piesNaEncje())
    }

    suspend fun usunPieska(pies: Pies) = withContext(Dispatchers.IO) {
        piesDao.usunPieska(pies.piesNaEncje())
    }

    suspend fun pobierzRasy(): List<String> = withContext(Dispatchers.IO) {
        val odpowiedz = dogApi.pobierzRasy()
        return@withContext odpowiedz.message.keys.toList()
    }

    suspend fun pobierzLosoweZdjecie(): String = withContext(Dispatchers.IO) {
        val odpowiedz = dogApi.pobierzLosoweZdjecie()
        return@withContext odpowiedz.message
    }

    suspend fun czyIstniejePiesek(nazwa: String): Boolean = withContext(Dispatchers.IO) {
        return@withContext piesDao.liczPieskiZNazwa(nazwa) > 0
    }

    // Metody dla Użytkowników
    fun pobierzWszystkichUzytkownikow(): Flow<List<Uzytkownik>> {
        return uzytkownikDao.pobierzWszystkichUzytkownikow().map { encje ->
            encje.map { it.naModel() }
        }
    }

    // Ta metoda zachowuje kompatybilność ze starym API
    suspend fun pobierzUzytkownikow(): List<Uzytkownik> = withContext(Dispatchers.IO) {
        // Próbuj odświeżyć najpierw
        try {
            odswiezUzytkownikow()
        } catch (e: Exception) {
            // Ignoruj błędy, będziemy próbować pobrać z bazy
        }

        // Pobierz z bazy
        val uzytkownicyFlow = pobierzWszystkichUzytkownikow()
        val uzytkownicy = uzytkownicyFlow.first()

        // Jeśli baza jest pusta, pobierz z API
        if (uzytkownicy.isEmpty()) {
            return@withContext uzytkownikApi.pobierzUzytkownikow()
        }

        return@withContext uzytkownicy
    }

    suspend fun odswiezUzytkownikow() = withContext(Dispatchers.IO) {
        try {
            // Pobierz użytkowników z API
            val uzytkownicy = uzytkownikApi.pobierzUzytkownikow()

            // Zapisz do bazy danych lokalnej
            uzytkownikDao.dodajUzytkownikow(uzytkownicy.map { it.naEncje() })
        } catch (e: Exception) {
            // W przypadku błędu z API, polegamy na danych z lokalnej bazy
            // Jeśli baza jest pusta, propaguj wyjątek
            if (uzytkownikDao.liczbaUzytkownikow() == 0) {
                throw e
            }
        }
    }
}