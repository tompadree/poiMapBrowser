package com.example.poibrowser.ui.map

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.poibrowser.R
import com.example.poibrowser.databinding.FragmentMapsBinding
import com.example.poibrowser.ui.BindingFragment
import com.example.poibrowser.ui.map.adapters.MapAdapter
import com.example.poibrowser.ui.map.adapters.OnMapAnnotationClickPositionListener
import com.example.poibrowser.utils.helpers.observe
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.material.bottomsheet.BottomSheetBehavior
import kotlinx.android.synthetic.main.fragment_maps.*
import nz.co.trademe.mapme.annotations.MapAnnotation
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.lang.Exception


class MapsFragment : BindingFragment<FragmentMapsBinding>(), GoogleMap.OnCameraMoveStartedListener,
    GoogleMap.OnCameraIdleListener, OnMapAnnotationClickPositionListener {

    override val layoutId = R.layout.fragment_maps

    private val viewModel: MapViewModel by viewModel()

    private lateinit var bottomSheet: BottomSheetBehavior<ConstraintLayout>
    private lateinit var googleMap: GoogleMap
    private lateinit var mapAdapter: MapAdapter

    private var cameraStart = false

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this.viewLifecycleOwner

        setupMap()
        setupBottomSheet()

        viewModel.refresh(true)
    }

    override fun onMapAnnotationPositionClick(mapAnnotationObject: MapAnnotation, position: LatLng): Boolean {
        viewModel.poiClicked(mapAnnotationObject.position)
        googleMap.animateCamera(CameraUpdateFactory.newLatLng(position))
        bottomSheet.state = BottomSheetBehavior.STATE_COLLAPSED
        return true
    }

    override fun onCameraIdle() {
        if (cameraStart) {
            viewModel.visibleRegion.set(googleMap.projection.visibleRegion)
            viewModel.onCameraMoved(googleMap.cameraPosition.target)
            cameraStart = false
        }
    }

    override fun onCameraMoveStarted(p0: Int) {
        cameraStart = true
    }

    fun setClickListeners() {
        mapAdapter.setOnAnnotationPositionClickListener(this)

        bottomSheetShareGMaps.setOnClickListener { shareToGMaps(viewModel.latLngDouble.get()!!) }
        bottomSheetShareFourSquare.setOnClickListener { shareFourSquare() }
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

        viewModel.canonicalURL.observe(this) {
            it?.let { bottomSheetShareFourSquare.holdUrl = it }
        }
    }

    private fun setupMap() {
        try {
            val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
            mapAdapter = MapAdapter(requireContext())

            mapFragment?.getMapAsync {
                googleMap = it
                val evenly = LatLng(52.500342, 13.425170)
                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(evenly, 17f))
                googleMap.setOnCameraMoveStartedListener(this)
                googleMap.setOnCameraIdleListener(this)
                mapAdapter.attach(mapFragment.requireView(), googleMap)
                mapAdapter.attachCustomListener(googleMap)

                googleMap.setMapStyle(
                    MapStyleOptions.loadRawResourceStyle(
                        requireContext(),
                        R.raw.style_json
                    )
                )
                setClickListeners()
                setupObservers()
            }
        } catch (e: Exception) {
            showError(e)
            e.printStackTrace()
        }
    }

    private fun shareToGMaps(latLng: LatLng) {
            val geoUri =
                "http://maps.google.com/maps?q=loc:" + latLng.latitude.toString() + "," + latLng.longitude.toString() +
                        " (" + "TEST" + ")"
           startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(geoUri)))
    }

    private fun shareFourSquare() {
        val intent = Intent(Intent.ACTION_SEND)
        intent.type = "text/plain"
        intent.putExtra(Intent.EXTRA_SUBJECT, "Sharing FourSquare URL")
        intent.putExtra(Intent.EXTRA_TEXT, bottomSheetShareFourSquare.holdUrl)
        startActivity(Intent.createChooser(intent, "Share URL"))
    }
}