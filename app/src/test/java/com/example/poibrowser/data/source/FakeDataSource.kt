package com.example.poibrowser.data.source

import androidx.lifecycle.LiveData
import com.example.poibrowser.data.model.FourSquareModel
import com.google.android.gms.maps.model.LatLng
import com.example.poibrowser.data.model.Result
import java.lang.Exception

/**
 * @author Tomislav Curis
 */
class FakeDataSource (var venues: MutableList<FourSquareModel>? = mutableListOf()) : MapDataSource {



    override fun observeVenues(latlng: LatLng): LiveData<Result<List<FourSquareModel>>> {
        TODO("Not yet implemented")
    }

    override suspend fun saveVenues(venues: List<FourSquareModel>) {
        this.venues?.clear()
        this.venues?.addAll(venues)
    }

    override suspend fun saveVenue(venue: FourSquareModel) {
        this.venues?.add(venue)
    }

    override suspend fun searchVenues(latlng: LatLng, radius: Double): Result<List<FourSquareModel>> {
        venues?.let { return  Result.Success(ArrayList(it)) }
        return Result.Error(Exception("Venues not found"))
    }

    override suspend fun getVenueDetail(venueId: String): Result<FourSquareModel> {
        venues?.let { return  Result.Success((it[0])) }
        return Result.Error(Exception("Venues not found"))
    }

    override suspend fun deleteVenues() {
        venues?.clear()
    }
}