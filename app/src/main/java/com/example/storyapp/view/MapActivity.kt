package com.example.storyapp.view

import android.content.res.Resources
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.storyapp.R
import com.example.storyapp.data.Result
import com.example.storyapp.custom_view.CustomDialog
import com.example.storyapp.databinding.ActivityMapBinding
import com.example.storyapp.model_responses.Story
import com.example.storyapp.viewmodel.MapViewModel
import com.example.storyapp.viewmodel.ViewModelFactory
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.MarkerOptions


class MapActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var binding: ActivityMapBinding
    private lateinit var factory: ViewModelFactory
    private val mapViewModel: MapViewModel by viewModels { factory }
    private val boundsBuilder = LatLngBounds.Builder()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupViewModel()

        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        googleMap.uiSettings.isZoomControlsEnabled = true
        googleMap.uiSettings.isIndoorLevelPickerEnabled = true
        googleMap.uiSettings.isCompassEnabled = true
        googleMap.uiSettings.isMapToolbarEnabled = true

        val dummyLocation = LatLng(-7.4283079, 109.3390984)
        googleMap.addMarker(
            MarkerOptions()
                .position(dummyLocation)
                .title("Wisma Damai")
        )
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(dummyLocation, 15f))

        setMapStyle(googleMap)
        getStoryWithLocation(googleMap)
    }

    private fun setupViewModel() {
        factory = ViewModelFactory.getInstance(this)
    }

    private fun getStoryWithLocation(googleMap: GoogleMap) {
        mapViewModel.getStories().observe(this) { result ->
            if (result != null) {
                when(result) {
                    is Result.Loading -> {
                        loadingHandler(true)
                    }
                    is Result.Eror -> {
                        loadingHandler(false)
                        errorHandler()
                    }
                    is Result.Berhasil -> {
                        loadingHandler(false)
                        showMarker(result.data.listStory, googleMap)
                    }
                }
            }
        }
    }

    private fun showMarker(listStory: List<Story>, googleMap: GoogleMap) {
        listStory.forEach { story ->
            val latLng = LatLng(story.lat, story.lon)
            googleMap.addMarker(
                MarkerOptions()
                    .position(latLng)
                    .title(story.createdAt)
                    .snippet(StringBuilder("created: ")
                        .append(story.createdAt.subSequence(11, 16).toString())
                        .toString()
                    )
            )
            boundsBuilder.include(latLng)
        }
    }

    private fun errorHandler() {
        CustomDialog(this, R.string.pesan_error).show()
    }

    private fun setMapStyle(googleMap: GoogleMap) {
        try {
            val success =
                googleMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.map_style))
            if (!success) {
                CustomDialog(this, R.string.pesan_error).show()
            }
        } catch (exception: Resources.NotFoundException) {
            CustomDialog(this, R.string.tidak_ditemukan).show()
        }
    }

    private fun loadingHandler(isLoading: Boolean) {
        if (isLoading) {
            binding.progressBarMap.visibility = View.VISIBLE
            binding.root.visibility = View.INVISIBLE
        } else {
            binding.progressBarMap.visibility = View.INVISIBLE
            binding.root.visibility = View.VISIBLE
        }
    }
}
