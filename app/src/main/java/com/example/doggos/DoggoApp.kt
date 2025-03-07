package com.example.doggos

import android.app.Application
import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.doggos.di.AppComponent
import com.example.doggos.di.AppModul
import com.example.doggos.di.DaggerAppComponent
import com.example.doggos.ui.ekrany.glowny.GlownyEkran
import com.example.doggos.ui.ekrany.szczegoly.SzczegolyEkran
import com.example.doggos.ui.motywy.DoggosTheme

class DoggoApp : Application() {
    companion object {
        lateinit var appComponent: AppComponent
    }

    override fun onCreate() {
        super.onCreate()
        appComponent = DaggerAppComponent.builder()
            .appModul(AppModul(this))
            .build()
    }
}

@Composable
fun DoggosApp() {
    val navController = rememberNavController()

    DoggosTheme(darkTheme = false) {  // Wymuszamy jasny motyw
        NavHost(
            navController = navController,
            startDestination = "glowny"
        ) {
            composable("glowny") {
                GlownyEkran(navController)
            }

            composable(
                route = "szczegoly/{piesId}",
                arguments = listOf(
                    navArgument("piesId") {
                        type = NavType.LongType
                    }
                )
            ) { backStackEntry ->
                val piesId = backStackEntry.arguments?.getLong("piesId") ?: -1
                SzczegolyEkran(navController, piesId)
            }
        }
    }
}