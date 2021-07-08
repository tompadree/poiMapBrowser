package com.example.poibrowser.data.source

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.poibrowser.data.model.FourSquareModel
import com.example.poibrowser.data.model.Result
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.runBlocking
import java.lang.Exception

/**
 * @author Tomislav Curis
 */
class FakeRepository: MapRepository {

    var currentListVenues: List<FourSquareModel> = mutableListOf()

    private var shouldReturnError = false

    private val observeVenues = MutableLiveData<Result<List<FourSquareModel>>>()

    fun setReturnError(value: Boolean) {
        shouldReturnError = value
    }


    override fun observeVenues(latlng: LatLng): LiveData<Result<List<FourSquareModel>>> {
        runBlocking { observeVenues.value = Result.Success(currentListVenues) }
        return observeVenues
    }

    override suspend fun saveVenues(venues: List<FourSquareModel>) {
        (currentListVenues as ArrayList).clear()
        (currentListVenues as ArrayList).addAll(venues)
    }

    override suspend fun searchVenues(
        update: Boolean,
        latlng: LatLng,
        radius: Double
    ): Result<List<FourSquareModel>> {
        if(shouldReturnError) {
            return Result.Error(Exception("Test exception"))
        }
        return Result.Success(currentListVenues)
    }

    override suspend fun getVenueDetail(venueId: String): Result<FourSquareModel> {
        if(shouldReturnError) {
            return Result.Error(Exception("Test exception"))
        }
        return Result.Success(currentListVenues[0])
    }

    override suspend fun saveVenue(venue: FourSquareModel) {
        (currentListVenues as ArrayList).remove(venue)
        (currentListVenues as ArrayList).add(venue)
    }
}