package com.example.poibrowser.ui.map

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.poibrowser.R
import com.example.poibrowser.databinding.FragmentMapsBinding
import com.example.poibrowser.ui.BindingFragment
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.material.bottomsheet.BottomSheetBehavior
import kotlinx.android.synthetic.main.fragment_maps.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import com.example.poibrowser.utils.helpers.observe
import nz.co.trademe.mapme.annotations.MapAnnotation


class MapsFragment : BindingFragment<FragmentMapsBinding>(),
    GoogleMap.OnCameraMoveCanceledListener, OnMapAnnotationClickPositionListener {

    override val layoutId = R.layout.fragment_maps

    private val viewModel: MapViewModel by viewModel()

    private lateinit var bottomSheet: BottomSheetBehavior<ConstraintLayout>
    private lateinit var googleMap: GoogleMap
    private lateinit var mapAdapter: MapAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.viewModel = viewModel
        binding.lifecycleOwner = this.viewLifecycleOwner

        setupMap()
        setupBottomSheet()

        viewModel.refresh(true)
    }

    override fun onMapAnnotationPositionClick(mapAnnotationObject: MapAnnotation, position: LatLng
    ): Boolean {
        googleMap.animateCamera(CameraUpdateFactory.newLatLng(position))
        bottomSheet.state = BottomSheetBehavior.STATE_COLLAPSED
        viewModel.poiClicked(mapAnnotationObject.position)
        return true
    }

    override fun onCameraMoveCanceled() {
        viewModel.onCameraMoved(googleMap.cameraPosition.target)
    }

    fun setClickListeners() {
        mapAdapter.setOnAnnotationPositionClickListener(this)

        bottomSheetShareGMaps.setOnClickListener { shareToGMaps(viewModel.latLngDouble.get()!!) }
        bottomSheetShareFourSquare.setOnClickListener { shareFourSquare(viewModel.latLngDouble.get()!!) }
    }

    fun setupBottomSheet() {
        bottomSheet = BottomSheetBehavior.from(bottomSheetLayout)
        bottomSheet.state = BottomSheetBehavior.STATE_HIDDEN
    }

    fun setupObservers() {
        observeError(viewModel.error)

        viewModel.isDataLoadingError.observe(this) {

        }

        viewModel.items.observe(this) {
            it?.let {
                mapAdapter.submitList(it)
                mapAdapter.notifyDataSetChanged()
            }
        }
    }

    fun setupMap() {
//        mapsBinding(map, 100)
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapAdapter = MapAdapter(requireContext())

        mapFragment?.getMapAsync {
            googleMap = it
            val evenly = LatLng(52.500342, 13.425170)
            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(evenly, 16f))
            googleMap.setOnCameraMoveCanceledListener(this)
            mapAdapter.attach(mapFragment.requireView(), googleMap)
            mapAdapter.attachCustomListener(googleMap)

            setClickListeners()
            setupObservers()
        }
    }

    private fun shareToGMaps(latLng: LatLng) {
            val geoUri =
                "http://maps.google.com/maps?q=loc:" + latLng.latitude.toString() + "," + latLng.longitude.toString() +
                        " (" + "TEST" + ")"
           startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(geoUri)))
    }

    private fun shareFourSquare(latLng: LatLng) {
        val intent = Intent()
        val geoUri =
            "http://maps.google.com/maps?q=loc:" + latLng.latitude.toString() + "," + latLng.longitude.toString() +
                    " (" + "TEST" + ")"
        intent.action = Intent.ACTION_VIEW
        startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(geoUri)))
    }
}