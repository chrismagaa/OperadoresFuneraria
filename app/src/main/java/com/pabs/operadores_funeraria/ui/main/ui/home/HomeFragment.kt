package com.pabs.operadores_funeraria.ui.main.ui.home

 import android.Manifest
 import android.annotation.SuppressLint
 import android.app.Activity
 import android.content.Intent
 import android.content.pm.PackageManager
 import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
 import android.widget.Button
 import android.widget.Toast
 import androidx.constraintlayout.widget.ConstraintLayout
 import androidx.core.app.ActivityCompat
 import androidx.fragment.app.Fragment
 import com.google.android.gms.maps.GoogleMap
 import com.google.android.gms.maps.OnMapReadyCallback
 import com.google.android.gms.maps.SupportMapFragment
 import com.google.android.material.bottomsheet.BottomSheetBehavior
 import com.pabs.operadores_funeraria.R
 import com.pabs.operadores_funeraria.databinding.FragmentHomeBinding
 import com.pabs.operadores_funeraria.utils.LocationService
 import com.pabs.operadores_funeraria.utils.isPermissionsGranted


class HomeFragment : Fragment(), OnMapReadyCallback {

    companion object{
        const val REQUEST_CODE_LOCATION = 102
        const val TAG = "HomeFragment"
    }



    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var mMap: GoogleMap
    lateinit var bsb: BottomSheetBehavior<ConstraintLayout>

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

    private fun setupBottomSheet() {
        binding.root.findViewById<ConstraintLayout>(R.id.bottomSheet)
        var bottomSheet = binding.root.findViewById<ConstraintLayout>(R.id.bottomSheet)
        bsb = BottomSheetBehavior.from(bottomSheet)
        bsb.state = BottomSheetBehavior.STATE_EXPANDED
        bsb.isHideable = false

        bottomSheet.findViewById<Button>(R.id.btnMaps).setOnClickListener {
            Toast.makeText(requireContext(), "Ir a mapas", Toast.LENGTH_SHORT).show()
        }
    }


    private fun createFragmentMap() {
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }




    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        enableMyLocation()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


    @SuppressLint("MissingPermission")
    private fun enableMyLocation(){
        if(!::mMap.isInitialized) return
        if(requireContext().isPermissionsGranted()){
            mMap.isMyLocationEnabled = true
            Intent(requireContext(), LocationService::class.java).apply {
                action = LocationService.ACTION_START
                requireContext().startService(this)
            }
        }else{
            requestLocationPermission(requireActivity())
        }
    }

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

    @SuppressLint("MissingPermission")
    override fun onResume() {
        super.onResume()
        if (!::mMap.isInitialized) return
        if(!requireContext().isPermissionsGranted()){
            mMap.isMyLocationEnabled = false
            Toast.makeText(requireContext(), "Para activar la localización ve a ajustes y acepta los permisos", Toast.LENGTH_LONG).show()
        }
    }



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



}