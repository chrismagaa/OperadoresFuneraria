package com.pabs.operadores_funeraria.ui.main

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.*
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.airbnb.lottie.LottieAnimationView
import com.airbnb.lottie.LottieDrawable
import com.google.android.material.navigation.NavigationView
import com.google.android.material.snackbar.Snackbar
import com.pabs.operadores_funeraria.BuildConfig
import com.pabs.operadores_funeraria.R
import com.pabs.operadores_funeraria.data.network.model.MyLocationService
import com.pabs.operadores_funeraria.databinding.ActivityMainBinding
import com.pabs.operadores_funeraria.ui.login.LoginActivity
import com.pabs.operadores_funeraria.common.*
import com.pabs.operadores_funeraria.common.location.LocationService
import com.pabs.operadores_funeraria.common.location.LocationStateChangeBroadcastReceiver
import com.tinder.scarlet.Message
import com.tinder.scarlet.WebSocket
import io.reactivex.android.schedulers.AndroidSchedulers


class MainActivity : AppCompatActivity() {

    companion object {
        const val REQUEST_CODE_LOCATION = 102
        const val TAG = "MainActivity"

        const val EXTRA_LATITUDE = "EXTRA_LATITUDE"
        const val EXTRA_LONGITUDE = "EXTRA_LONGITUDE"
    }


    private var dialogGPS: AlertDialog? = null

    private lateinit var intentTracking: Intent

    var locationStateChangeBroadcastReceiver =
        LocationStateChangeBroadcastReceiver()

