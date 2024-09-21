package com.example.emptyactivity;



import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

public final class DatabaseHelper extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "Booking.db";

    /* Inner class that defines the table contents */
    public static class BookingEntry implements BaseColumns {
        public static final String TABLE_NAME = "booking";
        public static final String COLUMN_DATE = "date";
        public static final String COLUMN_TIME_SLOT = "time_slot";
        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_DEPARTMENT = "department";
        public static final String COLUMN_PURPOSE = "purpose";
    }

    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + BookingEntry.TABLE_NAME + " (" +
                    BookingEntry._ID + " INTEGER PRIMARY KEY," +
                    BookingEntry.COLUMN_DATE + " TEXT," +
                    BookingEntry.COLUMN_TIME_SLOT + " TEXT," +
                    BookingEntry.COLUMN_NAME + " TEXT," +
                    BookingEntry.COLUMN_DEPARTMENT + " TEXT," +
                    BookingEntry.COLUMN_PURPOSE + " TEXT)";

    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + BookingEntry.TABLE_NAME;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
    }
}
