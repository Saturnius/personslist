package com.example.pipedrive.android.personslist.data;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

//inserts and reads actual data
public class PersonsDbDataAccess {




    public static void enterPersonInfo(PersonsDbHelper personsDbHelper, ContentValues contentValues) {

        SQLiteDatabase sq = personsDbHelper.getWritableDatabase();

        long newRowId = sq.insert(
                PersonsContract.PersonsEntry.TABLE_NAME,
                null,
                contentValues);
    }


    public static void enterPhoneInfo(PersonsDbHelper personsDbHelper, ContentValues contentValues) {

        SQLiteDatabase sq = personsDbHelper.getWritableDatabase();

       long newRowId = sq.insert(
                PersonsContract.PhonesEntry.TABLE_NAME,
                null,
                contentValues);
    }

    public static void enterEmailInfo(PersonsDbHelper personsDbHelper, ContentValues contentValues) {

        SQLiteDatabase sq = personsDbHelper.getWritableDatabase();

        long newRowId = sq.insert(
                PersonsContract.EmailsEntry.TABLE_NAME,
                null,
                contentValues);
    }

    public static Cursor getPersonCursor(PersonsDbHelper personsDbHelper, String id) {
        String WHERE;
        if (id == null) {
            WHERE = null;
        } else {
            WHERE = "person_id" + "=" + id;
        }
        SQLiteDatabase sq = personsDbHelper.getReadableDatabase();
        String sortOrder = "LOWER (" +
                PersonsContract.PersonsEntry.COLUMN_NAME_PERSON_NAME + ") ASC";
        String[] columns = {PersonsContract.PersonsEntry._ID, PersonsContract.PersonsEntry.COLUMN_NAME_PERSON_ID, PersonsContract.PersonsEntry.COLUMN_NAME_PERSON_NAME, PersonsContract.PersonsEntry.COLUMN_NAME_COMPANY_NAME};
        Cursor cursor = sq.query(PersonsContract.PersonsEntry.TABLE_NAME, columns, WHERE, null, null, null, sortOrder);
        return cursor;
    }


    public static Cursor getPhoneCursor(PersonsDbHelper personsDbHelper, String id) {
        String WHERE = "person_id" + "=" + id;
        String sortOrder = "LOWER (" +
                PersonsContract.PhonesEntry.COLUMN_NAME_PHONES + ") ASC";
        SQLiteDatabase sq = personsDbHelper.getReadableDatabase();
        String[] columns = {PersonsContract.PhonesEntry._ID, PersonsContract.PhonesEntry.COLUMN_NAME_PERSON_ID, PersonsContract.PhonesEntry.COLUMN_NAME_PHONES};
        Cursor cursor = sq.query(PersonsContract.PhonesEntry.TABLE_NAME, columns, WHERE, null, null, null, sortOrder);
        return cursor;
    }


    public static Cursor getEmailCursor(PersonsDbHelper personsDbHelper, String id) {
        String WHERE = "person_id" + "=" + id;
        String sortOrder = "LOWER (" +
                PersonsContract.EmailsEntry.COLUMN_NAME_EMAILS + ") ASC";
        SQLiteDatabase sq = personsDbHelper.getReadableDatabase();
        String[] columns = {PersonsContract.EmailsEntry._ID, PersonsContract.EmailsEntry.COLUMN_NAME_PERSON_ID, PersonsContract.EmailsEntry.COLUMN_NAME_EMAILS};
        Cursor cursor = sq.query(PersonsContract.EmailsEntry.TABLE_NAME, columns, WHERE, null, null, null, sortOrder);
        return cursor;
    }


    //full data query not in use
    public static Cursor getContactData(PersonsDbHelper personsDbHelper, String id) {
        SQLiteDatabase sq = personsDbHelper.getReadableDatabase();
        String sql = "SELECT " + PersonsContract.PersonsEntry.TABLE_NAME + "." + PersonsContract.PersonsEntry.COLUMN_NAME_PERSON_ID + ", " +
                PersonsContract.PersonsEntry.COLUMN_NAME_PERSON_NAME + ", " +
                PersonsContract.PersonsEntry.COLUMN_NAME_COMPANY_NAME + ", " + PersonsContract.PhonesEntry.COLUMN_NAME_PHONES +
                ", " + PersonsContract.EmailsEntry.COLUMN_NAME_EMAILS +
                " FROM " + PersonsContract.PersonsEntry.TABLE_NAME +
                " LEFT JOIN " + PersonsContract.PhonesEntry.TABLE_NAME +
                " ON " + PersonsContract.PersonsEntry.TABLE_NAME + "." + PersonsContract.PersonsEntry.COLUMN_NAME_PERSON_ID +
                " = " + PersonsContract.PhonesEntry.TABLE_NAME + "." + PersonsContract.PhonesEntry.COLUMN_NAME_PERSON_ID +

                " LEFT JOIN " + PersonsContract.EmailsEntry.TABLE_NAME +
                " ON " + PersonsContract.PersonsEntry.TABLE_NAME + "." + PersonsContract.PersonsEntry.COLUMN_NAME_PERSON_ID +
                " = " + PersonsContract.EmailsEntry.TABLE_NAME + "." + PersonsContract.EmailsEntry.COLUMN_NAME_PERSON_ID +
                " WHERE " + PersonsContract.PersonsEntry.TABLE_NAME + "." + PersonsContract.PersonsEntry.COLUMN_NAME_PERSON_ID + "=" + id + ";";

        Cursor cursor = sq.rawQuery(sql, null);
        return cursor;

    }
}
