package com.example.doggos.dane.baza

import androidx.room.*
import com.example.doggos.dane.baza.encje.PiesEncja
import kotlinx.coroutines.flow.Flow

@Dao
interface PiesDao {
    @Query("SELECT * FROM pieski ORDER BY jestUlubiony DESC, nazwa ASC")
    fun pobierzWszystkiePieski(): Flow<List<PiesEncja>>

    @Query("SELECT * FROM pieski WHERE nazwa LIKE :wzorzec")
    suspend fun szukajPieski(wzorzec: String): List<PiesEncja>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun dodajPieska(pies: PiesEncja): Long

    @Update
    suspend fun aktualizujPieska(pies: PiesEncja)

    @Delete
    suspend fun usunPieska(pies: PiesEncja)

    @Query("SELECT * FROM pieski WHERE id = :id")
    suspend fun pobierzPieskaPoId(id: Long): PiesEncja?

    @Query("SELECT COUNT(*) FROM pieski WHERE nazwa = :nazwa")
    suspend fun liczPieskiZNazwa(nazwa: String): Int
}