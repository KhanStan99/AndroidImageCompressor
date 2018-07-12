package in.trentweet.imagecompressor.utilities;

import android.Manifest;
import android.app.Activity;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.widget.Toast;

public class Utility {

    private Activity context;
    public final static int REQUEST_CODE = 1;

    public Utility(Activity _context) {
        this.context = _context;
    }

    public Boolean isProvidedPermission() {
        int STORAGE_READ = ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE);
        int STORAGE_WRITE = ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int CAMERA = ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA);

        //If any of the permission is not granted then this function will return false.
        // User must grant all permissions.

        return STORAGE_READ == 0 && STORAGE_WRITE == 0 && CAMERA == 0;
    }

    public void requestPermission() {

        //Initializing Request for storage read/write and camera

        String[] PERMISSIONS = {
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.CAMERA};
        ActivityCompat.requestPermissions(context, PERMISSIONS, REQUEST_CODE);
    }

    public void showToast(String msg) {
        Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
    }

}
