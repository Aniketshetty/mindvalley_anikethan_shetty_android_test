package com.example.anikethan.mindvalley_anikethan_android_test.Utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.util.Log;

/**
 * Created by $ANIKETHAN on 8/11/2016.
 */
public class NetworkChecker {
    private static final String TAG = "Network Available Error";
    private Context mContext;
//    ConnectivityManager cm = null;

    public NetworkChecker(Context context) {
        this.mContext = context;
    }

    public NetworkChecker() {

    }

    /**
     * Checking for all possible internet providers
     **/
    public boolean isConnectingToInternet(Context mContext) {
        boolean isNetworkAvailable = false;
        ConnectivityManager cm = (ConnectivityManager) mContext
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        try {

            if (cm != null) {
                NetworkInfo netInfo = cm.getActiveNetworkInfo();
                if (netInfo != null) {
                    isNetworkAvailable = netInfo.isConnectedOrConnecting();
                }
            }
        } catch (Exception ex) {
            Log.e(TAG, ex.getMessage());
        }
        //check for wifi also
        if (!isNetworkAvailable) {
            WifiManager wifiManager = (WifiManager) mContext
                    .getSystemService(Context.WIFI_SERVICE);
            NetworkInfo.State wifi = cm.getNetworkInfo(1).getState();
            if (wifiManager.isWifiEnabled()
                    && wifi.toString().equalsIgnoreCase("CONNECTED")) {
                isNetworkAvailable = true;
            } else {

                isNetworkAvailable = false;
            }

        }
        return isNetworkAvailable;
    }


}
