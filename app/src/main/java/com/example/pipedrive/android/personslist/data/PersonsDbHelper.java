package com.example.pipedrive.android.personslist.data;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;


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

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(sqlCommands.SQL_CREATE_PERSON_ENTRIES);
        db.execSQL(sqlCommands.SQL_CREATE_PHONE_ENTRIES);
        db.execSQL(sqlCommands.SQL_CREATE_EMAIL_ENTRIES);

    }

    public void deleteAllTables(SQLiteDatabase db) {
        db.execSQL(sqlCommands.SQL_DELETE_PERSON_ENTRIES);
        db.execSQL(sqlCommands.SQL_DELETE_PHONE_ENTRIES);
        db.execSQL(sqlCommands.SQL_DELETE_EMAIL_ENTRIES);

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

    //testing method to log out the db
    public String getTableAsString(SQLiteDatabase db) {
        String tableString = String.format("Table %s:\n", PersonsContract.PersonsEntry.TABLE_NAME);
        Cursor allRows = db.rawQuery("SELECT * FROM " + PersonsContract.PersonsEntry.TABLE_NAME, null);
        if (allRows.moveToFirst()) {
            String[] columnNames = allRows.getColumnNames();
            do {
                for (String name : columnNames) {
                    tableString += String.format("%s: %s\n", name,
                            allRows.getString(allRows.getColumnIndex(name)));
                }
                tableString += "\n";

            } while (allRows.moveToNext());
        }

        return tableString;
    }

    //repopulate table
    public void refreshTable(SQLiteDatabase db) {
        if (db != null) {
            deleteAllTables(db);
            onCreate(db);
        }
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
    }


}
