package com.pabs.operadores_funeraria.ui.main.ui.servicio

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Intent
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
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
import com.pabs.operadores_funeraria.common.MessageFactory
import com.pabs.operadores_funeraria.common.MessageType
import com.pabs.operadores_funeraria.common.isPermissionsGranted


class ServicioFragment : Fragment(), OnMapReadyCallback, OnLocationChangedListener {

    companion object {
        const val REQUEST_CODE_LOCATION = 102
        const val TAG = "ServicioFragment"
    }


    private lateinit var tvClienteName: TextView
    private lateinit var tvPlanData: TextView
    private lateinit var tvTipoCuenta: TextView
    private lateinit var tvTelefono: TextView
    private lateinit var btnRoute: ConstraintLayout
    private lateinit var tvRoute: TextView
    private lateinit var ivIconRoute: ImageView
    private lateinit var tvTitle: TextView
    private lateinit var tvNoServicio: TextView
    private lateinit var tvAddress: TextView
    private lateinit var containerCuenta: ConstraintLayout
    private lateinit var containerTelefono: ConstraintLayout
    private lateinit var containerCliente: ConstraintLayout


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
        vmMain.servicio.observe(viewLifecycleOwner) { servicio ->
            // Toast.makeText(requireContext(), servicio.toString(), Toast.LENGTH_SHORT).show()
            if (servicio != null) {
                updateUI(servicio)
            } else {
                //No tiene un servicio
                updateUISinServicio()
            }
        }

        vmMain.isLoading.observe(viewLifecycleOwner) { isLoading ->
            if (isLoading != null) {
                if (isLoading) {
                    dialogLoading =
                        MessageFactory.getDialogLoading(requireContext(), "Espere porfavor...")
                            .show()
                } else {
                    if (dialogLoading != null && dialogLoading!!.isShowing) {
                        dialogLoading!!.dismiss()
                    }
                }
            }
        }

        vmMain.distancia.observe(viewLifecycleOwner) { distancia ->
            if (distancia != null) {
                // Toast.makeText(requireContext(), "Distancia: $distancia", Toast.LENGTH_SHORT).show()
                val btnIniciar = bottomSheet.findViewById<TextView>(R.id.ped_route_text)
                val ivIniciar = bottomSheet.findViewById<ImageView>(R.id.ped_route_iv)
                if (distancia < 0.500) {
                    btnIniciar.text = "Terminar Recolección"
                    ivIniciar.visibility = View.GONE
                } else {
                    btnIniciar.text = "Iniciar"
                    ivIniciar.visibility = View.VISIBLE
                }
            }
        }


