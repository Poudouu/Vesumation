package com.vesuvius.arlau.vesumation;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by criqulau on 23/12/2015.
 * Database manipulation helper class.
 */
public class UserDbHelper extends SQLiteOpenHelper {

    // Create a databasefor the application if it doesn't exist or edit the actual one.
    // The database created/ modified is directly linked with the DATABASE_VERSION parameter.
    private static final String DATABASE_NAME = "TAGINFOO.DB";
    private static final int DATABASE_VERSION = 2;
    // !!!!!!! The query string has to be formated like this with space at the correct positions!!!!
    private static final String CREATE_QUERY =
            "CREATE TABLE " + DatabasContainer.NewDatabasInfos.TABLE_NAME + "(" +DatabasContainer.NewDatabasInfos.VIEW_ID + " INTEGER," + DatabasContainer.NewDatabasInfos.DATA_TYPE + " INTEGER," +
                    DatabasContainer.NewDatabasInfos.DB_TYPE + " INTEGER," + DatabasContainer.NewDatabasInfos.BYTE_M + " TEXT," +
                    DatabasContainer.NewDatabasInfos.BIT_M + " TEXT," + DatabasContainer.NewDatabasInfos.DB_NUM + " TEXT," +
                    DatabasContainer.NewDatabasInfos.DB_BYTE + " TEXT," + DatabasContainer.NewDatabasInfos.DB_BIT + " TEXT);";

    public UserDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        Log.e("DATABASE OPERATION", "Database created/ opened...");
    }

    @Override
    // Call if the database version doesn't exist only.
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_QUERY);
        Log.e("DATABASE OPERATION", "Table created...");
    }

    // Function to add a row in the database.
    public void addInformations(int Data_type, int BD_type, String Byte_M, String Bit_M, String DB_num, String DB_byte, String DB_bit, SQLiteDatabase db, int View_id) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(DatabasContainer.NewDatabasInfos.VIEW_ID, View_id);
        contentValues.put(DatabasContainer.NewDatabasInfos.DATA_TYPE, Data_type);
        contentValues.put(DatabasContainer.NewDatabasInfos.DB_TYPE, BD_type);
        contentValues.put(DatabasContainer.NewDatabasInfos.BYTE_M, Byte_M);
        contentValues.put(DatabasContainer.NewDatabasInfos.BIT_M, Bit_M);
        contentValues.put(DatabasContainer.NewDatabasInfos.DB_NUM, DB_num);
        contentValues.put(DatabasContainer.NewDatabasInfos.DB_BYTE, DB_byte);
        contentValues.put(DatabasContainer.NewDatabasInfos.DB_BIT, DB_bit);
        db.insert(DatabasContainer.NewDatabasInfos.TABLE_NAME, null, contentValues);

        Log.e("DATABASE OPERATION", "One row inserted...");
    }

    public Cursor getInformations(SQLiteDatabase db)
    {
        // Function to get all the infos in the database, return a cursor with all the info -> To parse.
        Cursor cursor;
        String[] projections ={DatabasContainer.NewDatabasInfos.VIEW_ID,DatabasContainer.NewDatabasInfos.DATA_TYPE,DatabasContainer.NewDatabasInfos.DB_TYPE,DatabasContainer.NewDatabasInfos.BYTE_M,DatabasContainer.NewDatabasInfos.BIT_M,DatabasContainer.NewDatabasInfos.DB_NUM,DatabasContainer.NewDatabasInfos.DB_BYTE,DatabasContainer.NewDatabasInfos.DB_BIT};
        cursor=db.query(DatabasContainer.NewDatabasInfos.TABLE_NAME,projections,null, null, null, null, null);
        return cursor;
    }

    public void deleteInfo(int view_id, SQLiteDatabase db){

        // Function to delete a row with a particular id in the database -> Search on the id in the id column and delete.
        String selection=DatabasContainer.NewDatabasInfos.VIEW_ID+" LIKE ?";
        String [] selection_args={view_id+""};
        db.delete(DatabasContainer.NewDatabasInfos.TABLE_NAME, selection,selection_args);
        Log.e("DATABASE OPERATION", "One row deleted...");
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

}



