package com.example.poibrowser.data.source.local

import androidx.lifecycle.LiveData
import androidx.lifecycle.map
import com.example.poibrowser.data.model.FourSquareModel
import com.example.poibrowser.data.model.Result
import com.example.poibrowser.data.source.MapDataSource
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * @author Tomislav Curis
 */
class LocalMapDataSource(
    private val poibDao: POIBDao,
    private val dispatchers: CoroutineDispatcher = Dispatchers.IO) : MapDataSource {

    override fun observeVenues(latlng: LatLng): LiveData<Result<List<FourSquareModel>>> {

        val lat1 = (latlng.latitude - 0.00918276656434).toString()
        val lat2 = (latlng.latitude + 0.00918276656434).toString()
        val lng1 = (latlng.longitude - 0.0077244266868).toString()
        val lng2 = (latlng.longitude + 0.0077244266868).toString()

        return poibDao.observeVenues(lat1, lat2, lng1, lng2).map { Result.Success(it) }

    }

    override suspend fun saveVenues(venues: List<FourSquareModel>) =
        withContext(dispatchers){
            poibDao.saveVenues(venues)
        }

    override suspend fun saveVenue(venue: FourSquareModel) {
        withContext(dispatchers){
            poibDao.saveVenue(venue)
        }
    }


    override suspend fun searchVenues(latlng: LatLng, radius: Double): Result<List<FourSquareModel>> =
        withContext(dispatchers) {
            return@withContext try {

                val lat1 = (latlng.latitude - 0.00918276656434).toString()
                val lat2 = (latlng.latitude + 0.00918276656434).toString()
                val lng1 = (latlng.longitude - 0.0077244266868).toString()
                val lng2 = (latlng.longitude + 0.0077244266868).toString()

                Result.Success(poibDao.getVenues(lat1, lat2, lng1, lng2)) // latlng, limit
            } catch (e: Exception) {
                Result.Error(e)
            }
        }

    override suspend fun getVenueDetail(venueId: String): Result<FourSquareModel> =
        withContext(dispatchers) {
            return@withContext try {
                Result.Success(poibDao.getVenue(venueId)) // latlng, limit
            } catch (e: Exception) {
                Result.Error(e)
            }
        }


    override suspend fun deleteVenues() =
        withContext(dispatchers) {
            poibDao.deleteVenues()
        }

}