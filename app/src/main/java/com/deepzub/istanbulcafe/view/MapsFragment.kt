package com.deepzub.istanbulcafe.view

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import androidx.fragment.app.Fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.deepzub.istanbulcafe.R

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import java.util.*

class MapsFragment : Fragment() {

    private lateinit var locationManager: LocationManager
    private lateinit var locationListener: LocationListener

    private var cafeName = ""
    private var cafeAdress = ""

    @SuppressLint("MissingPermission")
    private val callback = OnMapReadyCallback { googleMap ->

        val geocoder = Geocoder(requireContext(), Locale.getDefault())

        try{
            val adress = geocoder.getFromLocationName(cafeAdress,1)
            val cafe = LatLng(adress.get(0).latitude, adress.get(0).longitude)
            googleMap.addMarker(MarkerOptions().position(cafe).title(cafeName))
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(cafe,15f))

        }catch (e : Exception){
            e.printStackTrace()
        }

            locationManager = activity?.getSystemService(Context.LOCATION_SERVICE) as LocationManager

            locationListener = object : LocationListener {
            override fun onLocationChanged(p0: Location) {

                val userLocation = LatLng(p0.latitude, p0.longitude)
                googleMap.addMarker(MarkerOptions().position(userLocation).title("Güncel Konum"))


            }
        }
        if(ContextCompat.checkSelfPermission(requireContext(),Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(requireActivity(), arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),1)
        }else{
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,1,1f,locationListener)
        }
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
            cafeAdress = MapsFragmentArgs.fromBundle(it).cafeAdress
        }

        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(callback)

    }


}