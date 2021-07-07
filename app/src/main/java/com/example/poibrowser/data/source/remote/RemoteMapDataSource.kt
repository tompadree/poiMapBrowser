package com.example.poibrowser.data.source.remote

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.poibrowser.data.model.FourSquareModel
import com.example.poibrowser.data.source.MapDataSource
import java.io.IOException
import com.example.poibrowser.data.model.Result

/**
 * @author Tomislav Curis
 */
class RemoteMapDataSource(private val fourSquareAPI: FourSquareAPI) : MapDataSource {

    private val observableVenues = MutableLiveData<Result<List<FourSquareModel>>>()

    override fun observeVenues(latlng: String): LiveData<Result<List<FourSquareModel>>> = observableVenues

    override suspend fun saveVenues(venues: List<FourSquareModel>) {
        TODO("Not yet implemented")
    }

    override suspend fun searchVenues(latlng: String): Result<List<FourSquareModel>> {
        val response =
            fourSquareAPI.searchVenues(ll= latlng, radius = "200")
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

    override suspend fun deleteVenues() {
        TODO("Not yet implemented")
    }
}