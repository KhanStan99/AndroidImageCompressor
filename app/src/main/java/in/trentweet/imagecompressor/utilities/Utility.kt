package `in`.trentweet.imagecompressor.utilities

import android.Manifest
import android.app.Activity
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class Utility(private val context: Activity) {
    //If any of the permission is not granted then this function will return false.
    // User must grant all permissions.
    val isProvidedPermission: Boolean
        get() {
            val STORAGE_READ = ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE)
            val STORAGE_WRITE = ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE)
            val CAMERA = ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA)
            //If any of the permission is not granted then this function will return false.
// User must grant all permissions.
            return STORAGE_READ == 0 && STORAGE_WRITE == 0 && CAMERA == 0
        }

    fun requestPermission() { //Initializing Request for storage read/write and camera
        val PERMISSIONS = arrayOf(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.CAMERA)
        ActivityCompat.requestPermissions(context, PERMISSIONS, REQUEST_CODE)
    }

    fun showToast(msg: String?) {
        Toast.makeText(context, msg, Toast.LENGTH_LONG).show()
    }

    companion object {
        const val REQUEST_CODE = 1
    }

}