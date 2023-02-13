package com.pabs.operadores_funeraria.ui.main

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.navigation.NavigationView
import com.google.android.material.snackbar.Snackbar
import com.pabs.operadores_funeraria.BuildConfig
import com.pabs.operadores_funeraria.R
import com.pabs.operadores_funeraria.data.network.model.MyLocationService
import com.pabs.operadores_funeraria.databinding.ActivityMainBinding
import com.pabs.operadores_funeraria.ui.login.LoginActivity
import com.pabs.operadores_funeraria.utils.StatusWebConnection
import com.pabs.operadores_funeraria.utils.location.LocationService
import com.pabs.operadores_funeraria.utils.location.LocationStateChangeBroadcastReceiver
import com.pabs.operadores_funeraria.utils.isPermissionsGranted
import com.tinder.scarlet.Message
import com.tinder.scarlet.WebSocket
import io.reactivex.android.schedulers.AndroidSchedulers


class MainActivity : AppCompatActivity() {

    companion object{
        const val REQUEST_CODE_LOCATION = 102
        const val TAG = "MainActivity"

        const val EXTRA_LATITUDE = "EXTRA_LATITUDE"
        const val EXTRA_LONGITUDE = "EXTRA_LONGITUDE"
    }


    var locationStateChangeBroadcastReceiver =
        LocationStateChangeBroadcastReceiver()

    private val changeLocationBroadcastReceiver = object : BroadcastReceiver(){
        override fun onReceive(context: android.content.Context?, intent: Intent?) {
            val latitude = intent?.getStringExtra(EXTRA_LATITUDE).toString().toDouble()
            val longitude = intent?.getStringExtra(EXTRA_LONGITUDE).toString().toDouble()

            val message = MyLocationService("JAD-232", "Juan Perez",latitude, longitude)


            Log.d(TAG, MyLocationService.toJson(message))
            sendLocation(MyLocationService.toJson(message))
        }
    }

    private val internalLocationChangeReceiver = object : BroadcastReceiver(){
        override fun onReceive(context: Context, intent: Intent) {
            val stateGPS = intent.getStringExtra("Gps_state")
            if (stateGPS == "Gps Disabled") {
                changeStatusTitleGps("GPS Deshabilitado", R.color.red)
                Toast.makeText(context, "GPS Habilitado", Toast.LENGTH_SHORT).show()
            } else {
                changeStatusTitleGps("GPS Habilitado", R.color.green)
                Toast.makeText(context, "GPS Desabilidado", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding

    private val vmMain: MainViewModel by viewModels()

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.appBarMain.toolbar)

        val drawerLayout: DrawerLayout = binding.drawerLayout
        val navView: NavigationView = binding.navView
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_home, R.id.nav_gallery, R.id.nav_slideshow
            ), drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)


        vmMain.user.observe(   this) {
            if(BuildConfig.DEBUG){
                Toast.makeText(this, "URL: ${it.servicio.url}", Toast.LENGTH_SHORT).show()
                Log.d(TAG, "URL: ${it.servicio.url}")
            }
            if(it != null){
                binding.navView.getHeaderView(0).findViewById<TextView>(R.id.tvUserName).text = it.username
                binding.navView.getHeaderView(0).findViewById<TextView>(R.id.tvAuto).text = "Carroza: "+it.autoPlaca
                //TEST: wss://demo.piesocket.com/v3/channel_124?api_key=VCXCEuvhGcBDP7XhiJJUDvR1e1D3eiVjgZ9VRiaV&notify_self
                //Comenzamos la conexión con el websocket
                        vmMain.setupWebSocketService(it.servicio.url!!)
                        observeConnection()
            }
        }

        //checamos la localización
        checkLocation()

        //Registramos el broadcast receiver de los cambios en la ubicación
        registerRecivers()

        //Comenzamos a obtener la ubicación en tiempo real
        if(this.isPermissionsGranted()){
            vmMain.permissionEnabledLocation.postValue(true)
            initTracking()
        }else{
            vmMain.permissionEnabledLocation.postValue(false)
            requestLocationPermission(this)
        }
    }

