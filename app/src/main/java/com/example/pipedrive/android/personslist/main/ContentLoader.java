package com.example.pipedrive.android.personslist.main;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.content.AsyncTaskLoader;
import android.util.Log;

import com.example.pipedrive.android.personslist.data.PersonsContract;
import com.example.pipedrive.android.personslist.data.PersonsDbDataAccess;
import com.example.pipedrive.android.personslist.data.PersonsDbHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class ContentLoader extends AsyncTaskLoader<Cursor> {
    private static final String LOG_TAG = "ContentLoader";
    private static PersonsDbHelper personsDbHelper;
    private static boolean hasMoreItems;
    private static int start;
    private static boolean success;
    private Cursor cachedCursor;


    public ContentLoader(Context context) {
        super(context);
        personsDbHelper = PersonsDbHelper.getInstance(getContext());
        hasMoreItems = true;
        start = 0;
    }

    @Override
    public Cursor loadInBackground() {
        if (cachedCursor == null) {
            personsDbHelper.refreshTable(personsDbHelper.getWritableDatabase());
        }
        Cursor readyCursor = null;

        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        String personsJsonStr = null;
        int limit = 50;
        final String token = "f3fedaf634c3278bd30c7d0deef28707fe2386a2";

        //in case there are more than 50 items get & and write all to database in the loop

        while (hasMoreItems) {
            try {

                final String PIPEDRIVE_BASE_URL =
                        "https://api.pipedrive.com/v1/persons?";
                final String START_PARAM = "start";
                final String LIMIT_PARAM = "limit";
                final String TOKEN_PARAM = "api_token";

                Uri uriBuilt = Uri.parse(PIPEDRIVE_BASE_URL).buildUpon()
                        .appendQueryParameter(START_PARAM, Integer.toString(start))
                        .appendQueryParameter(LIMIT_PARAM, Integer.toString(limit))
                        .appendQueryParameter(TOKEN_PARAM, token)
                        .build();


                URL url = new URL(uriBuilt.toString());

                // Create the request
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");//read-only
                urlConnection.connect();

                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {

                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    buffer.append(line);
                }

                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    return null;
                }
                start += limit;
                personsJsonStr = buffer.toString();

            } catch (IOException e) {
                Log.e(LOG_TAG, "Error ", e);
                return null;

            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e(LOG_TAG, "Error closing stream", e);
                    }
                }
            }
            try {

                JSONparser.getPersonsDataFromJson(personsJsonStr);

                if (!success) {
                    return null;
                }
                readyCursor = PersonsDbDataAccess.getPersonCursor(personsDbHelper, null);


            } catch (Exception e) {


                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            }
        }


        return readyCursor;
    }

    @Override
    protected void onStartLoading() {
        //get cached data if available
        if (cachedCursor == null) {
            forceLoad();
        } else {
            super.deliverResult(cachedCursor);
        }
    }

    @Override
    public void deliverResult(Cursor data) {
        //cache data
        cachedCursor = data;
        super.deliverResult(data);
    }

    //class to parse json and fill data
    private static class JSONparser {

        public static void getPersonsDataFromJson(String personsJsonStr)
                throws JSONException {

            // these are the names of the JSON objects that need to be extracted.
            final String PERSONS_DATA = "data";
            final String NAME = "name";
            final String PERSON_PHONE = "phone";
            final String ORGANISATION = "org_id";
            final String PERSON_EMAIL = "email";
            final String PERSON_ID = "id";
            final String VALUE = "value";


            JSONObject personsJson = new JSONObject(personsJsonStr);

            success = personsJson.getBoolean("success");

            if (!success) {
                return;
            }
            //get request extras
            JSONObject additional = personsJson.getJSONObject("additional_data").getJSONObject("pagination");

            //determine whether more results are available
            hasMoreItems = additional.getBoolean("more_items_in_collection");

            //actual person data for the database
            //check for null
            if (personsJson.isNull(PERSONS_DATA)) {
                return;
            }

            JSONArray personsArray = personsJson.getJSONArray(PERSONS_DATA);

            for (int i = 0; i < personsArray.length(); i++) {

                String name;
                String organisationName;
                int id;
                String phoneNumber;
                String emailAddress;

                // get the JSON object representing a single person
                JSONObject singlePerson = personsArray.getJSONObject(i);
                id = singlePerson.getInt(PERSON_ID);

                name = singlePerson.getString(NAME);

                //check if company is null and get its name if not
                if (singlePerson.isNull(ORGANISATION)) {
                    organisationName = "";
                } else {
                    JSONObject organisation = singlePerson.getJSONObject(ORGANISATION);
                    organisationName = organisation.getString(NAME);
                }

                //values to insert into person data table
                ContentValues personValues = new ContentValues();

                personValues.put(PersonsContract.PersonsEntry.COLUMN_NAME_PERSON_ID, id);
                personValues.put(PersonsContract.PersonsEntry.COLUMN_NAME_PERSON_NAME, name);
                personValues.put(PersonsContract.PersonsEntry.COLUMN_NAME_COMPANY_NAME, organisationName);

                JSONArray phones = singlePerson.getJSONArray(PERSON_PHONE);
                JSONArray emails = singlePerson.getJSONArray(PERSON_EMAIL);

                //get & add phones
                for (int j = 0; j < phones.length(); j++) {
                    ContentValues phoneValues = new ContentValues();
                    phoneNumber = phones.getJSONObject(j).getString(VALUE);
                    phoneValues.put(PersonsContract.PhonesEntry.COLUMN_NAME_PERSON_ID, id);
                    phoneValues.put(PersonsContract.PhonesEntry.COLUMN_NAME_PHONES, phoneNumber);
                    PersonsDbDataAccess.enterPhoneInfo(personsDbHelper, phoneValues);
                }
                //get & add emails
                for (int k = 0; k < emails.length(); k++) {
                    ContentValues emailValues = new ContentValues();
                    emailAddress = emails.getJSONObject(k).getString(VALUE);
                    emailValues.put(PersonsContract.EmailsEntry.COLUMN_NAME_PERSON_ID, id);
                    emailValues.put(PersonsContract.EmailsEntry.COLUMN_NAME_EMAILS, emailAddress);
                    PersonsDbDataAccess.enterEmailInfo(personsDbHelper, emailValues);
                }

                PersonsDbDataAccess.enterPersonInfo(personsDbHelper, personValues);

            }


        }


    }


}
