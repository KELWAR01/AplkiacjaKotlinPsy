package com.example.doggos.dane.baza

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.doggos.dane.baza.encje.UzytkownikEncja
import kotlinx.coroutines.flow.Flow

@Dao
interface UzytkownikDao {
    @Query("SELECT * FROM uzytkownicy")
    fun pobierzWszystkichUzytkownikow(): Flow<List<UzytkownikEncja>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun dodajUzytkownikow(uzytkownicy: List<UzytkownikEncja>)

    @Query("SELECT COUNT(*) FROM uzytkownicy")
    suspend fun liczbaUzytkownikow(): Int
}