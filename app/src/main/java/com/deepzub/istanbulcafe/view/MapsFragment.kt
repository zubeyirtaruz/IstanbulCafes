package com.deepzub.istanbulcafe.view

import androidx.fragment.app.Fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.deepzub.istanbulcafe.R

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class MapsFragment : Fragment() {

    private var cafeName = ""
    private var lat = 0.0f
    private var lng = 0.0f

    private val callback = OnMapReadyCallback { googleMap ->
        val cafe = LatLng(lat.toDouble(), lng.toDouble())
        googleMap.addMarker(MarkerOptions().position(cafe).title(cafeName))
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(cafe,17f))
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.fragment_maps, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        arguments?.let {
            cafeName = MapsFragmentArgs.fromBundle(it).cafeName
            lat = MapsFragmentArgs.fromBundle(it).lat
            lng = MapsFragmentArgs.fromBundle(it).lng
        }



        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(callback)
    }


}