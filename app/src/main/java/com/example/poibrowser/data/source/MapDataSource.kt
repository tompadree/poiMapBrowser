package com.example.poibrowser.data.source

import androidx.lifecycle.LiveData
import com.example.poibrowser.data.model.FourSquareModel
import com.google.android.gms.maps.model.LatLng
import com.example.poibrowser.data.model.Result

/**
 * @author Tomislav Curis
 */
interface MapDataSource {

    fun observeVenues(latlng: LatLng): LiveData<Result<List<FourSquareModel>>>

    suspend fun saveVenues(venues: List<FourSquareModel>)

    suspend fun saveVenue(venue: FourSquareModel)

    suspend fun searchVenues(latlng: LatLng, radius: Double): Result<List<FourSquareModel>>

    suspend fun getVenueDetail(venueId: String): Result<FourSquareModel>

    // For test only
    suspend fun deleteVenues()
}