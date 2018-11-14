package uqac.inf872.projet.imok.controllers.activities;

import android.hardware.Camera;
import android.os.Bundle;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.google.firebase.perf.metrics.AddTrace;

import java.io.IOException;

import uqac.inf872.projet.imok.R;
import uqac.inf872.projet.imok.base.BaseActivity;

public class CameraActivity extends BaseActivity implements SurfaceHolder.Callback {

    private Camera mCamera = null;

    @Override
    @AddTrace(name = "test_trace")
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SurfaceView surface = findViewById(R.id.surface_view);
        SurfaceHolder holder = surface.getHolder();

        // On déclare que la classe actuelle gérera les callbacks
        holder.addCallback(this);
    }

    @Override
    public int getFragmentLayout() {
        return R.layout.activity_camera;
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
