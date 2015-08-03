package com.example.pipedrive.android.personslist.detail;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.pipedrive.android.personslist.R;
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

    public void getDetailedInfo(String id) {
        //first get person name & company
        Cursor person = PersonsDbDataAccess.getPersonCursor(personsDbHelper, id);

        person.moveToFirst();
        TextView view = (TextView) rootView.findViewById(R.id.person_name);
        view.setText(person.getString(2));
        TextView viewOrg = (TextView) rootView.findViewById(R.id.org_name);
        viewOrg.setText(person.getString(3));

        person.close();


        loadViews(
                //get cursor for phone records
                PersonsDbDataAccess.getPhoneCursor(personsDbHelper, id),
                R.drawable.ic_action_phone_start, phoneLayout);

        loadViews(
                //get cursor for email records
                PersonsDbDataAccess.getEmailCursor(personsDbHelper, id),
                R.drawable.ic_action_mail, emailLayout);
    }


    public void loadViews(Cursor cursor, int id, LinearLayout layout) {
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            String data = cursor.getString(2);
            if (data.isEmpty()) {
                return;
            }

            // create linear layout and add views
            LinearLayout subLayout = new LinearLayout(getActivity());
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

            layoutParams.setMargins(16, 16, 16, 8);

            //create and load views
            ImageView img = new ImageView(getActivity());
            img.setImageDrawable(getResources().getDrawable(id));
            img.setContentDescription(getResources().getString(R.string.logo));

            TextView contactView = new TextView(getActivity());
            contactView.setText(data);
            contactView.setPadding(0, 25, 0, 0);
            img.setPadding(0, 0, 25, 0);

            //add views to the sublayout
            subLayout.addView(img);
            subLayout.addView(contactView);
            subLayout.setBackgroundColor(getResources().getColor(R.color.white_bg));

            //add newly created layout to a specific layout passed as an argument
            layout.addView(subLayout, layoutParams);
            cursor.moveToNext();
        }
        cursor.close();

    }
}
