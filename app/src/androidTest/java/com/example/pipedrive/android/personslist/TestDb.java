package com.example.pipedrive.android.personslist;


import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.test.AndroidTestCase;

import com.example.pipedrive.android.personslist.data.PersonsContract;
import com.example.pipedrive.android.personslist.data.PersonsDbHelper;

public class TestDb extends AndroidTestCase {


    @Override
    protected void setUp() throws Exception {
        mContext.deleteDatabase(PersonsDbHelper.DATABASE_NAME);
    }
    //test insertion into persons table
    public void testPersonsTable() throws Throwable {

        SQLiteDatabase db = PersonsDbHelper.getInstance(
                this.mContext).getWritableDatabase();
        assertEquals(true, db.isOpen());

        ContentValues testValues = new ContentValues();
        testValues.put(PersonsContract.PersonsEntry.COLUMN_NAME_PERSON_ID, "2");
        testValues.put(PersonsContract.PersonsEntry.COLUMN_NAME_PERSON_NAME, "Jon Snow");
        testValues.put(PersonsContract.PersonsEntry.COLUMN_NAME_COMPANY_NAME, "Stark Ltd.");

        long locationRowId;
        locationRowId = db.insert(PersonsContract.PersonsEntry.TABLE_NAME, null, testValues);

        // verify we got a row back.
        assertTrue(locationRowId != -1);

        // query the database and receive a cursor back

        Cursor cursor = db.query(
                PersonsContract.PersonsEntry.TABLE_NAME, null, null, null, null, null, null);

        // move the cursor to a valid database row
        assertTrue("Error: No Records returned from location query", cursor.moveToFirst());

        //verify columns data
        assertTrue("Error: wrong info in the column 0", cursor.getInt(PersonsContract.ColumnIndexes._ID) == 1);
        assertTrue("Error: wrong info in the column 1", cursor.getInt(PersonsContract.ColumnIndexes.PERSON_ID) == 2);
        assertTrue("Error: wrong info in the column 2", cursor.getString(PersonsContract.ColumnIndexes.PERSON_NAME).equals("Jon Snow"));
        assertTrue("Error: wrong info in the column 3", cursor.getString(PersonsContract.ColumnIndexes.ORG_NAME).equals("Stark Ltd."));
        assertTrue("Error: wrong type of info in the column 0", cursor.getType(PersonsContract.ColumnIndexes._ID) == Cursor.FIELD_TYPE_INTEGER );
        assertTrue("Error: wrong type of info in the column 1", cursor.getType(PersonsContract.ColumnIndexes.PERSON_ID) == Cursor.FIELD_TYPE_INTEGER );
        assertTrue("Error: wrong type of info in the column 2", cursor.getType(PersonsContract.ColumnIndexes.PERSON_NAME) == Cursor.FIELD_TYPE_STRING );
        assertTrue("Error: wrong type of info in the column 3", cursor.getType(PersonsContract.ColumnIndexes.ORG_NAME) == Cursor.FIELD_TYPE_STRING );

        assertFalse("Error: More than one record returned from location query", cursor.moveToNext());
        cursor.close();
        db.close();
    }
}
