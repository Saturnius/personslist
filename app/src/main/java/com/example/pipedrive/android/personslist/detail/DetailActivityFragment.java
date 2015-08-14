package com.example.pipedrive.android.personslist.detail;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.pipedrive.android.personslist.R;
import com.example.pipedrive.android.personslist.data.PersonsContract;
import com.example.pipedrive.android.personslist.data.PersonsDbDataAccess;
import com.example.pipedrive.android.personslist.data.PersonsDbHelper;


public class DetailActivityFragment extends Fragment {
    private PersonsDbHelper personsDbHelper;
    private LinearLayout phoneLayout;
    private LinearLayout emailLayout;
    private View rootView;




    public DetailActivityFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Intent intent = getActivity().getIntent();
        String record_id;
        personsDbHelper = PersonsDbHelper.getInstance(getActivity());
        rootView = inflater.inflate(R.layout.fragment_detail, container, false);
        phoneLayout = (LinearLayout) rootView.findViewById(R.id.phones_layout);
        emailLayout = (LinearLayout) rootView.findViewById(R.id.emails_layout);
        if (intent != null && intent.hasExtra(Intent.EXTRA_TEXT)) {
            record_id = intent.getStringExtra(Intent.EXTRA_TEXT);
            getDetailedInfo(record_id);
        }
        return rootView;

    }

    private void getDetailedInfo(String id) {
        //first get person name & company
        Cursor person = PersonsDbDataAccess.getPersonCursor(personsDbHelper, id);

        person.moveToFirst();
        TextView view = (TextView) rootView.findViewById(R.id.person_name);
        view.setText(person.getString(PersonsContract.ColumnIndexes.PERSON_NAME));
        TextView viewOrg = (TextView) rootView.findViewById(R.id.org_name);
        viewOrg.setText(person.getString(PersonsContract.ColumnIndexes.ORG_NAME));

        person.close();


        loadViews(
                //get cursor for phone records
                PersonsDbDataAccess.getPhoneCursor(personsDbHelper, id),
                R.layout.phone_item, phoneLayout);

        loadViews(
                //get cursor for email records
                PersonsDbDataAccess.getEmailCursor(personsDbHelper, id),
                R.layout.email_item, emailLayout);
    }


    private void loadViews(Cursor cursor, int layoutId, LinearLayout layout) {
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            String data = cursor.getString(PersonsContract.ColumnIndexes.CONTACT_DATA);
            if (data.isEmpty()) {
                return;
            }

            View contactView = LayoutInflater.from(getActivity()).inflate(layoutId, layout, false);
            TextView tv = (TextView)contactView.findViewById(R.id.item_text_view);
            tv.setText(data);
            layout.addView(contactView);

            cursor.moveToNext();
        }
        cursor.close();

    }
}
