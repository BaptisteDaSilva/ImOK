package uqac.inf872.projet.imok.controllers.activities;

import android.content.Context;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.google.firebase.perf.metrics.AddTrace;

import uqac.inf872.projet.imok.R;

public class WiFiActivity extends AppCompatActivity {

    @Override
    @AddTrace(name = "test_trace")
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wi_fi);

        WifiManager manager = (WifiManager) this.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        if ( manager != null && manager.isWifiEnabled() ) {
            WifiInfo wifiInfo = manager.getConnectionInfo();
            if ( wifiInfo != null ) {
                NetworkInfo.DetailedState state = WifiInfo.getDetailedStateOf(wifiInfo.getSupplicantState());
                if ( state == NetworkInfo.DetailedState.CONNECTED || state == NetworkInfo.DetailedState.OBTAINING_IPADDR ) {
                    TextView wifiName = findViewById(R.id.wifiName);

                    wifiName.setText(wifiInfo.toString());
                }
            }
        }
    }
}
