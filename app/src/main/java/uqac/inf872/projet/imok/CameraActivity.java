package uqac.inf872.projet.imok;

import android.Manifest;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.google.firebase.perf.metrics.AddTrace;

import java.io.IOException;

public class CameraActivity extends BaseActivity implements SurfaceHolder.Callback {
    private static final int MY_PERMISSIONS_REQUEST_CAMERA = 1;

    private Camera mCamera = null;

    @Override
    @AddTrace(name = "test_trace")
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);

        if ( ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED ) {
            if ( ActivityCompat.shouldShowRequestPermissionRationale(this,
                    android.Manifest.permission.CAMERA) ) {
            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.CAMERA},
                        MY_PERMISSIONS_REQUEST_CAMERA);
            }
        }

        SurfaceView surface = findViewById(R.id.surface_view);
        SurfaceHolder holder = surface.getHolder();

        // On déclare que la classe actuelle gérera les callbacks
        holder.addCallback(this);
    }

    // Se déclenche quand la surface est créée
    public void surfaceCreated(SurfaceHolder holder) {
        try {
            mCamera.setPreviewDisplay(holder);
            mCamera.startPreview();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Se déclenche quand la surface est détruite
    public void surfaceDestroyed(SurfaceHolder holder) {
    }

    // Se déclenche quand la surface change de dimensions ou de format
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
    }

    @Override
    protected void onResume() {
        super.onResume();
        mCamera = Camera.open();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mCamera.release();
    }
}
