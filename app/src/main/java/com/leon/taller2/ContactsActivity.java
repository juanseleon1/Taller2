package com.leon.taller2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.widget.ListView;
import android.widget.Toast;

public class ContactsActivity extends AppCompatActivity {

    Cursor cursorCont;
    ContacAdapter adaptador;
    String [] dbAc;
    ListView lContac;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts);
        lContac=findViewById(R.id.listCont);
        requestPermission(this, Manifest.permission.READ_CONTACTS, "Es necesario para mostrar los contactos", permisos.PERMISSION_CONTACTS_ID.ordinal() );
        dbAc= new String[]{ContactsContract.Profile._ID,ContactsContract.Profile.DISPLAY_NAME_PRIMARY};
        adaptador= new ContacAdapter(this,null,0);
        lContac.setAdapter(adaptador);
        cargarContactos();
    }

    public void cargarContactos()
    {
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS)== PackageManager.PERMISSION_GRANTED){
            cursorCont=getContentResolver().query(ContactsContract.Contacts.CONTENT_URI, dbAc, null, null, null);
            adaptador.changeCursor(cursorCont);
        }

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
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        cargarContactos();
    }
}
