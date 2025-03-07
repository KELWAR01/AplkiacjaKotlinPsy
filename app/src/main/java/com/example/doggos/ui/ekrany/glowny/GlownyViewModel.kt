package com.example.doggos.ui.ekrany.glowny

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.doggos.DoggoApp
import com.example.doggos.dane.api.modele.Uzytkownik
import com.example.doggos.dane.repozytorium.PiesRepozytorium
import com.example.doggos.modele.Pies
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

class GlownyViewModel : ViewModel() {

    @Inject
    lateinit var repozytorium: PiesRepozytorium

    private val _uiStan = MutableStateFlow(UIGlownyStan())
    val uiStan: StateFlow<UIGlownyStan> = _uiStan.asStateFlow()

    init {
        DoggoApp.appComponent.wstrzyknijDoGlownyViewModel(this)
        pobierzPieski()
        pobierzUzytkownikow()
    }

    private fun pobierzPieski() {
        viewModelScope.launch {
            repozytorium.pobierzWszystkiePieski().collect { pieski ->
                _uiStan.value = _uiStan.value.copy(pieski = pieski)
            }
        }
    }

    private fun pobierzUzytkownikow() {
        viewModelScope.launch {
            try {
                // Próba odświeżenia danych z API i zapisania do bazy
                repozytorium.odswiezUzytkownikow()

                // Pobieraj użytkowników z bazy danych jako Flow
                repozytorium.pobierzWszystkichUzytkownikow().collect { uzytkownicy ->
                    _uiStan.value = _uiStan.value.copy(uzytkownicy = uzytkownicy)
                }
            } catch (e: Exception) {
                _uiStan.value = _uiStan.value.copy(
                    blad = "Błąd podczas pobierania użytkowników: ${e.message}"
                )
            }
        }
    }

    fun ustawTekstWyszukiwania(tekst: String) {
        _uiStan.value = _uiStan.value.copy(tekstWyszukiwania = tekst)
    }

    fun wyszukajPieski() {
        val zapytanie = _uiStan.value.tekstWyszukiwania
        if (zapytanie.isBlank()) {
            return
        }

        viewModelScope.launch {
            try {
                val wyniki = repozytorium.szukajPieski(zapytanie)
                _uiStan.value = _uiStan.value.copy(
                    pieski = wyniki,
                    czyWyszukano = true
                )
            } catch (e: Exception) {
                _uiStan.value = _uiStan.value.copy(
                    blad = "Błąd podczas wyszukiwania: ${e.message}"
                )
            }
        }
    }

    fun dodajPieska() {
        val nazwa = _uiStan.value.tekstWyszukiwania
        if (nazwa.isBlank()) {
            return
        }

        viewModelScope.launch {
            try {
                // Sprawdź czy piesek o takiej nazwie już istnieje
                val czyIstnieje = repozytorium.czyIstniejePiesek(nazwa)
                if (czyIstnieje) {
                    _uiStan.value = _uiStan.value.copy(
                        czyNazwaIstnieje = true,
                        blad = "Piesek o takiej nazwie już istnieje!"
                    )
                    return@launch
                }

                val zdjecie = repozytorium.pobierzLosoweZdjecie()
                val nowyPies = Pies(
                    nazwa = nazwa,
                    urlZdjecia = zdjecie
                )

                repozytorium.dodajPieska(nowyPies)
                _uiStan.value = _uiStan.value.copy(
                    tekstWyszukiwania = "",
                    czyNazwaIstnieje = false,
                    blad = null
                )
            } catch (e: Exception) {
                _uiStan.value = _uiStan.value.copy(
                    blad = "Błąd podczas dodawania pieska: ${e.message}"
                )
            }
        }
    }

    fun przełączUlubione(pies: Pies) {
        viewModelScope.launch {
            val zaktualizowanyPies = pies.copy(jestUlubiony = !pies.jestUlubiony)
            repozytorium.aktualizujPieska(zaktualizowanyPies)
        }
    }

    fun usunPieska(pies: Pies) {
        viewModelScope.launch {
            repozytorium.usunPieska(pies)
        }
    }

    fun wyczyscBlad() {
        _uiStan.value = _uiStan.value.copy(blad = null)
    }

    class Factory : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(GlownyViewModel::class.java)) {
                return GlownyViewModel() as T
            }
            throw IllegalArgumentException("Nieznany ViewModel class")
        }
    }
}

data class UIGlownyStan(
    val pieski: List<Pies> = emptyList(),
    val uzytkownicy: List<Uzytkownik> = emptyList(),
    val tekstWyszukiwania: String = "",
    val czyWyszukano: Boolean = false,
    val czyNazwaIstnieje: Boolean = false,
    val blad: String? = null
)