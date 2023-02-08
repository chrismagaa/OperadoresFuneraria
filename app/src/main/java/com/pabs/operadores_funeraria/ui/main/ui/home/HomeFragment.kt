package com.pabs.operadores_funeraria.ui.main.ui.home

 import android.net.Uri
 import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
 import android.widget.Button
 import android.widget.TextView
 import android.widget.Toast
 import androidx.constraintlayout.widget.ConstraintLayout
 import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
 import com.google.android.gms.maps.GoogleMap
 import com.google.android.gms.maps.OnMapReadyCallback
 import com.google.android.gms.maps.SupportMapFragment
 import com.google.android.material.bottomsheet.BottomSheetBehavior
 import com.pabs.operadores_funeraria.R
 import com.pabs.operadores_funeraria.databinding.FragmentHomeBinding


class HomeFragment : Fragment(), OnMapReadyCallback {


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
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}