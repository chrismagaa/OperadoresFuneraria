package com.pabs.operadores_funeraria.ui.main

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.navigation.NavigationView
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.drawerlayout.widget.DrawerLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.pabs.operadores_funeraria.R
import com.pabs.operadores_funeraria.core.ScarletHelper
import com.pabs.operadores_funeraria.data.network.EchoService
import com.pabs.operadores_funeraria.databinding.ActivityMainBinding
import com.tinder.scarlet.Lifecycle
import com.tinder.scarlet.Message
import com.tinder.scarlet.Scarlet
import com.tinder.scarlet.WebSocket
import com.tinder.scarlet.lifecycle.android.AndroidLifecycle
import com.tinder.scarlet.streamadapter.rxjava2.RxJava2StreamAdapterFactory
import io.reactivex.android.schedulers.AndroidSchedulers

class MainActivity : AppCompatActivity() {

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


        vmMain.user.observe(this) {
            if(it != null){
                binding.navView.getHeaderView(0).findViewById<TextView>(R.id.tvUserName).text = it.username
                binding.navView.getHeaderView(0).findViewById<TextView>(R.id.tvAuto).text = "Placas Carroza: "+it.autoPlaca
            }
        }
        vmMain.setupWebSocketService(provideLifeCycle(), "wss://socketsbay.com/wss/v2/1/demo/")
        observeConnection()

    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    private fun provideLifeCycle() = AndroidLifecycle.ofLifecycleOwnerForeground(application, this)


    private fun sendMessage(message: String) {
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
            is WebSocket.Event.OnConnectionOpened<*> -> changeStatusTitle("CONECTADO", R.color.green)
            is WebSocket.Event.OnConnectionClosed -> changeStatusTitle("CONCECCIÓN CERRADA", R.color.yellow)
            is WebSocket.Event.OnConnectionClosing -> changeStatusTitle("CERRANDO CONECCIÓN...", R.color.yellow)
            is WebSocket.Event.OnConnectionFailed -> changeStatusTitle("LA CONNECCIÓN FALLÓ", R.color.red)
            is WebSocket.Event.OnMessageReceived -> handleOnMessageReceived(response.message)
        }
    }

    @SuppressLint("SetTextI18n")
    private fun handleOnMessageReceived(message: Message) {
        //   binding.textHome.text = message.toValue()
        Toast.makeText(this, message.toValue(), Toast.LENGTH_SHORT).show()
    }

    private fun Message.toValue(): String {
        return when (this) {
            is Message.Text -> value
            is Message.Bytes -> value.toString()
        }
    }

    private fun changeStatusTitle(title: String, color: Int) {
        Toast.makeText(this, title, Toast.LENGTH_SHORT).show()
           this.findViewById<TextView>(R.id.tv_status).apply {
               text = title
               setBackgroundColor(ContextCompat.getColor(context, color))
           }
    }






}