package com.example.poibrowser.ui.map

import android.content.Context
import android.util.Log
import android.view.View
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.graphics.drawable.toBitmap
import androidx.recyclerview.widget.DiffUtil
import com.example.poibrowser.R
import com.example.poibrowser.data.model.FourSquareModel
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng
import nz.co.trademe.mapme.annotations.AnnotationFactory
import nz.co.trademe.mapme.annotations.MapAnnotation
import nz.co.trademe.mapme.annotations.MarkerAnnotation
import nz.co.trademe.mapme.annotations.OnMapAnnotationClickListener


/**
 * @author Tomislav Curis
 */
class MapAdapter(context: Context) : MyGoogleMapMeAdapter<FourSquareModel>(context, MapMarkersDiffUtil() ) {

    var annotationPositionClickListener: OnMapAnnotationClickPositionListener? = null

    fun attachCustomListener(map: GoogleMap) {
        this.factory.setOnMarkerClickListener(map) { marker -> notifyAnnotatedMarkerPositionClicked(marker) }
    }

    fun notifyAnnotatedMarkerPositionClicked(marker: Any): Boolean {
        val clickListener = annotationPositionClickListener ?: return false

        val annotation = annotations.find { it.annotatesObject(marker) }

        if (annotation != null) {
            val item: FourSquareModel = this.getItem(annotation.position)
            val latLng = LatLng(item.location.lat.toDouble(), item.location.lng.toDouble())
            return clickListener.onMapAnnotationPositionClick(annotation, latLng)
        } else {
            Log.e("MapMeAdapter", "Unable to find an annotation that annotates the marker")
        }
        return false
    }

    fun setOnAnnotationPositionClickListener(listener: OnMapAnnotationClickPositionListener) {
        this.annotationPositionClickListener = listener
    }


    override fun onBindAnnotation(annotation: MapAnnotation, position: Int, payload: Any?) {
        if (annotation is MarkerAnnotation) {
            annotation.icon = getIconBitmap()
        }
    }

    override fun onCreateAnnotation(factory: AnnotationFactory<GoogleMap>, position: Int, annotationType: Int
    ): MapAnnotation {
        val item: FourSquareModel = this.getItem(position)
        val latLng = nz.co.trademe.mapme.LatLng(item.location.lat.toDouble(), item.location.lng.toDouble())

        return factory.createMarker(latLng, getIconBitmap(), item.name)
    }

    fun getIconBitmap() = AppCompatResources.getDrawable(context, R.drawable.ic_baseline_location_on_24)?.toBitmap()
}

class MapMarkersDiffUtil : DiffUtil.ItemCallback<FourSquareModel>() {
    override fun areContentsTheSame(oldItem: FourSquareModel, newItem: FourSquareModel): Boolean {
        return oldItem.location.formattedAddress == newItem.location.formattedAddress
    }

    override fun areItemsTheSame(oldItem: FourSquareModel, newItem: FourSquareModel): Boolean {
        return oldItem.id == newItem.id
    }
}

interface OnMapAnnotationClickPositionListener {

    fun onMapAnnotationPositionClick(mapAnnotationObject: MapAnnotation, position: LatLng): Boolean

}