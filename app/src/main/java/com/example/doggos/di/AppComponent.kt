package com.example.doggos.di

import com.example.doggos.ui.ekrany.glowny.GlownyViewModel
import com.example.doggos.ui.ekrany.szczegoly.SzczegolyViewModel
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [AppModul::class, BazaModul::class, NetworkModul::class])
interface AppComponent {
    fun wstrzyknijDoGlownyViewModel(viewModel: GlownyViewModel)
    fun wstrzyknijDoSzczegolyViewModel(viewModel: SzczegolyViewModel)
}