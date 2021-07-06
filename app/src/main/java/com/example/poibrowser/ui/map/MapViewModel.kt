package com.example.poibrowser.ui.map

import androidx.databinding.ObservableField
import androidx.lifecycle.ViewModel
import com.google.android.gms.maps.model.PointOfInterest

/**
 * @author Tomislav Curis
 */
class MapViewModel : ViewModel() {

    val title = ObservableField("Evenly")
    val subTitle = ObservableField("Software company")

    fun poiClicked(point: PointOfInterest) {


        title.set(point.name)
        subTitle.set(point.placeId)



    }
}