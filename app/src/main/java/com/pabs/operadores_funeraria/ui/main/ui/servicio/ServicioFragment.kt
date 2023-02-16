package com.pabs.operadores_funeraria.ui.main.ui.servicio

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Intent
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.text.util.Linkify
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.widget.NestedScrollView
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
import com.pabs.operadores_funeraria.data.network.model.ServicioFuneral
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

    private var dialogLoading: AlertDialog? = null
    private lateinit var bottomSheet: NestedScrollView
    private var _binding: FragmentServicioBinding? = null
    private val binding get() = _binding!!
    private lateinit var mMap: GoogleMap
    lateinit var bsb: BottomSheetBehavior<NestedScrollView>

    private val vmMain: MainViewModel by activityViewModels()

    private var servicio: ServicioFuneral? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentServicioBinding.inflate(inflater, container, false)

        createFragmentMap()
        setupBottomSheet()
       // vmMain.servicio.value?.let { updateUI(it) }


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
                updateUI(servicio)
            }
        }

        vmMain.isLoading.observe(viewLifecycleOwner) { isLoading ->
            if (isLoading != null) {
                if(isLoading){
                    dialogLoading = MessageFactory.getDialogLoading(requireContext(), "Cargando servicio...").show()
                }else{
                    if(dialogLoading!= null && dialogLoading!!.isShowing){
                        dialogLoading!!.dismiss()
                    }
                }
            }
        }



    }



    private fun setupBottomSheet() {
        bottomSheet = binding.root.findViewById<NestedScrollView>(R.id.bottomSheet)
        bsb = BottomSheetBehavior.from(bottomSheet)
        bsb.setPeekHeight(370, true)
        bsb.state = BottomSheetBehavior.STATE_EXPANDED
        bsb.isHideable = false

        bottomSheet.findViewById<ConstraintLayout>(R.id.btnRuta).setOnClickListener {
            startNavigation()
        }
    /*

        bottomSheet.findViewById<ImageView>(R.id.btnInfo).setOnClickListener{
            showDialogInfo()
        }

     */
    }

    private fun updateUI(servicio: ServicioFuneral) {
        bottomSheet.findViewById<TextView>(R.id.tvTitle).text = servicio.reco_name
        bottomSheet.findViewById<TextView>(R.id.tvAddress).text = servicio.reco_address
        val tvTelefono = bottomSheet.findViewById<TextView>(R.id.tvTelefonoCliente)
        tvTelefono.text = servicio.telefono
        tvTelefono.setOnClickListener {
            val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:${servicio.telefono}"))
            startActivity(intent)
        }
        bottomSheet.findViewById<TextView>(R.id.tvTipoCuentaData).text = servicio.tipo_cliente
        bottomSheet.findViewById<TextView>(R.id.tvNoServicio).text = "No Servicio: ${ servicio.servicio }"
        bottomSheet.findViewById<TextView>(R.id.tvPlanData).text = servicio.plan
        bottomSheet.findViewById<TextView>(R.id.tvClienteName).text = servicio.cliente

        if (servicio.reco_lat != null && servicio.reco_lng != null) {
            val latLng = LatLng(servicio.reco_lat, servicio.reco_lng)
            mMap.addMarker(
                MarkerOptions()
                    .position(latLng)
                    .title(servicio.reco_name)
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.house))
            )

            if(::mMap.isInitialized){
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f))
            }
        }
    }

    private fun showDialogInfo() {
        val info = "Destino name: ${vmMain.servicio.value?.reco_name} \n " +
                "Destino lat: ${vmMain.servicio.value?.reco_lat} \n " +
                "Destino lng: ${vmMain.servicio.value?.reco_lng} \n " +
                "URL: ${vmMain.servicio.value?.url} "
        MessageFactory.getDialog(requireContext(), MessageType.INFO, "Información del servicio", info, {}, "OK").show()
    }

    private fun startNavigation() {
        vmMain.servicio.value?.let {servicio ->

            if (servicio.reco_lat != null && servicio.reco_lng != null) {

                val gmmIntentUri =
                    Uri.parse("google.navigation:q=${servicio.reco_lat},${servicio.reco_lng}")
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


        if(vmMain.servicio.value!=null){
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(vmMain.servicio.value!!.reco_lat!!, vmMain.servicio.value!!.reco_lng!!), 15f))
        }else{
            //move camera position on mexico
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(19.432608, -99.133209), 5f))
        }
        enableMyLocation()

        mMap.setOnMapClickListener {
            //Hide Dialog
            bsb.state = BottomSheetBehavior.STATE_COLLAPSED
        }
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