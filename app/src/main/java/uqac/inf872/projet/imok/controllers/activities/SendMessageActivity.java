package uqac.inf872.projet.imok.controllers.activities;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.EditText;

import com.google.firebase.perf.metrics.AddTrace;

import java.util.ArrayList;

import uqac.inf872.projet.imok.R;

public class SendMessageActivity extends AppCompatActivity {

    private static final int MAX_SMS_MESSAGE_LENGTH = 160;

    private static final int MY_PERMISSIONS_REQUEST_SEND_SMS = 1;

    @Override
    @AddTrace(name = "test_trace")
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_message);

        if ( ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS)
                != PackageManager.PERMISSION_GRANTED ) {
            if ( ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.SEND_SMS) ) {
            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.SEND_SMS},
                        MY_PERMISSIONS_REQUEST_SEND_SMS);
            }
        }
    }

    public void onClickEnvoyer(View view) {

        //On récupère les deux EditText correspondant aux champs pour entrer le numéro et le message
        EditText numero = findViewById(R.id.numero);
        EditText message = findViewById(R.id.message);

        //On récupère ce qui a été entré dans les EditText
        String num = numero.getText().toString();
        String msg = message.getText().toString();

        SmsManager manager = SmsManager.getDefault();

        int length = msg.length();

        if ( length > MAX_SMS_MESSAGE_LENGTH ) {
            ArrayList<String> messagelist = manager.divideMessage(msg);

            manager.sendMultipartTextMessage(num, null, messagelist, null, null);
        } else {
            manager.sendTextMessage(num, null, msg, null, null); // piSend, piDelivered);
        }
    }
}
