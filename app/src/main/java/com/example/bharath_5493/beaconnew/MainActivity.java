package com.example.bharath_5493.beaconnew;

import android.annotation.TargetApi;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothAdapter.LeScanCallback;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanRecord;
import android.bluetooth.le.ScanResult;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.RemoteException;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.Toast;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.MonitorNotifier;
import org.altbeacon.beacon.RangeNotifier;
import org.altbeacon.beacon.Region;

import java.nio.ByteBuffer;
import java.util.Base64;
import java.util.Collection;
import java.util.UUID;

public class MainActivity extends AppCompatActivity implements BeaconConsumer{

    private BeaconManager beaconManager;
    ScanFilter mScanFilter;

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        final SharedPreferences prefs = getSharedPreferences("f1nd.initial.bharath.newUI", Context.MODE_PRIVATE);
        prefs.edit().putBoolean("camera",false).apply();


        //beaconManager = BeaconManager.getInstanceForApplication(this);

        //beaconManager.getBeaconParsers().add(new BeaconParser("m:2-3=0215,i:4-19,i:21-22,i:23-24,p:25-25,d:20-20"));
        //beaconManager.getBeaconParsers().add(new BeaconParser().setBeaconLayout("m:2-3=beac,i:4-19,i:20-21,i:22-23,p:24-24,d:25-25"));

        //beaconManager.bind(this);

        Toast.makeText(getApplicationContext(),"Pleas enable Storage / Location / Bluetooth / Camera / Microphone permissions",Toast.LENGTH_SHORT).show();

        Button b =(Button)findViewById(R.id.b);



        BluetoothAdapter adap = BluetoothAdapter.getDefaultAdapter();
        final BluetoothLeScanner scanner = adap.getBluetoothLeScanner();

        LeScanCallback mCB = new LeScanCallback() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
                StringBuilder sb = new StringBuilder();
                for (int i =4;i<20;i++) {
                    sb.append(String.format("%02X ", scanRecord[i]));
                }
                String uuid = sb.toString().trim();
                String compnayIdentifier = String.format("%02X ", scanRecord[0]).concat(String.format("%02X ", scanRecord[1])).trim();
                Log.d("baby", "" + uuid);
                Toast.makeText(getApplicationContext(),"UUID : " + uuid,Toast.LENGTH_SHORT).show();
                if(compnayIdentifier.equals("00 59") && uuid.equals("01 12 23 34 45 56 67 78 89 9A AB BC CD DE EF F0")){
                    Log.d("baby", "hii");
                }

            }
        };


        final ScanCallback mmCB = new ScanCallback() {
            @Override
            public void onScanResult(int callbackType, ScanResult result) {
                final byte []scanRecord = result.getScanRecord().getBytes();
                StringBuilder sb = new StringBuilder();
                for (int i =4;i<20;i++) {
                    sb.append(String.format("%02X ", scanRecord[i]));
                }
                final String uuid = sb.toString().trim();
                final String compnayIdentifier = String.format("%02X ", scanRecord[0]).concat(String.format("%02X ", scanRecord[1])).trim();
                Log.d("baby", "" + uuid);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(),"UUID : " + uuid + " \nmotion byte :" + String.format("%02X ", scanRecord[20] )  + "\nCompany identifier "  + compnayIdentifier,Toast.LENGTH_LONG).show();
                    }
                });
                if(uuid.equals("01 12 23 34 45 56 67 78 89 9A AB BC CD DE EF F0")){
                    Log.d("baby", "hii");
                    if(String.format("%02X ", scanRecord[20]).equals("AA")){
                        Log.d("baby","motion detected");
                        Toast.makeText(getApplicationContext()," Motion detected : Starting camera",Toast.LENGTH_SHORT).show();

                        boolean flag = prefs.getBoolean("camera", false);

                        if(!flag){

                            prefs.edit().putBoolean("camera",true).apply();
                            Log.d("baby","babieee");
                            scanner.stopScan(this);
                            Intent i = new Intent(getApplicationContext(),cameraActivity.class);
                            startActivity(i);


                        }

                    }
                }
                super.onScanResult(callbackType, result);
            }
        };

        //adap.startLeScan(mCB);

        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                scanner.startScan(mmCB);

            }
        });

    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)


    @Override
    public boolean bindService(Intent service, ServiceConnection conn, int flags) {
        return super.bindService(service, conn, flags);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        beaconManager.unbind(this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBeaconServiceConnect() {

        Log.d("Baby","Connected");

        final Region regio = new Region("myBeacon",null,null,null);


        try {
            beaconManager.startMonitoringBeaconsInRegion(regio);
        } catch (RemoteException e) {
            e.printStackTrace();
        }


        beaconManager.addMonitorNotifier(new MonitorNotifier() {
            @Override
            public void didEnterRegion(Region region) {

                Log.d("Baby","Inside region");


            }

            @Override
            public void didExitRegion(Region region) {

                Log.d("Baby","Exited");
                try {
                    beaconManager.stopMonitoringBeaconsInRegion(region);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void didDetermineStateForRegion(int i, Region region) {

            }
        });

        beaconManager.addRangeNotifier(new RangeNotifier() {
            @Override
            public void didRangeBeaconsInRegion(Collection<Beacon> beacons, Region region) {

                Log.d("Baby","Range");
                for (Beacon b : beacons ){
                    Log.d("\nBaby", "" + b.getId1() + "/" + b.getId2() + "/" + b.getId3());
                }

            }
        });



    }

    public static String ByteArrayToString(byte[] ba)
    {
        StringBuilder hex = new StringBuilder(ba.length * 2);
        for (byte b : ba)
            hex.append(b + " ");
        return hex.toString();
    }
}
