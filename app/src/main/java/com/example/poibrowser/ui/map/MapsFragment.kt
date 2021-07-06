package com.example.poibrowser.ui.map

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.poibrowser.R
import com.example.poibrowser.databinding.FragmentMapsBinding
import com.example.poibrowser.ui.BindingFragment
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PointOfInterest
import com.google.android.material.bottomsheet.BottomSheetBehavior
import kotlinx.android.synthetic.main.fragment_maps.*
import org.koin.androidx.viewmodel.ext.android.viewModel


class MapsFragment : BindingFragment<FragmentMapsBinding>(), GoogleMap.OnPoiClickListener {

    override val layoutId = R.layout.fragment_maps

    private val viewModel: MapViewModel by viewModel()

    private lateinit var bottomSheet: BottomSheetBehavior<ConstraintLayout>
    private lateinit var googleMap: GoogleMap

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        binding.viewModel = viewModel
        binding.lifecycleOwner = this.viewLifecycleOwner


        setupMap()
        setupBottomSheet()
        setClickListeners()
    }

    override fun onPoiClick(p0: PointOfInterest) {
        Log.e("TEST", googleMap.getCameraPosition().target.latitude.toString())
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(p0.latLng ))
        bottomSheet.state = BottomSheetBehavior.STATE_COLLAPSED
        viewModel.poiClicked(p0)
    }

    fun setClickListeners(){

    }

    fun setupBottomSheet() {
        bottomSheet = BottomSheetBehavior.from(bottomSheetLayout)
        bottomSheet.state = BottomSheetBehavior.STATE_HIDDEN
    }

    fun setupMap() {
//        mapsBinding(map, 100)
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(callback)
    }

        private val callback = OnMapReadyCallback { gmap ->
        /**
         * Manipulates the map once available.
         * This callback is triggered when the map is ready to be used.
         * This is where we can add markers or lines, add listeners or move the camera.
         * In this case, we just add a marker near Sydney, Australia.
         * If Google Play services is not installed on the device, the user will be prompted to
         * install it inside the SupportMapFragment. This method will only be triggered once the
         * user has installed Google Play services and returned to the app.
         */
            googleMap = gmap
        val evenly = LatLng(52.500342, 13.425170)
//        googleMap.addMarker(MarkerOptions().position(evenly).title("Marker on evenly"))
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(evenly, 17f))
            googleMap.setOnPoiClickListener(this)
    }
}