    private fun checkLocation() {
        val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            changeStatusTitleGps("GPS Deshabilitado", R.color.red)
        } else {
            changeStatusTitleGps("GPS Habilitado", R.color.green)
        }
    }

    private fun changeStatusTitleGps(title: String, color: Int) {
        findViewById<TextView>(R.id.tv_status_gps).apply {
            text = title
            setBackgroundColor(ContextCompat.getColor(this@MainActivity, color))
        }
    }


    private fun registerRecivers() {
        val intentFilter = IntentFilter("changeLocationReciver")
        registerReceiver(changeLocationBroadcastReceiver, intentFilter)


        //Broadcast para el cambio de estado de la conexión
        try {
            registerReceiver(
                locationStateChangeBroadcastReceiver,
                IntentFilter("android.location.PROVIDERS_CHANGED")
            )
            val intentFilter = IntentFilter()
            intentFilter.addAction(LocationStateChangeBroadcastReceiver.GPS_CHANGE_ACTION)
            registerReceiver(internalLocationChangeReceiver, intentFilter)
        } catch (ex: Exception) {
            ex.printStackTrace()
        }



    }

    fun requestLocationPermission(activity: Activity) {
        if (ActivityCompat.shouldShowRequestPermissionRationale(activity,
                Manifest.permission.ACCESS_FINE_LOCATION)) {
            Toast.makeText(activity, "Ve a ajustes y acepta los permisos", Toast.LENGTH_SHORT).show()
        } else {
            ActivityCompat.requestPermissions(activity,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                REQUEST_CODE_LOCATION
            )
        }
    }

    private fun initTracking() {
        Intent(this, LocationService::class.java).apply {
            action = LocationService.ACTION_START
            startService(this)
        }
    }

    @SuppressLint("MissingPermission")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when(requestCode){
            REQUEST_CODE_LOCATION -> if(grantResults.isNotEmpty() && grantResults[0]== PackageManager.PERMISSION_GRANTED){
                initTracking()
                vmMain.permissionEnabledLocation.postValue(true)
            }else{
                Toast.makeText(this, "Para activar la localización ve a ajustes y acepta los permisos", Toast.LENGTH_LONG).show()
            }
            else -> {}
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_salir -> {
                vmMain.logout(this){
                    unregisterReceiver(changeLocationBroadcastReceiver)
                    Intent(this, LocationService::class.java).apply {
                        action = LocationService.ACTION_START
                        stopService(this)
                    }

                    unregisterReceiver(locationStateChangeBroadcastReceiver);
                    unregisterReceiver(internalLocationChangeReceiver);

                    startActivity(Intent(this, LoginActivity::class.java))
                    finish()
                }

                true
            }
            else -> super.onOptionsItemSelected(item)
        }
        return super.onOptionsItemSelected(item)

    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    fun sendLocation(message: String) {
        vmMain.webSocketService?.sendMessage(message)
    }

    @SuppressLint("CheckResult")
    private fun observeConnection() {
        vmMain.webSocketService?.observeConnection()
            ?.observeOn(AndroidSchedulers.mainThread())
            ?.subscribe({ response ->
                Log.d("observeConnection", response.toString())
                onReceiveResponseConnection(response)
            }, { error ->
                Log.e("observeConnection", error.message.orEmpty())
                Snackbar.make(binding.root, error.message.orEmpty(), Snackbar.LENGTH_SHORT).show()
            })
    }


    private fun onReceiveResponseConnection(response: WebSocket.Event) {
        when (response) {
            is WebSocket.Event.OnConnectionOpened<*> -> changeStatusTitle(StatusWebConnection.OPENED)
            is WebSocket.Event.OnConnectionClosed -> changeStatusTitle(StatusWebConnection.CLOSED)
            is WebSocket.Event.OnConnectionClosing -> changeStatusTitle(StatusWebConnection.CLOSING)
            is WebSocket.Event.OnConnectionFailed -> changeStatusTitle(StatusWebConnection.FAILED)
            is WebSocket.Event.OnMessageReceived -> handleOnMessageReceived(response.message)
        }
    }

    @SuppressLint("SetTextI18n")
    private fun handleOnMessageReceived(message: Message) {
        //   binding.textHome.text = message.toValue()
       // Toast.makeText(this, message.toValue(), Toast.LENGTH_SHORT).show()
    }

    private fun Message.toValue(): String {
        return when (this) {
            is Message.Text -> value
            is Message.Bytes -> value.toString()
        }
    }

    private fun changeStatusTitle(statusWeb: StatusWebConnection) {
        Toast.makeText(this, title, Toast.LENGTH_SHORT).show()
           this.findViewById<TextView>(R.id.tv_status_service).apply {
               text = statusWeb.description()
               setBackgroundColor(ContextCompat.getColor(context, statusWeb.color()))
           }
    }



    override fun onBackPressed() {
    }





}