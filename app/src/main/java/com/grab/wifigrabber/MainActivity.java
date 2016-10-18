package com.grab.wifigrabber;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.net.wifi.ScanResult;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity
{
    WifiHandler wifiHandler;
    DescriptionButtonListAdapter adapter;
    WifiUpdateThread updater;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(Build.VERSION.SDK_INT >= 23)
        {
            requestPermission(Manifest.permission.ACCESS_FINE_LOCATION);
        }

        wifiHandler = new WifiHandler(this);
        updater = new WifiUpdateThread(this,1500);
        Thread t = new Thread(updater);
        t.start();

        inicializeListView();
    }

    /**
     * Inicializace grafického prvku pro zobrazení výsledků
     */
    void inicializeListView()
    {
        List<String> titles = new ArrayList<String>();
        List<String> descs = new ArrayList<String>();
        adapter = new DescriptionButtonListAdapter(this,R.layout.list_node,R.id.itemName,R.id.itemDesc,
                R.id.descLayout,titles,descs,R.id.button,this);
        ListView listview = (ListView) findViewById(R.id.contentListView);
        listview.setAdapter(adapter);
    }

    /**
     * Metoda volaná přes UI vlákno, která obnoví seznam wifi sítí
     */
    public void refreshWifiInfo()
    {
        wifiHandler.refresh(); // požadavek pro získání nového seznamu

        if(wifiHandler.getWifiInfo() == null) return; // pokud žádný seznam není, konec

        List<String> newTitles = new ArrayList<>();
        List<String> newInfo = new ArrayList<>();

        for(ScanResult r : wifiHandler.getWifiInfo()) // naplníme seznamy pro nadpisy a podrobnosti
        {
            newTitles.add(r.SSID);
            String info = "Signal: " + r.level + "\n";
            info += "Frequency: " + r.frequency + "\n";
            info += "Additional: " + r.capabilities + "\n";
            newInfo.add(info);
        }
        adapter.setTitles(newTitles); // prirazení nových hodnot do adaptéru
        adapter.setDescriptions(newInfo);
    }

    @TargetApi(Build.VERSION_CODES.M)
    void requestPermission(String permission)
    {
        requestPermissions(new String[]{permission},1);
    }

    /**
     * Metoda pro funkcionalitu tlačítka connect
     * @param SSID
     */
    public void buttonPress(final String SSID)
    {
        if(wifiHandler.getSecurityType(SSID) != SecurityType.NONE) // pokud wifi potrebuje heslo, vyvoláme AlertDialog s polem na heslo
        {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            final EditText passText = new EditText(this);
            passText.setHint("Password");
            passText.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
            builder.setView(passText);
            builder.setTitle("Secured wifi");
            builder.setPositiveButton("Ok", new DialogInterface.OnClickListener()
            {
                @Override
                public void onClick(DialogInterface dialog, int which)
                {
                    wifiHandler.connectToWifi(SSID,passText.getText().toString()); // po ukoncení dialogu tlačítkem ok připojujeme se získanou hodnout hesla
                }
            });

            builder.create().show();
        }
        else // jinak rovnou připojujeme
        {
            wifiHandler.connectToWifi(SSID,"");
        }
    }

    @Override
    protected void onDestroy()
    {
        super.onStop();
        updater.stop(); // při ukončován aplikace zastavíme update vlákno a chytání eventů z wifi handleru
        wifiHandler.unregisterReciever();
    }
}
