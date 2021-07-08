package com.example.poibrowser.data.source

import androidx.lifecycle.LiveData
import com.example.poibrowser.data.model.FourSquareModel
import com.example.poibrowser.utils.helpers.wrapEspressoIdlingResource
import java.lang.Exception
import com.example.poibrowser.data.model.Result
import com.google.android.gms.maps.model.LatLng

/**
 * @author Tomislav Curis
 */
class MapRepositoryImpl(
    private val localMapsDataSource: MapDataSource,
    private val remoteMapsDataSource: MapDataSource) : MapRepository {

    override fun observeVenues(latlng: LatLng): LiveData<Result<List<FourSquareModel>>> =
       wrapEspressoIdlingResource {
           localMapsDataSource.observeVenues(latlng)
       }

    override suspend fun saveVenues(venues: List<FourSquareModel>) =
        wrapEspressoIdlingResource {
            localMapsDataSource.saveVenues(venues)
        }

    override suspend fun saveVenue(venue: FourSquareModel) {
        wrapEspressoIdlingResource {
            localMapsDataSource.saveVenue(venue)
        }
    }

    override suspend fun searchVenues(
        update: Boolean,
        latlng: LatLng, radius: Double
    ): Result<List<FourSquareModel>> {
        if(update)
            try {
                updateVenuesFromRemote(latlng, radius)
            } catch (e: Exception) {
                return Result.Error(e)
            }
        return localMapsDataSource.searchVenues(latlng, radius)
    }

    override suspend fun getVenueDetail(venueId: String): Result<FourSquareModel> {
        wrapEspressoIdlingResource {
            var localDetails = localMapsDataSource.getVenueDetail(venueId)
            if (localDetails is Result.Success && !localDetails.data.canonicalUrl.isNullOrEmpty()) {
                return localMapsDataSource.getVenueDetail(venueId)
            } else {
                localDetails = remoteMapsDataSource.getVenueDetail(venueId)
                if (localDetails is Result.Success)
                    localMapsDataSource.saveVenue(localDetails.data)
                return localDetails
            }
        }
    }

    private suspend fun updateVenuesFromRemote(latlng: LatLng, radius: Double) {
        wrapEspressoIdlingResource {
            val remoteVenues = remoteMapsDataSource.searchVenues(latlng, radius)
            if(remoteVenues is Result.Success) {
                localMapsDataSource.saveVenues(remoteVenues.data)
            } else if (remoteVenues is Result.Error) {
                throw remoteVenues.exception
            }
        }
    }
}