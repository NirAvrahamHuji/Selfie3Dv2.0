package com.javahelps.com.myapplication;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;


class DatabaseAccess {
    private SQLiteOpenHelper openHelper;
    private SQLiteDatabase database;
    private static DatabaseAccess instance;

    /**
     * Private constructor to avoid object creation from outside classes.
     */
    private DatabaseAccess(Context context) {
        this.openHelper = new DatabaseOpenHelper(context);
    }

    /**
     * Return a singleton instance of DatabaseAccess.
     *
     * @param context the Context
     * @return the instance of DabaseAccess
     */
    static DatabaseAccess getInstance(Context context) {
        if (instance == null) {
            instance = new DatabaseAccess(context);
        }
        return instance;
    }

    /**
     * Open the database connection.
     */
    void open() {
        this.database = openHelper.getWritableDatabase();
    }

    /**
     * Close the database connection.
     */
    void close() {
        if (database != null) {
            this.database.close();
        }
    }

    /**
     * Read all quotes from the database.
     *
     * @return a List of quotes
     */
    List<Descriptor> exeDescriptorsQuery(String query_str) {
        List<Descriptor> list = new ArrayList<>();
        Cursor cursor = database.rawQuery(query_str, null);
        cursor.moveToFirst();

        while (!cursor.isAfterLast()) {
            Descriptor desc = new Descriptor();
            desc.setID(Integer.parseInt(cursor.getString(0)));
            desc.setCol(Integer.parseInt(cursor.getString(1)));
            desc.setRow(Integer.parseInt(cursor.getString(2)));
            desc.setDesc(MatSerializer.string2Mat(cursor.getString(3),true));
            list.add(desc);
            cursor.moveToNext();
        }
        cursor.close();
        return list;
    }

    /**
     * Read all quotes from the database.
     *
     * @return a List of quotes
     */
    DepthPatch exeDepthPatchesQuery(String query_str) {
        Cursor cursor = database.rawQuery(query_str, null);
        cursor.moveToFirst();

        DepthPatch dp = new DepthPatch();
        dp.setID(Integer.parseInt(cursor.getString(0)));
        dp.setCol(Integer.parseInt(cursor.getString(1)));
        dp.setRow(Integer.parseInt(cursor.getString(2)));
        dp.setDP(MatSerializer.string2Mat(cursor.getString(3),false));

        cursor.close();
        return dp;
    }
}