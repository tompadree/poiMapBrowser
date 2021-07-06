package com.example.poibrowser.ui.map

import androidx.databinding.BindingAdapter
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentContainerView
import com.example.poibrowser.R
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions


@BindingAdapter("app:maps")
fun mapsBinding(map: FragmentContainerView, value: Int) {

    val parentFrag = map.parent as Fragment
    val mapFragment = parentFrag.childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
    mapFragment?.getMapAsync(callback)
}

private val callback = OnMapReadyCallback { googleMap ->
    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera.
     * In this case, we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to
     * install it inside the SupportMapFragment. This method will only be triggered once the
     * user has installed Google Play services and returned to the app.
     */
    val evenly = LatLng(52.500342, 13.425170)
//    googleMap.addMarker(MarkerOptions().position(evenly).title("Marker on evenly"))
    googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(evenly, 17f))
}