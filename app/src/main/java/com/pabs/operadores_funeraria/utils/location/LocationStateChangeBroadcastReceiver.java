package com.pabs.operadores_funeraria.utils.location;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;

import com.pabs.operadores_funeraria.R;
import com.pabs.operadores_funeraria.utils.StatusGPSConnection;

public class LocationStateChangeBroadcastReceiver extends BroadcastReceiver
{

    public static final String GPS_CHANGE_ACTION = "com.android.broadcast_listeners.LocationChangeReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction() != null && intent.getAction().equals(context.getString(R.string.location_change_receiver))) {
            if (!isGpsEnabled(context)) {
                sendInternalBroadcast(context, StatusGPSConnection.DISABLED.name());
            }else {
                sendInternalBroadcast(context, StatusGPSConnection.ENABLED.name());
            }
        }
    }

    private void sendInternalBroadcast(Context context, String status) {
        try {
            Intent intent = new Intent();
            intent.putExtra("Gps_state", status);
            intent.setAction(GPS_CHANGE_ACTION);
            context.sendBroadcast(intent);

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static boolean isGpsEnabled(Context context) {
        LocationManager manager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);

        return manager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }
}