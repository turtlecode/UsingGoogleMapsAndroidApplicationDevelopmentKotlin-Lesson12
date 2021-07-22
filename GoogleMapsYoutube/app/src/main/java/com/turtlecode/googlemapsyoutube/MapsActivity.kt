package com.turtlecode.googlemapsyoutube

import android.content.Context
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.turtlecode.googlemapsyoutube.databinding.ActivityMapsBinding
import java.lang.Exception
import java.util.*
import java.util.jar.Manifest

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding

    private lateinit var locationManager: LocationManager
    private lateinit var locationListener: LocationListener


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.setOnMapClickListener(listener)
        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        locationListener = object : LocationListener {
            override fun onLocationChanged(location: Location) {
                mMap.clear()
                val current_location = LatLng(location.latitude, location.longitude)
                mMap.addMarker(MarkerOptions().position(current_location).title("You are here"))
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(current_location,15f))
                val geocoder = Geocoder(this@MapsActivity, Locale.getDefault())
                try {
                    val adress_list = geocoder.getFromLocation(location.latitude, location.longitude,1)
                    if (adress_list.size > 0) {
                        println(adress_list.get(0).toString())
                    }
                } catch (e:Exception) {
                    e.printStackTrace()
                }
            }
        }
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),1)
        } else {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,1,1f,locationListener)
            val lastknownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
            if (lastknownLocation != null) {
                val last_knowLatlng = LatLng(lastknownLocation.latitude, lastknownLocation.longitude)
                mMap.addMarker(MarkerOptions().position(last_knowLatlng).title("Last Known Location"))
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(last_knowLatlng,15f))
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == 1) {
            if(grantResults.size < 1) {
                if(ContextCompat.checkSelfPermission(this,android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,1,1f,locationListener)
                }
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }
    val listener = object : GoogleMap.OnMapClickListener {
        override fun onMapClick(p0: LatLng) {
            mMap.clear()
            val geocoder = Geocoder(this@MapsActivity, Locale.getDefault())
            if (p0 != null) {
                var adress = ""
                try {
                    val adressList = geocoder.getFromLocation(p0.latitude, p0.longitude,1)
                    if (adressList.size > 0 ) {
                        if (adressList.get(0).thoroughfare != null) {
                            adress += adressList.get(0).thoroughfare
                            if (adressList.get(0).subThoroughfare != null) {
                                adress += adressList.get(0).subThoroughfare
                            }
                        }
                    }
                } catch (e:Exception){
                    e.printStackTrace()
                }
                mMap.addMarker(MarkerOptions().position(p0).title(adress))
            }
        }
    }
}






