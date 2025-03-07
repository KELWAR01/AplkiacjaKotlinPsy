package com.example.doggos.ui.ekrany.szczegoly

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

class SzczegolyViewModel(private val piesId: Long) : ViewModel() {

    @Inject
    lateinit var repozytorium: PiesRepozytorium

    private val _uiStan = MutableStateFlow(UISzczegolyStan())
    val uiStan: StateFlow<UISzczegolyStan> = _uiStan.asStateFlow()

    init {
        DoggoApp.appComponent.wstrzyknijDoSzczegolyViewModel(this)
        pobierzPieska()
        pobierzRasy()
        pobierzUzytkownikow()
    }

    private fun pobierzPieska() {
        viewModelScope.launch {
            try {
                repozytorium.pobierzWszystkiePieski().collect { pieski ->
                    val pies = pieski.find { it.id == piesId }
                    if (pies != null) {
                        _uiStan.value = _uiStan.value.copy(pies = pies)
                    } else {
                        _uiStan.value = _uiStan.value.copy(blad = "Nie znaleziono pieska")
                    }
                }
            } catch (e: Exception) {
                _uiStan.value = _uiStan.value.copy(blad = "Błąd: ${e.message}")
            }
        }
    }

    private fun pobierzRasy() {
        viewModelScope.launch {
            try {
                val rasy = repozytorium.pobierzRasy()
                _uiStan.value = _uiStan.value.copy(dostepneRasy = rasy)
            } catch (e: Exception) {
                _uiStan.value = _uiStan.value.copy(blad = "Błąd pobierania ras: ${e.message}")
            }
        }
    }

    private fun pobierzUzytkownikow() {
        viewModelScope.launch {
            try {
                // Najpierw spróbuj odświeżyć dane z API
                try {
                    repozytorium.odswiezUzytkownikow()
                } catch (e: Exception) {
                    // Jeśli nie udało się odświeżyć, to po prostu kontynuuj z danymi z bazy
                }

                // Pobieraj użytkowników z bazy danych jako Flow
                repozytorium.pobierzWszystkichUzytkownikow().collect { uzytkownicy ->
                    _uiStan.value = _uiStan.value.copy(dostepniUzytkownicy = uzytkownicy)
                }
            } catch (e: Exception) {
                _uiStan.value = _uiStan.value.copy(blad = "Błąd pobierania użytkowników: ${e.message}")
            }
        }
    }

    fun wybierzRase(rasa: String) {
        val aktualnyPies = _uiStan.value.pies ?: return
        val zaktualizowanyPies = aktualnyPies.copy(rasa = rasa)

        viewModelScope.launch {
            repozytorium.aktualizujPieska(zaktualizowanyPies)
            _uiStan.value = _uiStan.value.copy(pies = zaktualizowanyPies)
        }
    }

    fun wybierzWlasciciela(uzytkownik: Uzytkownik) {
        val aktualnyPies = _uiStan.value.pies ?: return
        val zaktualizowanyPies = aktualnyPies.copy(
            wlascicielId = uzytkownik.id,
            wlascicielImie = uzytkownik.name
        )

        viewModelScope.launch {
            repozytorium.aktualizujPieska(zaktualizowanyPies)
            _uiStan.value = _uiStan.value.copy(pies = zaktualizowanyPies)
        }
    }

    fun odswiezZdjecie() {
        viewModelScope.launch {
            try {
                val noweZdjecie = repozytorium.pobierzLosoweZdjecie()
                val aktualnyPies = _uiStan.value.pies ?: return@launch
                val zaktualizowanyPies = aktualnyPies.copy(urlZdjecia = noweZdjecie)

                repozytorium.aktualizujPieska(zaktualizowanyPies)
                _uiStan.value = _uiStan.value.copy(pies = zaktualizowanyPies)
            } catch (e: Exception) {
                _uiStan.value = _uiStan.value.copy(blad = "Błąd odświeżania zdjęcia: ${e.message}")
            }
        }
    }

    fun wyczyscBlad() {
        _uiStan.value = _uiStan.value.copy(blad = null)
    }

    class Factory(private val piesId: Long) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(SzczegolyViewModel::class.java)) {
                return SzczegolyViewModel(piesId) as T
            }
            throw IllegalArgumentException("Nieznany ViewModel class")
        }
    }
}

data class UISzczegolyStan(
    val pies: Pies? = null,
    val dostepneRasy: List<String> = emptyList(),
    val dostepniUzytkownicy: List<Uzytkownik> = emptyList(),
    val blad: String? = null
)