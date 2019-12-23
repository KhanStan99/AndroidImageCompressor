package `in`.trentweet.imagecompressor

import `in`.trentweet.imagecompressor.utilities.Utility
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.text.Html
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.SeekBar
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.esafirm.imagepicker.features.ImagePicker
import com.esafirm.imagepicker.features.ReturnMode
import id.zelory.compressor.Compressor
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {

    //region Global Variables

    private lateinit var btnCompImages: Button
    private lateinit var utility: Utility
    private var photoFile: File? = null

    //endregion Global Variables

    //region Activity Functions

    private fun setSeekBarThings(progress: Int) {
        seekBar.progress = progress
        image_quality_text.text = getString(R.string.instruction_seek_bar, progress.toString())
    }

    private fun pickFile() {
        ImagePicker.create(this)
                .returnMode(ReturnMode.ALL)
                .folderMode(true)
                .toolbarFolderTitle("Gallery")
                .toolbarImageTitle("Tap to select")
                .toolbarArrowColor(Color.BLACK)
                .includeVideo(false)
                .single()
                .showCamera(true)
                .enableLog(false)
                .start()
    }

    private fun setPhotoInImageView(path: String) {
        Glide.with(this)
                .load(path)
                .into((findViewById<View>(R.id.userImage) as ImageView))
    }

    @SuppressLint("SetTextI18n")
    private fun compressImage() {
        if (photoFile != null) {
            val compressedImageBitmap: File
            try {
                compressedImageBitmap = Compressor(this)
                        .setQuality(seekBar.progress)
                        .compressToFile(photoFile)
                val bitmap = BitmapFactory.decodeFile(compressedImageBitmap.path)
                getImageUri(bitmap)
            } catch (e: IOException) {
                e.printStackTrace()
            }
        } else {
            utility.showToast("Please Select a photo first")
        }
    }

    private fun getImageUri(inImage: Bitmap) {
        val path = MediaStore.Images.Media.insertImage(this.contentResolver, inImage, "Title", null)
        Uri.parse(path)
        setPhotoInImageView(path.toString())
        val text = "<b> Name </b>: " + randomFileName +
                "\n<b>Size:</b> " + path.length.toString() + "KB (Approximately)"
        selectedImageName.text = Html.fromHtml(text)
        utility.showToast("Image saved at: Storage://0/Pictures")
    }

    fun pickImageClicked(view: View) {
        if (Build.VERSION.SDK_INT >= 23) {
            if (utility.isProvidedPermission) {
                pickFile()
            } else {
                utility.requestPermission()
            }
        } else {
            pickFile()
        }
    }

    private val randomFileName: String
        get() {
            @SuppressLint("SimpleDateFormat") val sdf = SimpleDateFormat("HH:mm dd-MM")
            return "trentweet.in_" + sdf.format(Date()) + ".jpg"
        }

    //endregion Activity Functions

    //region Override Methods

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btnCompImages = findViewById(R.id.btnCompImage)
        utility = Utility(this@MainActivity)

        setSeekBarThings(20)

        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(p0: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser)
                    setSeekBarThings(progress)
            }

            override fun onStartTrackingTouch(p0: SeekBar?) {

            }

            override fun onStopTrackingTouch(p0: SeekBar?) {

            }

        })

        btnCompImages.setOnClickListener { compressImage() }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (ImagePicker.shouldHandle(requestCode, resultCode, data)) {
            // Get a list of picked images
            val images = ImagePicker.getImages(data)
            // or get a single image only
            val image = ImagePicker.getFirstImageOrNull(data)
            if (image != null) {
                photoFile = File(image.path)
                val sizeInKb = (photoFile!!.length() / 1024).toInt()
                setPhotoInImageView(image.path)
                val text = "<b>Name</b>: " + randomFileName +
                        "\n<b>Size:</b> " + sizeInKb + "KB"
                selectedImageName.text = Html.fromHtml(text)
                btnCompImages.visibility = View.VISIBLE
                pickImage.text = getString(R.string.change_image)
            }
        } else {
            btnCompImages.visibility = View.GONE
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                grantResults[1] == PackageManager.PERMISSION_GRANTED &&
                grantResults[2] == PackageManager.PERMISSION_GRANTED) {
            pickFile()
        } else {
            utility.showToast("Permission(s) Denied")
        }
    }

    //endregion Override Methods

}