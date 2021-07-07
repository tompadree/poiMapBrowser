package com.example.poibrowser.data.source.local

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.poibrowser.data.model.FourSquareModel

/**
 * @author Tomislav Curis
 */

@Dao
interface POIBDao {

    /**
     * Observes list of venues.
     *
     * @return all venues.
     */
    @Query("SELECT * FROM venues") // WHERE title LIKE :searchQuery LIMIT :limit OFFSET :offset")
    fun observeVenues(): LiveData<List<FourSquareModel>>

    /**
     * Delete all venues.
     */
    @Query("DELETE FROM venues")
    suspend fun deleteVenues()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun saveVenues(venues: List<FourSquareModel>) // : LongArray

    @Query("SELECT * FROM venues") // WHERE title LIKE :searchQuery LIMIT :limit OFFSET :offset")
    fun getVenues(): List<FourSquareModel>

}