package com.example.poibrowser.data.source.remote

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.poibrowser.data.model.FourSquareModel
import com.example.poibrowser.data.source.MapDataSource
import java.io.IOException
import com.example.poibrowser.data.model.Result
import com.google.android.gms.maps.model.LatLng

/**
 * @author Tomislav Curis
 */
class RemoteMapDataSource(private val fourSquareAPI: FourSquareAPI) : MapDataSource {

    private val observableVenues = MutableLiveData<Result<List<FourSquareModel>>>()

    override fun observeVenues(latlng: LatLng): LiveData<Result<List<FourSquareModel>>> = observableVenues

    override suspend fun saveVenues(venues: List<FourSquareModel>) {
        TODO("Not yet implemented")
    }

    override suspend fun searchVenues(latlng: LatLng, radius: Double): Result<List<FourSquareModel>> {
        val latLngString = latlng.latitude.toString() + ", " + latlng.longitude
        val response =
            fourSquareAPI.searchVenues(ll= latLngString, radius = radius.toString())
        if (response.isSuccessful) {
            val body = response.body()
            if (response.body() != null) {
                val result = Result.Success(body!!.response.venues)
                observableVenues.value = result
                return result
            }
        }
        return Result.Error(IOException("Error loading data " + "${response.code()} ${response.message()}"))

    }

    override suspend fun getVenueDetail(venueId: String): Result<FourSquareModel> {
        val response =
            fourSquareAPI.getVenueDetail(venueId)
        if (response.isSuccessful) {
            val body = response.body()
            if (response.body() != null) {
                val result = Result.Success(body!!.response.venue)
                return result
            }
        }
        return Result.Error(IOException("Error loading data " + "${response.code()} ${response.message()}"))

    }

    override suspend fun saveVenue(venue: FourSquareModel) {
        TODO("Not yet implemented")
    }

    override suspend fun deleteVenues() {
        TODO("Not yet implemented")
    }
}