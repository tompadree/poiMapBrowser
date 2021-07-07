package com.example.poibrowser.ui.map

import android.os.SystemClock
import androidx.databinding.ObservableField
import androidx.lifecycle.*
import com.example.poibrowser.data.model.FourSquareModel
import com.example.poibrowser.data.source.MapRepository
import com.example.poibrowser.utils.helpers.SingleLiveEvent
import com.google.android.gms.maps.model.PointOfInterest
import kotlinx.coroutines.launch
import com.example.poibrowser.data.model.Result
import com.google.android.gms.maps.model.LatLng


/**
 * @author Tomislav Curis
 */
class MapViewModel(private val repository: MapRepository) : ViewModel() {

    val title = ObservableField("Evenly")
    val subTitle = ObservableField("Software company")

    val latLng = ObservableField("52.500337048569975, 13.425081651523898")
    val latLngDouble = ObservableField(LatLng(52.500337048569975, 13.425081651523898))

    val isSearchEmpty = ObservableField<Boolean>(true)
    val isInternetAvailable = ObservableField(true)

    val isDataLoadingError = MutableLiveData<Boolean>(false)

    val _itemClicked = SingleLiveEvent<String>()
    val itemClicked: LiveData<String> = _itemClicked

    protected val _error = SingleLiveEvent<Throwable>()
    val error: LiveData<Throwable> get() = _error

    private val _dataLoading = MutableLiveData<Boolean>()
    val dataLoading: LiveData<Boolean> = _dataLoading

    private val _forceUpdate = MutableLiveData<Boolean>(false)

    private val _venues: LiveData<List<FourSquareModel>> = _forceUpdate.switchMap { forceUpdate ->
        if (forceUpdate) {
            viewModelScope.launch {
                handleResponseWithError(repository.searchVenues(true, latLng.get()!!))
                _dataLoading.value = false
            }
        } else
            _dataLoading.value = false

        repository.observeVenues(latLng.get()!!).map {
            if (it is Result.Success) {
                isDataLoadingError.value = false
                it.data

            } else if(it is Result.Error){
                _error.postValue(it.exception)
                isDataLoadingError.value = true
                emptyList()
            }
            else
                emptyList()
        }
    }

    val items: LiveData<List<FourSquareModel>> = _venues

    // This LiveData depends on another so we can use a transformation.
    val empty: LiveData<Boolean> = Transformations.map(_venues) {
        it.isEmpty()
    }

    var lastTime = 0L
    fun refresh(refresh: Boolean) {

        if(SystemClock.currentThreadTimeMillis() - lastTime > 500 ) {
            _forceUpdate.postValue(refresh)
            lastTime = SystemClock.currentThreadTimeMillis()
        }
//        loadGifs(refresh)
    }

    fun onCameraMoved(centerLatLng: LatLng) {
        latLngDouble.set(centerLatLng)
        latLng.set(centerLatLng.latitude.toString() + ", " + centerLatLng.longitude)
        refresh(true)
    }

    fun poiClicked(position: Int) {
        latLngDouble.set(LatLng(items.value?.get(position)?.location?.lat?.toDouble()?: 0.0,
                items.value?.get(position)?.location?.lng?.toDouble()?: 0.0))
        latLng.set(items.value?.get(position)?.location?.lat
                + ", "
                + items.value?.get(position)?.location?.lng)
        title.set(items.value?.get(position)?.name)
//        subTitle.set(point.placeId)
    }

    private fun handleResponseWithError(response: Result<List<FourSquareModel>>): LiveData<List<FourSquareModel>> {
        return when (response) {
            is Result.Success -> {
                isDataLoadingError.value = false
                MutableLiveData(response.data) as LiveData<List<FourSquareModel>>
            }
            is Result.Error -> {
                isDataLoadingError.value = true
                _error.postValue(response.exception)
                MutableLiveData( response.exception) as LiveData<List<FourSquareModel>>
            }
            is Result.Loading -> MutableLiveData( null)
        }
    }
}