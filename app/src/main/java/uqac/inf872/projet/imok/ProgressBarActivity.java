package uqac.inf872.projet.imok;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.perf.metrics.AddTrace;

import uqac.inf872.projet.imok.progressBar.ProgressHandler;

public class ProgressBarActivity extends Activity {
    private final static int PROGRESS_DIALOG_ID = 0;
    private final static int MAX_SIZE = 20;
    private final static int PROGRESSION = 0;

    private Button mProgressButton = null;
    private ProgressDialog mProgressBar = null;
    private Thread mProgress = null;

    private int mProgression = 0;

    // Gère les communications avec le thread de téléchargement
    private ProgressHandler mHandler = new ProgressHandler();

    @Override
    @AddTrace(name = "test_trace")
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_progress_bar);

        mProgressButton = findViewById(R.id.progress_button);
        mProgressButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Initialise la boîte de dialogue
                showDialog(PROGRESS_DIALOG_ID);

                // On remet le compteur à zéro
                mProgression = 0;

                mProgress = new Thread(new Runnable() {
                    public void run() {
                        try {
                            while (mProgression < MAX_SIZE) {
                                // On télécharge un bout du fichier
                                mProgression = download();

                                // Repose-toi pendant une seconde
                                Thread.sleep(1000);

                                Message msg = mHandler.obtainMessage(PROGRESSION, mProgression, 0);
                                msg.obj = mProgressBar;
                                mHandler.sendMessage(msg);
                            }

                            // Le fichier a été téléchargé
                            if ( mProgression >= MAX_SIZE ) {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(ProgressBarActivity.this, ProgressBarActivity.this.getString(R.string.over), Toast.LENGTH_SHORT).show();
                                    }
                                });

                                // Ferme la boîte de dialogue
                                mProgressBar.dismiss();
                            }
                        } catch (InterruptedException e) {
                            // Si le thread est interrompu, on sort de la boucle de cette manière
                            e.printStackTrace();
                        }
                    }
                });
                mProgress.start();
            }
        });
    }

    @Override
    public Dialog onCreateDialog(int identifiant) {
        if ( mProgressBar == null ) {
            mProgressBar = new ProgressDialog(this);
            // L'utilisateur peut annuler la boîte de dialogue
            mProgressBar.setCancelable(true);
            // Que faire quand l'utilisateur annule la boîte ?
            mProgressBar.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    // On interrompt le thread
                    mProgress.interrupt();
                    Toast.makeText(ProgressBarActivity.this, ProgressBarActivity.this.getString(R.string.canceled), Toast.LENGTH_SHORT).show();
                    removeDialog(PROGRESS_DIALOG_ID);
                }
            });
            mProgressBar.setTitle("Téléchargement en cours");
            mProgressBar.setMessage("C'est long...");
            mProgressBar.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            mProgressBar.setMax(MAX_SIZE);
        }
        return mProgressBar;
    }

    public int download() {
        if ( mProgression <= MAX_SIZE ) {
            mProgression++;
            return mProgression;
        }
        return MAX_SIZE;
    }
}