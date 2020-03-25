package com.leon.taller2;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.widget.ListView;

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
        requestPermission(this, Manifest.permission.READ_CONTACTS, "Es necesario para mostrar los contactos", permisos.PERMISSION_CONTACTS_ID );
        dbAc= new String[]{ContactsContract.Profile._ID,ContactsContract.Profile.DISPLAY_NAME_PRIMARY};
        adaptador= new ContacAdapter(this,null,0);
        lContac.setAdapter(adaptador);
    }
}
