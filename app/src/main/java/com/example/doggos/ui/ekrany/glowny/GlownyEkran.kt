package com.example.doggos.ui.ekrany.glowny

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.doggos.dane.api.modele.Uzytkownik
import com.example.doggos.modele.Pies
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.text.style.TextOverflow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GlownyEkran(
    navController: NavController,
    viewModel: GlownyViewModel = viewModel(factory = GlownyViewModel.Factory())
) {
    val uiStan by viewModel.uiStan.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Pasek wyszukiwania
        PasekWyszukiwania(
            tekstWyszukiwania = uiStan.tekstWyszukiwania,
            onZmianaTextu = { viewModel.ustawTekstWyszukiwania(it) },
            onWyszukaj = { viewModel.wyszukajPieski() },
            onDodaj = { viewModel.dodajPieska() },
            czyMoznaWyszukac = uiStan.tekstWyszukiwania.isNotBlank(),
            czyNazwaIstnieje = uiStan.czyNazwaIstnieje
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Liczniki
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Start
        ) {
            Text(
                text = "🐶: ${uiStan.pieski.size}",
                style = MaterialTheme.typography.bodyLarge
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = "💜: ${uiStan.pieski.count { it.jestUlubiony }}",
                style = MaterialTheme.typography.bodyLarge
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Zakładki do przełączania między pieskami i użytkownikami
        var selectedTabIndex by remember { mutableStateOf(0) }

        TabRow(selectedTabIndex = selectedTabIndex) {
            Tab(
                selected = selectedTabIndex == 0,
                onClick = { selectedTabIndex = 0 },
                text = { Text("Pieski") },
                icon = { Icon(Icons.Default.Pets, contentDescription = null) }
            )
            Tab(
                selected = selectedTabIndex == 1,
                onClick = { selectedTabIndex = 1 },
                text = { Text("Użytkownicy") },
                icon = { Icon(Icons.Default.Person, contentDescription = null) }
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        when (selectedTabIndex) {
            0 -> {
                // Lista piesków
                LazyColumn(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(uiStan.pieski, key = { it.id }) { pies ->
                        ElementListyPiesek(
                            pies = pies,
                            onKliknij = { navController.navigate("szczegoly/${pies.id}") },
                            onToggleUlubione = { viewModel.przełączUlubione(pies) },
                            onUsun = { viewModel.usunPieska(pies) }
                        )
                        Divider()
                    }
                }
            }
            1 -> {
                // Lista użytkowników
                LazyColumn(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(uiStan.uzytkownicy, key = { it.id }) { uzytkownik ->
                        ElementListyUzytkownik(uzytkownik = uzytkownik)
                        Divider()
                    }
                }
            }
        }
    }

    // Wyświetlanie błędów
    uiStan.blad?.let { blad ->
        AlertDialog(
            onDismissRequest = { viewModel.wyczyscBlad() },
            title = { Text("Błąd") },
            text = { Text(blad) },
            confirmButton = {
                Button(onClick = { viewModel.wyczyscBlad() }) {
                    Text("OK")
                }
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PasekWyszukiwania(
    tekstWyszukiwania: String,
    onZmianaTextu: (String) -> Unit,
    onWyszukaj: () -> Unit,
    onDodaj: () -> Unit,
    czyMoznaWyszukac: Boolean,
    czyNazwaIstnieje: Boolean
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        OutlinedTextField(
            value = tekstWyszukiwania,
            onValueChange = { onZmianaTextu(it) },
            modifier = Modifier.weight(1f),
            label = { Text("Poszukaj lub dodaj pieska 🐕") },
            singleLine = true,
            isError = czyNazwaIstnieje,
            colors = if (czyNazwaIstnieje) {
                TextFieldDefaults.outlinedTextFieldColors(
                    focusedBorderColor = Color.Red,
                    unfocusedBorderColor = Color.Red
                )
            } else {
                TextFieldDefaults.outlinedTextFieldColors()
            }
        )

        IconButton(
            onClick = onWyszukaj,
            enabled = czyMoznaWyszukac
        ) {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "Szukaj"
            )
        }

        IconButton(
            onClick = onDodaj,
            enabled = czyMoznaWyszukac && !czyNazwaIstnieje
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Dodaj"
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ElementListyPiesek(
    pies: Pies,
    onKliknij: () -> Unit,
    onToggleUlubione: () -> Unit,
    onUsun: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        onClick = onKliknij
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Zdjęcie
            AsyncImage(
                model = pies.urlZdjecia,
                contentDescription = "Zdjęcie psa",
                modifier = Modifier
                    .size(50.dp)
                    .padding(end = 8.dp)
            )

            // Informacje o psie
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 8.dp)
            ) {
                Text(
                    text = pies.nazwa,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                if (pies.rasa.isNotBlank()) {
                    Text(
                        text = pies.rasa,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }

            // Przyciski akcji
            IconButton(onClick = onToggleUlubione) {
                Icon(
                    imageVector = if (pies.jestUlubiony) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                    contentDescription = "Ulubione",
                    tint = if (pies.jestUlubiony) Color.Magenta else Color.Gray
                )
            }

            IconButton(onClick = onUsun) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Usuń"
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ElementListyUzytkownik(uzytkownik: Uzytkownik) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Ikona użytkownika
            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = null,
                modifier = Modifier
                    .size(40.dp)
                    .padding(end = 16.dp)
            )

            // Informacje o użytkowniku
            Column {
                Text(
                    text = uzytkownik.name,
                    style = MaterialTheme.typography.titleMedium
                )

                Text(
                    text = uzytkownik.email,
                    style = MaterialTheme.typography.bodyMedium
                )

                Text(
                    text = uzytkownik.company.name,
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}