package com.example.doggos.dane.baza

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.doggos.dane.baza.encje.PiesEncja
import com.example.doggos.dane.baza.encje.UzytkownikEncja

@Database(entities = [PiesEncja::class, UzytkownikEncja::class], version = 2, exportSchema = false)
abstract class BazaDanych : RoomDatabase() {
    abstract fun piesDao(): PiesDao
    abstract fun uzytkownikDao(): UzytkownikDao
}