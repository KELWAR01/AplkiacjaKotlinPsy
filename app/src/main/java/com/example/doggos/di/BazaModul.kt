package com.example.doggos.di

import android.content.Context
import androidx.room.Room
import com.example.doggos.dane.baza.BazaDanych
import com.example.doggos.dane.baza.PiesDao
import com.example.doggos.dane.baza.UzytkownikDao
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class BazaModul {

    @Provides
    @Singleton
    fun zapewnijBazeDanych(context: Context): BazaDanych {
        return Room.databaseBuilder(
            context,
            BazaDanych::class.java,
            "pieski-baza"
        )
            .fallbackToDestructiveMigration() // Dodane, aby obsłużyć zmianę schematu
            .build()
    }

    @Provides
    @Singleton
    fun zapewnijPiesDao(bazaDanych: BazaDanych): PiesDao {
        return bazaDanych.piesDao()
    }

    @Provides
    @Singleton
    fun zapewnijUzytkownikDao(bazaDanych: BazaDanych): UzytkownikDao {
        return bazaDanych.uzytkownikDao()
    }
}