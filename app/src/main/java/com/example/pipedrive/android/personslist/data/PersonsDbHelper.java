package com.example.pipedrive.android.personslist.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class PersonsDbHelper extends SQLiteOpenHelper {


    public static final String DATABASE_NAME = "persons_list.db";
    public static final int DATABASE_VERSION = 1;
    private static PersonsDbHelper instance;

    private PersonsDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);

    }

    //using singleton
    public static synchronized PersonsDbHelper getInstance(Context context) {
        if (instance == null) {
            instance = new PersonsDbHelper(context.getApplicationContext());
        }

        return instance;
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

        return sq.rawQuery(sql, null);


    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(sqlCommands.SQL_CREATE_PERSON_ENTRIES);
        db.execSQL(sqlCommands.SQL_CREATE_PHONE_ENTRIES);
        db.execSQL(sqlCommands.SQL_CREATE_EMAIL_ENTRIES);

    }

    private void deleteAllTables(SQLiteDatabase db) {
        db.execSQL(sqlCommands.SQL_DELETE_PERSON_ENTRIES);
        db.execSQL(sqlCommands.SQL_DELETE_PHONE_ENTRIES);
        db.execSQL(sqlCommands.SQL_DELETE_EMAIL_ENTRIES);

    }

    private void deleteTablesData() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL(sqlCommands.SQL_DELETE_PERSON_DATA);
        db.execSQL(sqlCommands.SQL_DELETE_PHONE_DATA);
        db.execSQL(sqlCommands.SQL_DELETE_EMAIL_DATA);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        deleteAllTables(db);
        onCreate(db);

    }

    @Override
    public synchronized void close() {
        super.close();
    }


    //repopulate table
    public void refreshTable() {
        SQLiteDatabase db = this.getWritableDatabase();
        if (db != null) {
            deleteTablesData();
        }
    }

    public void enterPersonInfo(ContentValues contentValues) {

        SQLiteDatabase sq = this.getWritableDatabase();

        sq.insert(
                PersonsContract.PersonsEntry.TABLE_NAME,
                null,
                contentValues);
    }


    public void enterPhoneInfo(ContentValues contentValues) {

        SQLiteDatabase sq = this.getWritableDatabase();

        sq.insert(
                PersonsContract.PhonesEntry.TABLE_NAME,
                null,
                contentValues);
    }

    public void enterEmailInfo(ContentValues contentValues) {

        SQLiteDatabase sq = this.getWritableDatabase();

        sq.insert(
                PersonsContract.EmailsEntry.TABLE_NAME,
                null,
                contentValues);
    }

    public Cursor getPersonCursor(String id) {
        String WHERE;
        if (id == null) {
            WHERE = null;
        } else {
            WHERE = "person_id" + "=" + id;
        }
        SQLiteDatabase sq = this.getReadableDatabase();
        String sortOrder = "LOWER (" +
                PersonsContract.PersonsEntry.COLUMN_NAME_PERSON_NAME + ") ASC";
        String[] columns = {PersonsContract.PersonsEntry._ID, PersonsContract.PersonsEntry.COLUMN_NAME_PERSON_ID, PersonsContract.PersonsEntry.COLUMN_NAME_PERSON_NAME, PersonsContract.PersonsEntry.COLUMN_NAME_COMPANY_NAME};
        return sq.query(PersonsContract.PersonsEntry.TABLE_NAME, columns, WHERE, null, null, null, sortOrder);

    }


    public Cursor getPhoneCursor(String id) {
        String WHERE = "person_id" + "=" + id;
        String sortOrder = "LOWER (" +
                PersonsContract.PhonesEntry.COLUMN_NAME_PHONES + ") ASC";
        SQLiteDatabase sq = this.getReadableDatabase();
        String[] columns = {PersonsContract.PhonesEntry._ID, PersonsContract.PhonesEntry.COLUMN_NAME_PERSON_ID, PersonsContract.PhonesEntry.COLUMN_NAME_PHONES};
        return sq.query(PersonsContract.PhonesEntry.TABLE_NAME, columns, WHERE, null, null, null, sortOrder);

    }


    public Cursor getEmailCursor(String id) {
        String WHERE = "person_id" + "=" + id;
        String sortOrder = "LOWER (" +
                PersonsContract.EmailsEntry.COLUMN_NAME_EMAILS + ") ASC";
        SQLiteDatabase sq = this.getReadableDatabase();
        String[] columns = {PersonsContract.EmailsEntry._ID, PersonsContract.EmailsEntry.COLUMN_NAME_PERSON_ID, PersonsContract.EmailsEntry.COLUMN_NAME_EMAILS};
        return sq.query(PersonsContract.EmailsEntry.TABLE_NAME, columns, WHERE, null, null, null, sortOrder);

    }

    static class sqlCommands {
        public static final String TEXT_TYPE = " TEXT";
        public static final String COMMA_SEP = ", ";
        //creation
        public static final String SQL_CREATE_PERSON_ENTRIES =
                "CREATE TABLE " + PersonsContract.PersonsEntry.TABLE_NAME + " (" +
                        PersonsContract.PersonsEntry._ID + " INTEGER PRIMARY KEY, " +
                        PersonsContract.PersonsEntry.COLUMN_NAME_PERSON_ID + " INTEGER NOT NULL" + COMMA_SEP +
                        PersonsContract.PersonsEntry.COLUMN_NAME_PERSON_NAME + TEXT_TYPE + COMMA_SEP +
                        PersonsContract.PersonsEntry.COLUMN_NAME_COMPANY_NAME + TEXT_TYPE + " );";

        public static final String SQL_CREATE_PHONE_ENTRIES =
                "CREATE TABLE " + PersonsContract.PhonesEntry.TABLE_NAME + " (" +
                        PersonsContract.PhonesEntry._ID + " INTEGER PRIMARY KEY " + COMMA_SEP +
                        PersonsContract.PhonesEntry.COLUMN_NAME_PERSON_ID + " INTEGER NOT NULL " + COMMA_SEP +
                        PersonsContract.PhonesEntry.COLUMN_NAME_PHONES + TEXT_TYPE + " );";

        public static final String SQL_CREATE_EMAIL_ENTRIES =
                "CREATE TABLE " + PersonsContract.EmailsEntry.TABLE_NAME + " (" +
                        PersonsContract.EmailsEntry._ID + " INTEGER PRIMARY KEY, " +
                        PersonsContract.EmailsEntry.COLUMN_NAME_PERSON_ID + " INTEGER NOT NULL" + COMMA_SEP +
                        PersonsContract.EmailsEntry.COLUMN_NAME_EMAILS + TEXT_TYPE + " );";
        //deletion
        private static final String SQL_DELETE_PERSON_ENTRIES =
                "DROP TABLE IF EXISTS " + PersonsContract.PersonsEntry.TABLE_NAME;
        private static final String SQL_DELETE_PHONE_ENTRIES =
                "DROP TABLE IF EXISTS " + PersonsContract.PhonesEntry.TABLE_NAME;
        private static final String SQL_DELETE_EMAIL_ENTRIES =
                "DROP TABLE IF EXISTS " + PersonsContract.EmailsEntry.TABLE_NAME;

        private static final String SQL_DELETE_PERSON_DATA =
                "DELETE FROM " + PersonsContract.PersonsEntry.TABLE_NAME;
        private static final String SQL_DELETE_PHONE_DATA =
                "DELETE FROM " + PersonsContract.PhonesEntry.TABLE_NAME;
        private static final String SQL_DELETE_EMAIL_DATA =
                "DELETE FROM " + PersonsContract.EmailsEntry.TABLE_NAME;
    }


}
