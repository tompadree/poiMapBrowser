package com.example.poibrowser.data.source.local

import androidx.lifecycle.LiveData
import androidx.lifecycle.map
import com.example.poibrowser.data.model.FourSquareModel
import com.example.poibrowser.data.model.Result
import com.example.poibrowser.data.source.MapDataSource
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * @author Tomislav Curis
 */
class LocalMapDataSource(
    private val poibDao: POIBDao,
    private val dispatchers: CoroutineDispatcher = Dispatchers.IO) : MapDataSource {

    override fun observeVenues(latlng: String): LiveData<Result<List<FourSquareModel>>> =
        poibDao.observeVenues().map { Result.Success(it) }

    override suspend fun saveVenues(venues: List<FourSquareModel>) =
        withContext(dispatchers){
            poibDao.saveVenues(venues)
        }


    override suspend fun searchVenues(latlng: String): Result<List<FourSquareModel>> =
        withContext(dispatchers) {
            return@withContext try {
                Result.Success(poibDao.getVenues()) // latlng, limit
            } catch (e: Exception) {
                Result.Error(e)
            }
        }

    override suspend fun deleteVenues() =
        withContext(dispatchers) {
            poibDao.deleteVenues()
        }

}