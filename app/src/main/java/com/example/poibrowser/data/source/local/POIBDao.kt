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
    @Query("SELECT * FROM venues WHERE (location_lat > :lat1 AND location_lat < :lat2 AND location_lng > :lng1 AND location_lng < :lng2)")
    fun observeVenues(lat1: String, lat2: String, lng1: String, lng2: String): LiveData<List<FourSquareModel>>

    /**
     * Delete all venues.
     */
    @Query("DELETE FROM venues")
    suspend fun deleteVenues()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun saveVenues(venues: List<FourSquareModel>) // : LongArray

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun saveVenue(venues: FourSquareModel)

//    0.00918276656434, 0.0077244266868
    @Query("SELECT * FROM venues WHERE (location_lat > :lat1 AND location_lat < :lat2 AND location_lng > :lng1 AND location_lng < :lng2)")
    fun getVenues(lat1: String, lat2: String, lng1: String, lng2: String): List<FourSquareModel>

    @Query("SELECT * FROM venues WHERE id LIKE :venueId")
    fun getVenue(venueId: String): FourSquareModel

}