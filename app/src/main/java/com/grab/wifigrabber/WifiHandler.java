package com.grab.wifigrabber;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;

import java.util.List;

/**
 * Třída, která drží informace o aktivních wifi sítích a zařizuje připojení k nim.
 */
public class WifiHandler
{
    private WifiManager manager;
    private List<ScanResult> results;
    private Context context;
    private BroadcastReceiver reciever;

    public WifiHandler(Context context)
    {
        this.context = context;
        manager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        manager.setWifiEnabled(true);
        reciever = new BroadcastReceiver()
        {
            @Override
            public void onReceive(Context context, Intent intent)
            {
                results = manager.getScanResults();
            }
        };
        context.registerReceiver(reciever, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
        refresh();
    }

    /**
     * Zahájí scan wifi sítí
     */
    public void refresh()
    {
        manager.startScan();
    }

    /**
     * Vrací list výsledků posledního scanu
     * @return list vysledků
     */
    public List<ScanResult> getWifiInfo()
    {
        return results;
    }

    /**
     * Pokusí se připojit na wifi s daným SSID a heslem
     * @param SSID
     * @param password pro sítě bez hesla může mít jakoukoliv hodnotu
     * @return true pokud byl požadavek o připojení uspěšný, jinak false
     */
    public boolean connectToWifi(String SSID,String password)
    {
        WifiConfiguration conf = new WifiConfiguration();
        conf.SSID = "\"" + SSID + "\"";

        switch(getSecurityType(SSID)) // naplníme konfiguraci podle typu zabezpečení
        {
            case WEP:
                conf.wepKeys[0] = password;
                conf.wepTxKeyIndex = 0;
                conf.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
                conf.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.SHARED);
                break;
            case WPA:
                conf.preSharedKey = "\"" + password + "\"";
                break;
            case NONE:
                conf.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
        }

        int netId = manager.addNetwork(conf);

        if(netId != -1) // odpojíme stávající sít, a poté připojíme novou
        {
            for(WifiConfiguration con : manager.getConfiguredNetworks()) // vyřadíme z provozu ostatní zapamatované sítě
            {
                manager.disableNetwork(con.networkId);
            }

            manager.disconnect();
            manager.enableNetwork(netId, true);
            manager.reconnect();
            return true;
        }
        return false;
    }

    /**
     *  Zjistí zda sít s daným SSID potřebuje heslo
     * @param SSID
     * @return true pokud heslo vyžaduje, jinak false
     */
    public SecurityType getSecurityType(String SSID)
    {
        for(ScanResult s : results)
        {
            if(s.SSID.equals(SSID))
            {
                if(s.capabilities.contains("WPA"))
                {
                    return SecurityType.WPA;
                }

                if(s.capabilities.contains("WEP"))
                {
                    return SecurityType.WEP;
                }
            }
        }

        return SecurityType.NONE;
    }

    /**
     * Metoda, která zruší kontrolovní eventu skončení skanu wifi sítí, nutné volat při přerušení appky
     */
    public void unregisterReciever()
    {
        context.unregisterReceiver(reciever);
    }
}

enum SecurityType
{
    WEP,
    WPA,
    NONE
}
