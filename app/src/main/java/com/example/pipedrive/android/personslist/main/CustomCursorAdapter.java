package com.example.pipedrive.android.personslist.main;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.example.pipedrive.android.personslist.R;
import com.example.pipedrive.android.personslist.data.PersonsContract;



    public class CustomCursorAdapter extends CursorAdapter {

        public CustomCursorAdapter(Context context, Cursor c, int flags) {
            super(context, c, flags);
        }

        @Override
        public View newView(Context context, Cursor cursor, ViewGroup parent) {
            return LayoutInflater.from(context).inflate(R.layout.list_view_item, parent, false);
        }

        @Override
        public void bindView(View view, Context context, Cursor cursor) {

            TextView nameView = (TextView) view.findViewById(R.id.list_item_person_view);
            String name = cursor.getString(PersonsContract.ColumnIndexes.PERSON_NAME);
            nameView.setText(name);

            TextView companyView = (TextView) view.findViewById(R.id.list_item_company_view);
            String organisationName = cursor.getString(PersonsContract.ColumnIndexes.ORG_NAME);
            companyView.setText(organisationName);


        }





}