        vmMain.finalizarReco.observe(viewLifecycleOwner) { finalizar ->
            if (finalizar != null) {
                if (finalizar.is_finalized) {
                    MessageFactory.getDialog(
                        requireContext(),
                        MessageType.SUCCESS,
                        "RECOLECCIÓN FINALIZADA",
                        finalizar.message, {}
                    ).show()
                } else {
                    MessageFactory.getDialog(
                        requireContext(),
                        MessageType.ERROR,
                        "RECOLECCIÓN NO FINALIZADA",
                        finalizar.message,
                        {}
                    ).show()
                }
            }
        }
    }

    private fun updateUISinServicio() {
        mMap.clear()
        showUIDataServicio(View.GONE)
        tvTitle.text = "Sin servicio"
        tvRoute.text = "Refrescar"
        tvAddress.text = ""
    }

    private fun showUIDataServicio(visibility: Int) {
            containerCliente.visibility = visibility
            containerTelefono.visibility = visibility
            containerCuenta.visibility = visibility
            tvNoServicio.visibility = visibility
            ivIconRoute.visibility = visibility
    }


    private fun setupBottomSheet() {
        bottomSheet = binding.root.findViewById<NestedScrollView>(R.id.bottomSheet)
        bsb = BottomSheetBehavior.from(bottomSheet)
        bsb.setPeekHeight(370, true)
        bsb.state = BottomSheetBehavior.STATE_EXPANDED
        bsb.isHideable = false


        containerCliente = bottomSheet.findViewById<ConstraintLayout>(R.id.constraintCliente)
        containerTelefono = bottomSheet.findViewById<ConstraintLayout>(R.id.constraintTelefono)
        containerCuenta = bottomSheet.findViewById<ConstraintLayout>(R.id.constraintCuenta)
        tvAddress = bottomSheet.findViewById<TextView>(R.id.tvAddress)
        tvNoServicio = bottomSheet.findViewById<TextView>(R.id.tvNoServicio)
        tvTitle = bottomSheet.findViewById<TextView>(R.id.tvTitle)
        ivIconRoute = bottomSheet.findViewById<ImageView>(R.id.ped_route_iv)
        tvRoute = bottomSheet.findViewById<TextView>(R.id.ped_route_text)
        btnRoute = bottomSheet.findViewById<ConstraintLayout>(R.id.btnRuta)
        tvTelefono = bottomSheet.findViewById<TextView>(R.id.tvTelefonoCliente)
        tvTipoCuenta = bottomSheet.findViewById<TextView>(R.id.tvTipoCuentaData)
        tvPlanData = bottomSheet.findViewById<TextView>(R.id.tvPlanData)
        tvClienteName = bottomSheet.findViewById<TextView>(R.id.tvClienteName)


        btnRoute.setOnClickListener {
            if (tvRoute.text == "Iniciar") {
                startNavigation()
            } else if(tvRoute.text == "Terminar Recolección"){
                showDialogFinalizarRecoleccion()
            }else if(tvRoute.text == "Refrescar"){
                vmMain.refreshServicio(requireContext())
            }

        }
        tvTelefono.setOnClickListener {
            val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:${servicio?.telefono}"))
            startActivity(intent)
        }
        /*

            bottomSheet.findViewById<ImageView>(R.id.btnInfo).setOnClickListener{
                showDialogInfo()
            }

         */
    }

    private fun showDialogFinalizarRecoleccion() {
        MessageFactory.getDialogFinalizarReco(requireContext()) { code ->
            vmMain.finalizarRecoleccion(code)
        }.show()
    }

    private fun updateUI(servicio: ServicioFuneral) {
        showUIDataServicio(View.VISIBLE)
        tvTitle.text = servicio.reco_name
        tvAddress.text = servicio.reco_address
        tvTelefono.text = servicio.telefono

        tvTipoCuenta.text = servicio.tipo_cliente
        tvNoServicio.text = "No Servicio: ${servicio.id_servicio}"
        tvClienteName.text = servicio.cliente
        tvPlanData.text = servicio.plan

        if (servicio.reco_lat != null && servicio.reco_lng != null) {
            val latLng = LatLng(servicio.reco_lat, servicio.reco_lng)
            mMap.addMarker(
                MarkerOptions()
                    .position(latLng)
                    .title(servicio.reco_name)
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.house))
            )

            if (::mMap.isInitialized) {
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f))
            }
        }
    }

    private fun showDialogInfo() {
        val info = "Destino name: ${vmMain.servicio.value?.reco_name} \n " +
                "Destino lat: ${vmMain.servicio.value?.reco_lat} \n " +
                "Destino lng: ${vmMain.servicio.value?.reco_lng} \n "
        MessageFactory.getDialog(
            requireContext(),
            MessageType.INFO,
            "Información del servicio",
            info,
            {},
            "OK"
        ).show()
    }

    private fun startNavigation() {
        vmMain.servicio.value?.let { servicio ->

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


        if (vmMain.servicio.value != null) {
            mMap.moveCamera(
                CameraUpdateFactory.newLatLngZoom(
                    LatLng(
                        vmMain.servicio.value!!.reco_lat!!,
                        vmMain.servicio.value!!.reco_lng!!
                    ), 15f
                )
            )
        } else {
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
        Toast.makeText(
            requireContext(),
            "Latitud: ${p0.latitude} Longitud: ${p0.longitude}",
            Toast.LENGTH_SHORT
        ).show()
        val latLng = LatLng(p0.latitude, p0.longitude)
        val markerCarroza = MarkerOptions()
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