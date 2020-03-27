package com.leon.taller2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.FileNotFoundException;
import java.io.InputStream;

public class ImageActivity extends AppCompatActivity {

    Button camara;
    ImageView imagen;
    Button seleccion;
    static final int REQUEST_IMAGE_CAPTURE = 1;
    static final int IMAGE_PICKER_REQUEST = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image);

        camara = findViewById(R.id.camara);
        imagen = findViewById(R.id.image);
        seleccion = findViewById(R.id.seleccion);

        requestPermission(this, Manifest.permission.CAMERA, "Es necesario para acceder a la camara", permisos.PERMISSION_CAMERA.ordinal() );
        requestPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE, "Es necesario para acceder a imagenes", permisos.PERMISSION_READ_EXTERNAL_STORAGE.ordinal() );

        camara.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(ImageActivity.this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED)
                {
                    takePicture();
                }
            }
        });

        seleccion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(ImageActivity.this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(ImageActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)
                {
                    chooseImage();
                }
            }
        });
    }

    public void requestPermission(Activity context, String permission, String just, int id) {
        if (ContextCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(context, permission)) {
                Toast.makeText(context, just, Toast.LENGTH_LONG).show();
            }
            ActivityCompat.requestPermissions(context, new String[]{permission}, id);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull  String[] permissions, @NonNull int[] grantResults) {
        if(permisos.PERMISSION_CAMERA.ordinal() == requestCode){
            takePicture();
        }

        if(permisos.PERMISSION_READ_EXTERNAL_STORAGE.ordinal() == requestCode){
            chooseImage();
        }
    }

    private void takePicture() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    private void chooseImage(){
        Intent pickImage = new Intent(Intent.ACTION_PICK);
        pickImage.setType("image/*");
        startActivityForResult(pickImage, IMAGE_PICKER_REQUEST);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch(requestCode) {
            case IMAGE_PICKER_REQUEST:
                if(resultCode == RESULT_OK){
                    try {
                        final Uri imageUri = data.getData();
                        final InputStream imageStream = getContentResolver().openInputStream(imageUri);
                        final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                        imagen.setImageBitmap(selectedImage);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                }

            case REQUEST_IMAGE_CAPTURE:
            {
                if(resultCode == RESULT_OK){
                    Bundle extras = data.getExtras();
                    Bitmap imageBitmap = (Bitmap) extras.get("data");
                    imagen.setImageBitmap(imageBitmap);
                }
            }
        }
    }
}
