package com.example.doggos.ui.ekrany.szczegoly

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SzczegolyEkran(
    navController: NavController,
    piesId: Long,
    viewModel: SzczegolyViewModel = viewModel(factory = SzczegolyViewModel.Factory(piesId))
) {
    val uiStan by viewModel.uiStan.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detale") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Powrót"
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.odswiezZdjecie() }) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Odśwież zdjęcie"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            val pies = uiStan.pies

            if (pies != null) {
                // Zdjęcie pieska
                AsyncImage(
                    model = pies.urlZdjecia,
                    contentDescription = "Zdjęcie psa",
                    modifier = Modifier
                        .size(200.dp)
                        .padding(bottom = 16.dp)
                )

                Text(
                    text = pies.nazwa,
                    style = MaterialTheme.typography.headlineMedium,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                // Wybór rasy
                Text(
                    text = "Rasa:",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier
                        .align(Alignment.Start)
                        .padding(top = 16.dp, bottom = 8.dp)
                )

                var expandedRasa by remember { mutableStateOf(false) }
                ExposedDropdownMenuBox(
                    expanded = expandedRasa,
                    onExpandedChange = { expandedRasa = !expandedRasa }
                ) {
                    OutlinedTextField(
                        value = pies.rasa.ifEmpty { "Wybierz rasę" },
                        onValueChange = {},
                        readOnly = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(),
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedRasa) }
                    )

                    ExposedDropdownMenu(
                        expanded = expandedRasa,
                        onDismissRequest = { expandedRasa = false }
                    ) {
                        uiStan.dostepneRasy.forEach { rasa ->
                            DropdownMenuItem(
                                text = { Text(rasa) },
                                onClick = {
                                    viewModel.wybierzRase(rasa)
                                    expandedRasa = false
                                }
                            )
                        }
                    }
                }

                // Przycisk odświeżania zdjęcia
                Button(
                    onClick = { viewModel.odswiezZdjecie() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 24.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Odśwież",
                        modifier = Modifier.padding(end = 8.dp)
                    )
                    Text(text = "Odśwież zdjęcie")
                }
            } else {
                CircularProgressIndicator()
                Text(
                    text = "Ładowanie danych pieska...",
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(top = 16.dp)
                )
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