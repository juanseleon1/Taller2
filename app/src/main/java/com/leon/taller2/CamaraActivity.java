package com.leon.taller2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
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

public class CamaraActivity extends AppCompatActivity {

    Button selec,tomar;
    ImageView img;
    final static int SELEC=1, CAMARA=2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camara);
        img=findViewById(R.id.foto);
        selec=findViewById(R.id.selec);
        tomar=findViewById(R.id.tomar);
        selec.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i= new Intent(Intent.ACTION_PICK);
                i.setType("image/*");
                startActivityForResult(i,SELEC);
            }
        });

        tomar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                camara();
            }
        });
    }

    private void selecPic()
    {
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED) {
            permisos.requestPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE, null, permisos.PERMISSION_STORAGE_ID.ordinal());
        }
        else
        {
            Intent i= new Intent(Intent.ACTION_PICK);
            i.setType("image/*");
            startActivityForResult(i,SELEC);
        }

    }
    private void camara()
    {

        if(ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)!= PackageManager.PERMISSION_GRANTED) {
            permisos.requestPermission(this, Manifest.permission.CAMERA, null, permisos.PERMISSION_STORAGE_ID.ordinal());
        }
        else {
            Intent i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if (i.resolveActivity(getPackageManager()) != null) {
                startActivityForResult(i, CAMARA);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        boolean aprob=false;
        if(grantResults.length>0){
            aprob=grantResults[0]==PackageManager.PERMISSION_GRANTED;
        }
        switch (requestCode) {

            case CAMARA:
                if(!aprob)
                    Toast.makeText(this,"Camara Desactivada",Toast.LENGTH_LONG);
                else
                    camara();
                break;
            case SELEC:
                if(!aprob)
                    Toast.makeText(this,"Seleccion Desactivada",Toast.LENGTH_LONG);
                else
                selecPic();
                break;

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case CAMARA:
                if(resultCode==RESULT_OK){
                    Bundle extras = data.getExtras();
                    Bitmap imageBitmap = (Bitmap) extras.get("data");
                    img.setImageBitmap(imageBitmap);
                }
                break;
            case SELEC:
                if(resultCode==RESULT_OK){
                    try {
                        final Uri imageUri = data.getData();
                        final InputStream imageStream = getContentResolver().openInputStream(imageUri);
                        final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                        img.setImageBitmap(selectedImage);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }

                }
                break;

        }


    }



}
