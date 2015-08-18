package com.example.pipedrive.android.personslist.main;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import com.example.pipedrive.android.personslist.data.PersonsContract;
import com.example.pipedrive.android.personslist.data.PersonsDbHelper;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;


import rx.Observable;
import rx.Subscriber;

public class PersonsDataManager {

    private static PersonsDataManager instance;
    private final int LIMIT = 5;
    private int start = 0;
    private PersonsDbHelper personsDbHelper;
    private boolean hasMoreItems;
    private boolean success;
    private Observable<Cursor> cachedObservable;

    private PersonsDataManager(Context context) {
        hasMoreItems = false;
        personsDbHelper = PersonsDbHelper.getInstance(context);
        success = true;

    }

    public static synchronized PersonsDataManager getInstance(Context context) {
        if (instance == null) {
            instance = new PersonsDataManager(context.getApplicationContext());
        }

        return instance;

    }

    public Observable<Cursor> dataObservable() {
        if (cachedObservable == null) {
            cachedObservable =
                    Observable.create(
                            new Observable.OnSubscribe<Cursor>() {
                                @Override
                                public void call(Subscriber<? super Cursor> sub) {
                                    sub.onNext(loadInBackground());
                                    sub.onCompleted();
                                }

                            }
                    ).cache();
        }
        return cachedObservable;
    }

    public synchronized void invalidateCache() {
        cachedObservable = null;
    }

    private String getJsonString(String url) {
        String jsonString = "";
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(url).build();

        try {
            Response response = client.newCall(request).execute();
            jsonString = response.body().string();

        } catch (IOException e) {
            e.printStackTrace();
        }

        return jsonString;
    }

    private synchronized Cursor loadInBackground() {
        personsDbHelper.refreshTable();
        start = 0;

        do {
            try {
                getPersonsDataFromJson(getJsonString(urlString()));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } while (hasMoreItems);

        return success ? personsDbHelper.getPersonCursor(null) : null;
    }


    private String urlString() {

        final String token = "f3fedaf634c3278bd30c7d0deef28707fe2386a2";

        final String PIPEDRIVE_BASE_URL =
                "https://api.pipedrive.com/v1/persons?";
        final String START_PARAM = "start";
        final String LIMIT_PARAM = "limit";
        final String TOKEN_PARAM = "api_token";

        Uri uriBuilt = Uri.parse(PIPEDRIVE_BASE_URL).buildUpon()
                .appendQueryParameter(START_PARAM, Integer.toString(start))
                .appendQueryParameter(LIMIT_PARAM, Integer.toString(LIMIT))
                .appendQueryParameter(TOKEN_PARAM, token)
                .build();

        return uriBuilt.toString();
    }


    private void getPersonsDataFromJson(String jsonString)
            throws JSONException {


        final String PERSONS_DATA = "data";
        final String NAME = "name";
        final String PERSON_PHONE = "phone";
        final String ORGANISATION = "org_id";
        final String PERSON_EMAIL = "email";
        final String PERSON_ID = "id";
        final String VALUE = "value";

        JSONObject personsJson = new JSONObject(jsonString);
        success = personsJson.getBoolean("success");
        if (!success) {
            return;
        }
        //get request extras
        JSONObject additional = personsJson.getJSONObject("additional_data").getJSONObject("pagination");

        //determine whether more results are available
        hasMoreItems = additional.getBoolean("more_items_in_collection");

        if (personsJson.isNull(PERSONS_DATA)) {
            return;
        }

        JSONArray personsArray = personsJson.getJSONArray(PERSONS_DATA);
        for (int i = 0; i < personsArray.length(); i++) {

            // get the JSON object representing a single person
            JSONObject singlePerson = personsArray.getJSONObject(i);

            //check if company is null and get its name if not
            String organisationName = "";

            if (!singlePerson.isNull(ORGANISATION)) {

                JSONObject organisation = singlePerson.getJSONObject(ORGANISATION);
                organisationName = organisation.getString(NAME);
            }


            ContentValues personValues = new ContentValues();

            personValues.put(PersonsContract.PersonsEntry.COLUMN_NAME_PERSON_ID, singlePerson.getInt(PERSON_ID));
            personValues.put(PersonsContract.PersonsEntry.COLUMN_NAME_PERSON_NAME, singlePerson.getString(NAME));
            personValues.put(PersonsContract.PersonsEntry.COLUMN_NAME_COMPANY_NAME, organisationName);

            personsDbHelper.enterPersonInfo(personValues);

            JSONArray phones = singlePerson.getJSONArray(PERSON_PHONE);
            JSONArray emails = singlePerson.getJSONArray(PERSON_EMAIL);


            //get & add phones
            for (int j = 0; j < phones.length(); j++) {
                ContentValues phoneValues = new ContentValues();
                phoneValues.put(PersonsContract.PhonesEntry.COLUMN_NAME_PERSON_ID, singlePerson.getInt(PERSON_ID));
                phoneValues.put(PersonsContract.PhonesEntry.COLUMN_NAME_PHONES, phones.getJSONObject(j).getString(VALUE));
                personsDbHelper.enterPhoneInfo(phoneValues);
            }
            //get & add emails
            for (int k = 0; k < emails.length(); k++) {
                ContentValues emailValues = new ContentValues();
                emailValues.put(PersonsContract.EmailsEntry.COLUMN_NAME_PERSON_ID, singlePerson.getInt(PERSON_ID));
                emailValues.put(PersonsContract.EmailsEntry.COLUMN_NAME_EMAILS, emails.getJSONObject(k).getString(VALUE));
                personsDbHelper.enterEmailInfo(emailValues);
            }

        }
        if (hasMoreItems)
            start += LIMIT;


    }


}
