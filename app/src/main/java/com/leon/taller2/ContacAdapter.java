package com.leon.taller2;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

public class ContacAdapter extends CursorAdapter {
    public ContacAdapter(Context context, Cursor c, int flags) {
        super(context, c,flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.lcontac, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TextView idContacto= view.findViewById(R.id.idcont);
        TextView nomContacto=view.findViewById(R.id.nomcont);
        int id=cursor.getInt(0);
        String nom=cursor.getString(1);
        idContacto.setText(""+id);
        nomContacto.setText(nom);
    }
}
