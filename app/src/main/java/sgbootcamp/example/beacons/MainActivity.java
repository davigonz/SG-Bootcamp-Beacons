

package sgbootcamp.example.beacons;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.RangeNotifier;
import org.altbeacon.beacon.Region;

import java.util.Collection;

/**
 * App main activity, gestiona la detección de beacons, mostrando un mensaje de los beacons
 * detectados
 *
 * @author David González Verdugo
 */
public class MainActivity extends AppCompatActivity implements View.OnClickListener, BeaconConsumer,
 RangeNotifier {

    protected final String TAG = MainActivity.this.getClass().getSimpleName();;
    private static final int PERMISSION_REQUEST_COARSE_LOCATION = 1;
    private static final int REQUEST_ENABLE_BLUETOOTH = 1;
    private static final long DEFAULT_SCAN_PERIOD_MS = 6000l;
    private static final String ALL_BEACONS_REGION = "AllBeaconsRegion";

    // Para interactuar con los beacons desde una actividad
    private BeaconManager mBeaconManager;

    // Representa el criterio de campos con los que buscar beacons
    private Region mRegion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getStartButton().setOnClickListener(this);
        getStopButton().setOnClickListener(this);

        //TODO Inicializar variables necesarias
    }

    @Override
    public void onClick(View view) {

        if (view.equals(findViewById(R.id.startReadingBeaconsButton))) {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

                // Si los permisos de localización todavía no se han concedido, solicitarlos
                if (this.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) !=
                        PackageManager.PERMISSION_GRANTED) {

                    askForLocationPermissions();

                } else { // Permisos de localización concedidos

                    prepareDetection();
                }

            } else { // Versiones de Android < 6

                prepareDetection();
            }

        } else if (view.equals(findViewById(R.id.stopReadingBeaconsButton))) {

            stopDetectingBeacons();

            BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

            // Desactivar bluetooth
            if (mBluetoothAdapter.isEnabled()) {
                mBluetoothAdapter.disable();
            }
        }
    }

    /**
     * Activar localización y bluetooth para empezar a detectar beacons
     */
    private void prepareDetection() {

        if (!isLocationEnabled()) {

            askToTurnOnLocation();

        } else { // Localización activada, comprobemos el bluetooth

            BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

            if (mBluetoothAdapter == null) {

                showToastMessage(getString(R.string.not_support_bluetooth_msg));

            } else if (mBluetoothAdapter.isEnabled()) {

                startDetectingBeacons();

            } else {

                // Pedir al usuario que active el bluetooth
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BLUETOOTH);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == REQUEST_ENABLE_BLUETOOTH) {

            // Usuario ha activado el bluetooth
            if (resultCode == RESULT_OK) {

                startDetectingBeacons();

            } else if (resultCode == RESULT_CANCELED) { // User refuses to enable bluetooth

                showToastMessage(getString(R.string.no_bluetooth_msg));
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * Empezar a detectar beacons, ocultando o mostrando los botones correspondientes
     */
    private void startDetectingBeacons() {

        //TODO Enlazar servicio de beacons, fijando un periodo de escaneo

        // Desactivar botón de comenzar
        getStartButton().setEnabled(false);
        getStartButton().setAlpha(.5f);

        // Activar botón de parar
        getStopButton().setEnabled(true);
        getStopButton().setAlpha(1);
    }

    @Override
    public void onBeaconServiceConnect() {

//        try {

            // TODO Empezar a buscar beacons en una región

            showToastMessage(getString(R.string.start_looking_for_beacons));

//        } catch (RemoteException e) {
//            Log.d(TAG, "Se ha producido una excepción al empezar a buscar beacons " + e.getMessage());
//        }

        // TODO Hacer que esta actividad sea llamada cada DEFAULT_SCAN_PERIOD_MS segundos con los beacons detectados
    }


    /**
     * Método llamado cada DEFAULT_SCAN_PERIOD_MS segundos con los beacons detectados durante ese
     * periodo
     */
    @Override
    public void didRangeBeaconsInRegion(Collection<Beacon> beacons, Region region) {

        if (beacons.size() == 0) {
            showToastMessage(getString(R.string.no_beacons_detected));
        }

        //TODO Recorrer beacons mostrando la información necesaria
    }

    private void stopDetectingBeacons() {

        //TODO Parar de detectar beacons

        //try {

            showToastMessage(getString(R.string.stop_looking_for_beacons));

        //} catch (RemoteException e) {
        //    Log.d(TAG, "Se ha producido una excepción al parar de buscar beacons " + e.getMessage());
        //}

        // Activar botón de comenzar
        getStartButton().setEnabled(true);
        getStartButton().setAlpha(1);

        // Desactivar botón de parar
        getStopButton().setEnabled(false);
        getStopButton().setAlpha(.5f);
    }

    /**
     * Comprobar permisión de localización para Android >= M
     */
    private void askForLocationPermissions() {

        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.location_access_needed);
        builder.setMessage(R.string.grant_location_access);
        builder.setPositiveButton(android.R.string.ok, null);
        builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            public void onDismiss(DialogInterface dialog) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                        PERMISSION_REQUEST_COARSE_LOCATION);
            }
        });
        builder.show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_COARSE_LOCATION: {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    prepareDetection();
                } else {
                    final AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle(R.string.funcionality_limited);
                    builder.setMessage(getString(R.string.location_not_granted) +
                            getString(R.string.cannot_discover_beacons));
                    builder.setPositiveButton(android.R.string.ok, null);
                    builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialog) {

                        }
                    });
                    builder.show();
                }
                return;
            }
        }
    }

    /**
     * Comprobar si la localización está activada
     *
     * @return true si la localización esta activada, false en caso contrario
     */
    private boolean isLocationEnabled() {

        LocationManager lm = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        boolean networkLocationEnabled = false;

        boolean gpsLocationEnabled = false;

        try {
            networkLocationEnabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

            gpsLocationEnabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);

        } catch (Exception ex) {
            Log.d(TAG, "Excepción al obtener información de localización");
        }

        return networkLocationEnabled || gpsLocationEnabled;
    }

    /**
     * Abrir ajustes de localización para que el usuario pueda activar los servicios de localización
     */
    private void askToTurnOnLocation() {

        // Notificar al usuario
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setMessage(R.string.location_disabled);
        dialog.setPositiveButton(R.string.location_settings, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                // TODO Auto-generated method stub
                Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(myIntent);
            }
        });
        dialog.show();
    }

    private Button getStartButton() {
        return (Button) findViewById(R.id.startReadingBeaconsButton);
    }

    private Button getStopButton() {
        return (Button) findViewById(R.id.stopReadingBeaconsButton);
    }

    /**
     * Mostrar mensaje
     *
     * @param message mensaje a enseñar
     */
    private void showToastMessage (String message) {
        Toast toast = Toast.makeText(this, message, Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mBeaconManager.removeAllRangeNotifiers();
        mBeaconManager.unbind(this);
    }
}
