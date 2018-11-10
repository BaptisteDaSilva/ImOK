package uqac.inf872.projet.imok.progressBar;

import android.app.ProgressDialog;
import android.os.Handler;
import android.os.Message;

public class ProgressHandler extends Handler {
    @Override
    public void handleMessage(Message msg) {
        super.handleMessage(msg);
        ProgressDialog progressBar = (ProgressDialog) msg.obj;
        progressBar.setProgress(msg.arg1);
    }
}