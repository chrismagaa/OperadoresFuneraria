package com.pabs.operadores_funeraria.ui.main.ui.home

import android.annotation.SuppressLint
import android.content.Intent
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.LocationSource.OnLocationChangedListener
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.pabs.operadores_funeraria.R
import com.pabs.operadores_funeraria.databinding.FragmentHomeBinding
import com.pabs.operadores_funeraria.ui.main.MainViewModel
import com.pabs.operadores_funeraria.utils.isPermissionsGranted
import com.pabs.operadores_funeraria.utils.location.DefaultLocationClient
import com.pabs.operadores_funeraria.utils.location.LocationClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach


class HomeFragment : Fragment(), OnMapReadyCallback, OnLocationChangedListener {

    companion object {
        const val REQUEST_CODE_LOCATION = 102
        const val TAG = "HomeFragment"
    }

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var mMap: GoogleMap
    lateinit var bsb: BottomSheetBehavior<ConstraintLayout>

    private val vmMain: MainViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)

        createFragmentMap()
        setupBottomSheet()

        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        vmMain.permissionEnabledLocation.observe(viewLifecycleOwner) {
            if (it) {
                //  requestLocationPermission(requireActivity())
                enableMyLocation()
            }
        }

        //pintar destino
        vmMain.user.observe(viewLifecycleOwner) {
            if (it != null) {
                if (it.servicio.destino_lat != null && it.servicio.destino_lng != null) {
                    val latLng = LatLng(it.servicio.destino_lat, it.servicio.destino_lng)
                    mMap.addMarker(
                        MarkerOptions()
                            .position(latLng)
                            .title(it.servicio.destino_name)
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.house))
                    )
                }
            }
        }


    }


    private fun setupBottomSheet() {
        binding.root.findViewById<ConstraintLayout>(R.id.bottomSheet)
        var bottomSheet = binding.root.findViewById<ConstraintLayout>(R.id.bottomSheet)
        bsb = BottomSheetBehavior.from(bottomSheet)
        bsb.state = BottomSheetBehavior.STATE_EXPANDED
        bsb.isHideable = false

        bottomSheet.findViewById<Button>(R.id.btnMaps).setOnClickListener {
            startNavigation()
        }

        bottomSheet.findViewById<Button>(R.id.btnDestino).setOnClickListener {
            vmMain.user.value?.let {
                if (it.servicio.destino_lat != null && it.servicio.destino_lng != null) {
                    val latLng = LatLng(it.servicio.destino_lat, it.servicio.destino_lng)
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f))
                }
            }
        }
    }

    private fun startNavigation() {
        vmMain.user.value?.let {

            if (it.servicio.destino_lat != null && it.servicio.destino_lng != null) {

                val gmmIntentUri =
                    Uri.parse("google.navigation:q=${it.servicio.destino_lat},${it.servicio.destino_lng}")
                val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
                mapIntent.setPackage("com.google.android.apps.maps")
                //  try {
                startActivity(mapIntent)
                //  } catch (ex: android.content.ActivityNotFoundException) {
                //    Toast.makeText(requireContext(), "No tienes instalado Google Maps", Toast.LENGTH_SHORT).show()
                // }
            }
        }

    }


    private fun createFragmentMap() {
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }


    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap


        //move camera position on mexico
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(19.432608, -99.133209), 5f))
        enableMyLocation()

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


    @SuppressLint("MissingPermission")
    private fun enableMyLocation() {
        if (!::mMap.isInitialized) return
        if (requireContext().isPermissionsGranted()) {
            mMap.isMyLocationEnabled = true

        } else {
            //  requestLocationPermission(requireActivity())
        }
    }

    override fun onLocationChanged(p0: Location) {
        //add the same marker
        Toast.makeText(requireContext(), "Latitud: ${p0.latitude} Longitud: ${p0.longitude}", Toast.LENGTH_SHORT).show()
        val latLng = LatLng(p0.latitude, p0.longitude)
        val markerCarroza =  MarkerOptions()
            .position(latLng)
            .title("Carroza")
            .icon(BitmapDescriptorFactory.fromResource(R.drawable.funebre))

        mMap.addMarker(markerCarroza)
    }

/*
    @SuppressLint("MissingPermission")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when(requestCode){
            REQUEST_CODE_LOCATION -> if(grantResults.isNotEmpty() && grantResults[0]== PackageManager.PERMISSION_GRANTED){
                mMap.isMyLocationEnabled = true
            }else{
                Toast.makeText(requireContext(), "Para activar la localización ve a ajustes y acepta los permisos", Toast.LENGTH_LONG).show()
            }
            else -> {}
        }
    }


 */
/*    @SuppressLint("MissingPermission")
    override fun onResume() {
        super.onResume()
        if (!::mMap.isInitialized) return
        if(!requireContext().isPermissionsGranted()){
            mMap.isMyLocationEnabled = false
            Toast.makeText(requireContext(), "Para activar la localización ve a ajustes y acepta los permisos", Toast.LENGTH_LONG).show()
        }
    }

 */

/*

    fun requestLocationPermission(activity: Activity) {
        if (ActivityCompat.shouldShowRequestPermissionRationale(activity,
                Manifest.permission.ACCESS_FINE_LOCATION)) {
            Toast.makeText(activity, "Ve a ajustes y acepta los permisos", Toast.LENGTH_SHORT).show()
        } else {
            ActivityCompat.requestPermissions(activity,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                REQUEST_CODE_LOCATION)
        }
    }

 */


}