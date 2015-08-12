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

//custom adapter to load views from layout

public class CustomCursorAdapter extends CursorAdapter {

    public CustomCursorAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_view_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        view.setTag(viewHolder);

        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ViewHolder viewHolder = (ViewHolder) view.getTag();
        // Read data from cursor
        String name = cursor.getString(PersonsContract.ColumnIndexes.PERSON_NAME);
        viewHolder.nameView.setText(name);
        String organisationName = cursor.getString(PersonsContract.ColumnIndexes.ORG_NAME);
        viewHolder.companyView.setText(organisationName);

    }

    public static class ViewHolder {
        public final TextView nameView;
        public final TextView companyView;

        public ViewHolder(View view) {
            nameView = (TextView) view.findViewById(R.id.list_item_person_view);
            companyView = (TextView) view.findViewById(R.id.list_item_company_view);


        }

    }




}
