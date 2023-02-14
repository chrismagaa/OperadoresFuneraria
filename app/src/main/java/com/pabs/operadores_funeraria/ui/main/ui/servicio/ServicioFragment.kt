package com.pabs.operadores_funeraria.ui.main.ui.servicio

import android.annotation.SuppressLint
import android.content.Intent
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.os.Message
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
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
import com.pabs.operadores_funeraria.databinding.FragmentServicioBinding
import com.pabs.operadores_funeraria.ui.main.MainViewModel
import com.pabs.operadores_funeraria.utils.MessageFactory
import com.pabs.operadores_funeraria.utils.MessageType
import com.pabs.operadores_funeraria.utils.isPermissionsGranted


class ServicioFragment : Fragment(), OnMapReadyCallback, OnLocationChangedListener {

    companion object {
        const val REQUEST_CODE_LOCATION = 102
        const val TAG = "ServicioFragment"
    }

    private var _binding: FragmentServicioBinding? = null
    private val binding get() = _binding!!
    private lateinit var mMap: GoogleMap
    lateinit var bsb: BottomSheetBehavior<ConstraintLayout>

    private val vmMain: MainViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentServicioBinding.inflate(inflater, container, false)

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
        vmMain.servicio.observe(viewLifecycleOwner) {servicio ->
            if (servicio != null) {
                if (servicio.destino_lat != null && servicio.destino_lng != null) {
                    val latLng = LatLng(servicio.destino_lat, servicio.destino_lng)
                    mMap.addMarker(
                        MarkerOptions()
                            .position(latLng)
                            .title(servicio.destino_name)
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
            vmMain.servicio.value?.let {servicio ->
                if (servicio.destino_lat != null && servicio.destino_lng != null) {
                    val latLng = LatLng(servicio.destino_lat, servicio.destino_lng)
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f))
                }
            }
        }

        bottomSheet.findViewById<ImageView>(R.id.btnInfo).setOnClickListener{
            showDialogInfo()
        }
    }

    private fun showDialogInfo() {
        val info = "Destino name: ${vmMain.servicio.value?.destino_name} \n " +
                "Destino lat: ${vmMain.servicio.value?.destino_lat} \n " +
                "Destino lng: ${vmMain.servicio.value?.destino_lng} \n " +
                "URL: ${vmMain.servicio.value?.url} "
        MessageFactory.getDialog(requireContext(), MessageType.INFO, "Información del servicio", info, {}, "OK").show()
    }

    private fun startNavigation() {
        vmMain.servicio.value?.let {servicio ->

            if (servicio.destino_lat != null && servicio.destino_lng != null) {

                val gmmIntentUri =
                    Uri.parse("google.navigation:q=${servicio.destino_lat},${servicio.destino_lng}")
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