package in.trentweet.imagecompressor;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.fxn.pix.Pix;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

import id.zelory.compressor.Compressor;
import in.trentweet.imagecompressor.utilities.Logger;
import in.trentweet.imagecompressor.utilities.Utility;

public class MainActivity extends AppCompatActivity {

    //region Global Variables

    private Button pickImage, btnCompImages;
    private Utility utility;
    private Context context;
    private File photoFile = null;
    private TextView selectedImageName, compImageName;

    //endregion Global Variables

    //region Activity Functions

    private void pickFile() {
        int LIMIT = 1;
        Pix.start(MainActivity.this, Utility.REQUEST_CODE, LIMIT);
    }

    private void setPhotoInImageView(int imageViewId, String path) {
        Glide.with(context)
                .load(path)
                .into((ImageView) findViewById(imageViewId));
    }

    @SuppressLint("SetTextI18n")
    private void compressImage() {
        if (photoFile != null) {
            File compressedImageBitmap;
            try {
                compressedImageBitmap = new Compressor(context)
                        .setQuality(30) //Quality set to 30, You can change it to whatever you want. OR ask from user :)
                        .compressToFile(photoFile);
                Bitmap bitmap = BitmapFactory.decodeFile(compressedImageBitmap.getPath());
                getImageUri(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            utility.showToast("Please Select a photo first");
        }

    }

    private void getImageUri(Bitmap inImage) {
        String path = MediaStore.Images.Media.insertImage(context.getContentResolver(), inImage, "Title", null);
        Uri.parse(path);
        setPhotoInImageView(R.id.compImage, String.valueOf(path));

        String text = "<b> Name </b>: " + getRandomFileName() +
                " <b>Size:</b> " + String.valueOf(path.length()) + "KB (Approximately)";
        compImageName.setText(Html.fromHtml(text));

        utility.showToast("Image saved at: Storage://0/Pictures");

    }

    private String getRandomFileName() {
        @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("MMdd_HHmm");
        return "Compressed Image " + sdf.format(new Date()) + ".jpg";
    }

    //endregion Activity Functions

    //region Override Methods

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        pickImage = findViewById(R.id.pickImage);
        btnCompImages = findViewById(R.id.btnCompImage);
        selectedImageName = findViewById(R.id.selectedImageName);
        compImageName = findViewById(R.id.compImageName);
        utility = new Utility(MainActivity.this);
        context = MainActivity.this;

        pickImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= 23) {
                    if (utility.isProvidedPermission()) {
                        pickFile();
                    } else {
                        utility.requestPermission();
                    }
                } else {
                    pickFile();
                }
            }
        });

        btnCompImages.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                compressImage();
            }
        });

    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == Utility.REQUEST_CODE && resultCode == Activity.RESULT_OK && data != null) {
            ArrayList<String> returnValue = data.getStringArrayListExtra(Pix.IMAGE_RESULTS);
            photoFile = new File(returnValue.get(0));
            int sizeInKb = (int) (photoFile.length() / 1024);
            setPhotoInImageView(R.id.userImage, returnValue.get(0));

            String text = "<b> Name </b>: " + getRandomFileName() +
                    " <b>Size:</b> " + String.valueOf(sizeInKb) + "KB";

            selectedImageName.setText(Html.fromHtml(text));

            btnCompImages.setVisibility(View.VISIBLE);
            pickImage.setText("Change Image");
        } else {
            btnCompImages.setVisibility(View.GONE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case Utility.REQUEST_CODE:
                Logger.error("Details", "Request Code:" + requestCode + "\n" +
                        "Permissions " + Arrays.toString(permissions) + "\n" +
                        "Grant Results" + Arrays.toString(grantResults));

                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                        grantResults[1] == PackageManager.PERMISSION_GRANTED
                        && grantResults[2] == PackageManager.PERMISSION_GRANTED
                        ) {
                    pickFile();
                } else {
                    utility.showToast("Permission(s) Denied");
                }

                break;
        }

    }

    //endregion Override Methods

}
