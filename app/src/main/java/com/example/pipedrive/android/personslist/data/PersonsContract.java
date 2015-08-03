package com.example.pipedrive.android.personslist.data;


import android.provider.BaseColumns;

public class PersonsContract {

    private PersonsContract() {
    }

    public static abstract class PersonsEntry implements BaseColumns {
        public static final String TABLE_NAME = "persons_data";
        public static final String COLUMN_NAME_PERSON_ID = "person_id";
        public static final String COLUMN_NAME_PERSON_NAME = "person_name";
        public static final String COLUMN_NAME_COMPANY_NAME = "org_name";

    }

    public static abstract class PhonesEntry implements BaseColumns {
        public static final String TABLE_NAME = "phones";
        public static final String COLUMN_NAME_PERSON_ID = "person_id";
        public static final String COLUMN_NAME_PHONES = "phone_number";

    }

    public static abstract class EmailsEntry implements BaseColumns {
        public static final String TABLE_NAME = "emails";
        public static final String COLUMN_NAME_PERSON_ID = "person_id";
        public static final String COLUMN_NAME_EMAILS = "email";

    }

}
