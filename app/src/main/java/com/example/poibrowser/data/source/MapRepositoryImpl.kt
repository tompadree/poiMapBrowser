package com.example.poibrowser.data.source

import androidx.lifecycle.LiveData
import com.example.poibrowser.data.model.FourSquareModel
import com.example.poibrowser.utils.helpers.wrapEspressoIdlingResource
import com.google.android.gms.maps.model.LatLng
import java.lang.Exception
import com.example.poibrowser.data.model.Result

/**
 * @author Tomislav Curis
 */
class MapRepositoryImpl(
    private val localMapsDataSource: MapDataSource,
    private val remoteMapsDataSource: MapDataSource) : MapRepository {

    override fun observeVenues(latlng: String): LiveData<Result<List<FourSquareModel>>> =
       wrapEspressoIdlingResource {
           localMapsDataSource.observeVenues(latlng)
       }

    override suspend fun saveVenues(venues: List<FourSquareModel>) =
        wrapEspressoIdlingResource {
            localMapsDataSource.saveVenues(venues)
        }

    override suspend fun searchVenues(
        update: Boolean,
        latlng: String
    ): Result<List<FourSquareModel>> {
        if(update)
            try {
                updateVenuesFromRemote(latlng)
            } catch (e: Exception) {
                return Result.Error(e)
            }
        return localMapsDataSource.searchVenues(latlng)
    }

    private suspend fun updateVenuesFromRemote(latlng: String) {
        wrapEspressoIdlingResource {
            val remoteVenues = remoteMapsDataSource.searchVenues(latlng)
            if(remoteVenues is Result.Success) {
                localMapsDataSource.saveVenues(remoteVenues.data)
            } else if (remoteVenues is Result.Error) {
                throw remoteVenues.exception
            }
        }
    }
}