package com.StevenGibson.outdoorcompanion;

import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.util.Log;

import java.lang.reflect.Method;

public class MyHotspotManager {
    private WifiConfiguration wifiCon;
    private HotspotDetails hotspotDetails;
    private WifiManager mWifiManager;

    public MyHotspotManager(WifiManager wifiManager){
        this.mWifiManager = wifiManager;
    }

    public boolean setHotspot(HotspotDetails hotspotDetails) {
        this.hotspotDetails = hotspotDetails;
        boolean apstatus;
        wifiCon = new WifiConfiguration();
        wifiCon.SSID = hotspotDetails.getSsid();
        wifiCon.preSharedKey = hotspotDetails.getPassword();
        wifiCon.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.SHARED);
        wifiCon.allowedProtocols.set(WifiConfiguration.Protocol.RSN);
        wifiCon.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
        wifiCon.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
        try {
            Method setWifiApMethod = mWifiManager.getClass().getMethod("setWifiApEnabled", WifiConfiguration.class, boolean.class);
            apstatus = (Boolean) setWifiApMethod.invoke(mWifiManager, wifiCon, true);
        } catch (Exception e) {
            Log.e(this.getClass().toString(), "", e);
            return false;
        }

        return apstatus;
    }

}