    private val changeLocationBroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: android.content.Context?, intent: Intent?) {
            val latitude = intent?.getStringExtra(EXTRA_LATITUDE).toString().toDouble()
            val longitude = intent?.getStringExtra(EXTRA_LONGITUDE).toString().toDouble()

            vmMain.setDistancia(latitude, longitude)


            val message = MyLocationService(
                latitude,
                longitude,
                vmMain.user.value!!.username,
                vmMain.user.value!!.autoPlaca
            )

            Log.d(TAG, MyLocationService.toJson(message))
            sendLocation(MyLocationService.toJson(message))
        }
    }

    private val internalLocationChangeReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val stateGPS = intent.getStringExtra("Gps_state")
            if (stateGPS == StatusGPSConnection.DISABLED.name) {
                vmMain.gpsEnabled.postValue(false)
            } else {
                vmMain.gpsEnabled.postValue(true)
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
                R.id.nav_servicio
            ), drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)



        vmMain.onCreate(this)

        navView.menu.findItem(R.id.nav_info).setOnMenuItemClickListener {
            drawerLayout.closeDrawer(GravityCompat.START)
            MessageFactory.getDialogInfo(this).show()
            true
        }

        navView.menu.findItem(R.id.nav_log_out).setOnMenuItemClickListener {
            drawerLayout.closeDrawer(GravityCompat.START)
            showDialogSalir()
            true
        }

        observerUser()

        observerServicio()

        observerGps()

        observerMessageDialog()


        //Registramos el broadcast receiver de los cambios en la ubicaci??n
        registerRecivers()




    }

    private fun observerMessageDialog() {
        vmMain.message.observe(this) {
            it?.let { message ->
                MessageFactory.getDialog(this, message.type,"Error", message.message,{}).show()
            }
        }
    }


    private fun observerGps() {
        //observamos gps habilitado
        vmMain.gpsEnabled.observe(this) {
            if (it) {
                changeStatusGps(StatusGPSConnection.ENABLED)
                //Comenzamos a obtener la ubicaci??n en tiempo real
                if (this.isPermissionsGranted()) {
                    vmMain.permissionEnabledLocation.postValue(true)
                    if (!this::intentTracking.isInitialized) {
                        initTracking()
                    }
                } else {
                    vmMain.permissionEnabledLocation.postValue(false)
                    requestLocationPermission(this)
                }
            } else {
                changeStatusGps(StatusGPSConnection.DISABLED)
                mostrarDialogoGPSDeshabilitado()
            }
        }

    }

    private fun mostrarDialogoGPSDeshabilitado() {
        if (dialogGPS == null || !dialogGPS!!.isShowing) {
            dialogGPS = MessageFactory.getDialog(
                this,
                MessageType.ERROR,
                "GPS DESHABILITADO",
                "Para usar esta aplicaci??n es necesario habilitar tu ubicaci??n",
                {
                    try {
                        startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
                    } catch (e: ActivityNotFoundException) {
                        startActivity(Intent(Settings.ACTION_SETTINGS))
                    }
                }).show()
        }
    }

    private fun observerServicio() {
        vmMain.servicio.observe(this) { servicio ->
            if (servicio != null) {
                if (BuildConfig.DEBUG) {
                    //show snack bar
                    Snackbar.make(
                        binding.root,
                        "Servicio actualizado ",
                        Snackbar.LENGTH_LONG
                    ).setBackgroundTint(ContextCompat.getColor(this, R.color.green)).show()
                }
            }else{
                Snackbar.make(
                    binding.root,
                    "No tienes un servicio asignado",
                    Snackbar.LENGTH_LONG
                ).setBackgroundTint(ContextCompat.getColor(this, R.color.blue_light)).show()
            }

        }
    }

    private fun observerUser() {
        vmMain.user.observe(this) {
            if (it != null) {
                binding.navView.getHeaderView(0).findViewById<TextView>(R.id.tvUserName).text =
                    it.username
                binding.navView.getHeaderView(0).findViewById<TextView>(R.id.tvAuto).text =
                    "Carroza: " + it.autoPlaca

                //TEST: wss://demo.piesocket.com/v3/channel_124?api_key=VCXCEuvhGcBDP7XhiJJUDvR1e1D3eiVjgZ9VRiaV&notify_self
                //Comenzamos la conexi??n con el websocket
                //Toast.makeText(this, "wss://demo.piesocket.com/v3/${it.channel_tracking}?api_key=VCXCEuvhGcBDP7XhiJJUDvR1e1D3eiVjgZ9VRiaV&notify_self", Toast.LENGTH_LONG).show()
                vmMain.setupWebSocketService("wss://demo.piesocket.com/v3/${it.channel_tracking}?api_key=VCXCEuvhGcBDP7XhiJJUDvR1e1D3eiVjgZ9VRiaV&notify_self")
                observeConnection()
            }
        }
    }

    private fun changeStatusGps(status: StatusGPSConnection) {
      //  findViewById<ImageView>(R.id.ivStatusGPS).background =
        //    ContextCompat.getDrawable(this, status.icon())

        val animationStatusGPS: LottieAnimationView = findViewById<LottieAnimationView>(R.id.animationStatusGPS)

            animationStatusGPS.apply {
                visibility = View.VISIBLE
                repeatMode = LottieDrawable.RESTART
                setAnimation(status.animation())
                playAnimation()
            }


    }


    private fun registerRecivers() {

        //Broadcast para el cambio de estado de la conexi??n
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

        val intentFilter = IntentFilter("changeLocationReciver")
        registerReceiver(changeLocationBroadcastReceiver, intentFilter)
    }

    fun requestLocationPermission(activity: Activity) {
        if (ActivityCompat.shouldShowRequestPermissionRationale(
                activity,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
        ) {
            Toast.makeText(activity, "Ve a ajustes y acepta los permisos", Toast.LENGTH_SHORT)
                .show()
        } else {
            ActivityCompat.requestPermissions(
                activity,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                REQUEST_CODE_LOCATION
            )
        }
    }

    private fun initTracking() {
        intentTracking = Intent(this, LocationService::class.java).apply {
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
        when (requestCode) {
            REQUEST_CODE_LOCATION -> if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                initTracking()
                vmMain.permissionEnabledLocation.postValue(true)
            } else {
                Toast.makeText(
                    this,
                    "Para activar la localizaci??n ve a ajustes y acepta los permisos",
                    Toast.LENGTH_LONG
                ).show()
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
            R.id.action_refresh -> {
                vmMain.refreshServicio(this)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
        return super.onOptionsItemSelected(item)

    }

    private fun showDialogSalir() {
        MessageFactory.getDialog(
            this,
            MessageType.INFO,
            "??Estas seguro de que quieres salir?",
            "Si continuas no podras realizar un servicio.",
            {
                vmMain.logout(this) {
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
            },
            "CONTINUAR"
        ).show()
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
            is WebSocket.Event.OnConnectionOpened<*> -> changeStatusWEB(StatusWebConnection.OPENED)
            is WebSocket.Event.OnConnectionClosed -> changeStatusWEB(StatusWebConnection.CLOSED)
            is WebSocket.Event.OnConnectionClosing -> changeStatusWEB(StatusWebConnection.CLOSING)
            is WebSocket.Event.OnConnectionFailed -> changeStatusWEB(StatusWebConnection.FAILED)
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


    private fun changeStatusWEB(statusWeb: StatusWebConnection) {
        if (BuildConfig.DEBUG) {
            Log.d(TAG, "changeStatusWEB${statusWeb.description()}")
            // Toast.makeText(this, statusWeb.name, Toast.LENGTH_SHORT).show()
        }
        //this.findViewById<ImageView>(R.id.ivStatusWEB).background = ContextCompat.getDrawable(this, statusWeb.icon())

        val animationStatusWEB: LottieAnimationView = findViewById<LottieAnimationView>(R.id.animationStatusWeb)
            animationStatusWEB.apply {
                visibility = View.VISIBLE
                repeatMode = LottieDrawable.RESTART
                setAnimation(statusWeb.animation())
                playAnimation()
            }
    }


    override fun onBackPressed() {
    }


}