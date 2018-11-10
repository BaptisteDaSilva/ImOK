package uqac.inf872.projet.imok.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class WifiReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(final Context context, final Intent intent) {
        Toast.makeText(context, "Change", Toast.LENGTH_LONG).show();
    }
}