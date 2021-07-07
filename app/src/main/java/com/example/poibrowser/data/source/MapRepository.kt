package com.example.poibrowser.data.source

import androidx.lifecycle.LiveData
import com.example.poibrowser.data.model.FourSquareModel
import com.google.android.gms.maps.model.LatLng
import com.example.poibrowser.data.model.Result

/**
 * @author Tomislav Curis
 */
interface MapRepository {


    fun observeVenues(latlng: String): LiveData<Result<List<FourSquareModel>>>

    suspend fun saveVenues(venues: List<FourSquareModel>)

    suspend fun searchVenues(update: Boolean = false, latlng: String): Result<List<FourSquareModel>>

}