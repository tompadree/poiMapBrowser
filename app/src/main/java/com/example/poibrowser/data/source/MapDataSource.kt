package com.example.poibrowser.data.source

import androidx.lifecycle.LiveData
import com.example.poibrowser.data.model.FourSquareModel
import com.google.android.gms.maps.model.LatLng
import com.example.poibrowser.data.model.Result

/**
 * @author Tomislav Curis
 */
interface MapDataSource {

    fun observeVenues(latlng: String): LiveData<Result<List<FourSquareModel>>>

    suspend fun saveVenues(venues: List<FourSquareModel>)

    suspend fun searchVenues(latlng: String) : Result<List<FourSquareModel>>

    // For test only
    suspend fun deleteVenues()
}