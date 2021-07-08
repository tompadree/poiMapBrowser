package com.example.poibrowser.ui.map

import android.location.Location
import android.os.SystemClock
import androidx.databinding.ObservableField
import androidx.lifecycle.*
import com.example.poibrowser.data.model.FourSquareModel
import com.example.poibrowser.data.model.Result
import com.example.poibrowser.data.source.MapRepository
import com.example.poibrowser.utils.helpers.SingleLiveEvent
import com.example.poibrowser.utils.network.InternetConnectionManager
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.VisibleRegion
import kotlinx.coroutines.launch

/**
 * @author Tomislav Curis
 */
class MapViewModel(
    private val repository: MapRepository,
    private val internetConnectionManager: InternetConnectionManager
) : ViewModel() {

    val title = ObservableField("Evenly")
    val subTitle = ObservableField("Software company")

    val latLng = ObservableField("52.500337048569975, 13.425081651523898")
    val latLngDouble = ObservableField(LatLng(52.500337048569975, 13.425081651523898))
    val visibleRegion = ObservableField<VisibleRegion>(
        VisibleRegion(
            LatLng(52.49559676589108, 13.421296812593935),
            LatLng(52.49559676589108, 13.4290212392807),
            LatLng(52.50477953245542, 13.421296812593935),
            LatLng(52.50477953245542, 13.4290212392807),
            LatLngBounds(
                LatLng(52.49559676589108, 13.421296812593935),
                LatLng(52.50477953245542, 13.4290212392807)
            ) // 0.00918276656434, 0.0077244266868
        )
    )

    val isInternetAvailable = ObservableField(true)
    val isDataLoadingError = MutableLiveData<Boolean>(false)

    val _itemClicked = SingleLiveEvent<String>()
    val itemClicked: LiveData<String> = _itemClicked

    protected val _error = SingleLiveEvent<Throwable>()
    val error: LiveData<Throwable> get() = _error

    private val _dataLoading = MutableLiveData<Boolean>()
    val dataLoading: LiveData<Boolean> = _dataLoading

    private val _forceUpdate = MutableLiveData<Boolean>(false)

    val canonicalURL = SingleLiveEvent<String>()

    private val _venues: LiveData<List<FourSquareModel>> = _forceUpdate.switchMap { forceUpdate ->
        if (forceUpdate) {
            viewModelScope.launch {
                handleResponseWithError(repository.searchVenues(true, latLngDouble.get()!!, getMapVisibleRadius()))
                _dataLoading.value = false
            }
        } else
            _dataLoading.value = false

        repository.observeVenues(latLngDouble.get()!!).map {
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

    fun fetchCanonicalUrl(venueId: String?) {
        if (!internetConnectionManager.hasInternetConnection()) {
            isInternetAvailable.set(false)
        } else {
            if (isInternetAvailable.get() == false) isInternetAvailable.set(true)
            viewModelScope.launch {
                when (val response = repository.getVenueDetail(venueId ?: "")) {
                    is Result.Success -> {
                        isDataLoadingError.value = false
                        canonicalURL.postValue(
                            response.data.canonicalUrl ?: "https://foursquare.com/"
                        )
                    }
                    is Result.Error -> {
                        isDataLoadingError.value = true
                        _error.postValue(response.exception)
                    }
                    is Result.Loading -> MutableLiveData(null)
                }
            }
        }
    }

    var lastTime = 0L // Prevent backend call overflow
    fun refresh(refresh: Boolean) {

        // Temp workaround
        if(SystemClock.currentThreadTimeMillis() - lastTime > 500 || SystemClock.currentThreadTimeMillis() == 0L) {
            loadPoints(refresh)
            lastTime = SystemClock.currentThreadTimeMillis()
        }
    }
    private fun loadPoints(isLoad: Boolean) {
        if(!internetConnectionManager.hasInternetConnection()) {
            isInternetAvailable.set(false)
        }
        else {
            if(isInternetAvailable.get() == false) isInternetAvailable.set(true)
            _dataLoading.postValue(isLoad)
            _forceUpdate.postValue(isLoad)
        }
    }


    fun onCameraMoved(centerLatLng: LatLng) {
        latLngDouble.set(centerLatLng)
        latLng.set(centerLatLng.latitude.toString() + ", " + centerLatLng.longitude)
        refresh(true)
    }

    fun poiClicked(position: Int) {
        val item = items.value?.get(position)
        fetchCanonicalUrl(item?.id)

        latLngDouble.set(LatLng(item?.location?.lat?.toDouble()?: 0.0, item?.location?.lng?.toDouble()?: 0.0))
        latLng.set(item?.location?.lat + ", " + item?.location?.lng)

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

    // https://stackoverflow.com/a/50625121/5577679
    private fun getMapVisibleRadius() : Double {
        if(visibleRegion.get() != null) {
            val distanceWidth = FloatArray(1)
            val distanceHeight = FloatArray(1)
            val farRight = visibleRegion.get()!!.farRight
            val farLeft = visibleRegion.get()!!.farLeft
            val nearRight = visibleRegion.get()!!.nearRight
            val nearLeft = visibleRegion.get()!!.nearLeft

            Location.distanceBetween(
                (farLeft.latitude + nearLeft.latitude) / 2,
                farLeft.longitude,
                (farRight.latitude + nearRight.latitude) / 2,
                farRight.longitude,
                distanceWidth
            )
            Location.distanceBetween(
                farRight.latitude,
                (farRight.longitude + farLeft.longitude) / 2,
                nearRight.latitude,
                (nearRight.longitude + nearLeft.longitude) / 2,
                distanceHeight
            )
            return Math.sqrt(
                Math.pow(
                    distanceWidth[0]
                        .toDouble(), 2.0
                ) + Math.pow(distanceHeight[0].toDouble(), 2.0)
            ) / 2
        }
        return 200.0
    }
}