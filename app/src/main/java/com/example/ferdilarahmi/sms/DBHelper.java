package com.example.ferdilarahmi.sms;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by Ferdila Rahmi on 9/6/2016.
 */
public class DBHelper extends SQLiteOpenHelper {
    private final static String TAG = "DBHelper";
    private final Context context;
    private static final String DB_NAME = "smsdb";
    private static final int DB_VERSION = 1;
    private static String DB_PATH = "";
    SQLiteDatabase db;

    public DBHelper(Context context){
        super(context, DB_NAME, null, DB_VERSION);
        this.context = context;
        this.DB_PATH = ("data/data/" + context.getPackageName() + "/databases/"+DB_NAME);
    }
    public boolean prepareDatabase() throws Exception {
        boolean dbExist = checkDatabase();
        if(dbExist) {// HOW CAN IT BE ALWAYS TRUEEE ARGHHHH. SOLVED o:)
            db = this.getWritableDatabase();//IT SHOULD BE ALWAYS AFTER check exist
            Log.i("Info","DATABASE HAS EXISTED");
        } else {
            db = this.getWritableDatabase();//IT SHOULD BE ALWAYS AFTER check exist
            try {
                copyDatabase();
                Log.i("Info","YOUR FIRST TIME - COPIED DATABASE SUCCESS");
            } catch (Exception e){
                Log.e(TAG,e.toString());
            }
        }
        return dbExist;
    }
    private boolean checkDatabase() {
        boolean checkDB = false;
        try {
            File file = new File(DB_PATH);
            checkDB = file.exists();
        } catch(SQLiteException e) {
            Log.d(TAG, e.getMessage());
        }
        return checkDB;
    }
    public void resetDB() throws Exception {
        db = this.getWritableDatabase();//IT SHOULD BE ALWAYS AFTER check exist
        try {
            copyDatabase();
            Log.i("Info","COPIED DATABASE SUCCESS");
        } catch (Exception e){
            Log.e(TAG,e.toString());
        }
    }
    private void copyDatabase() throws Exception {
        OutputStream os = new FileOutputStream(DB_PATH);
        InputStream is = context.getAssets().open(DB_NAME);
        byte[] buffer = new byte[1024];
        int length;
        while ((length = is.read(buffer)) > 0) {
            os.write(buffer, 0, length);
        }
        is.close();
        os.flush();
        os.close();
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d(TAG, "onCreate");
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }
    public void openDatabase(){
        if(db != null && db.isOpen()){
            return;
        }
        db = SQLiteDatabase.openDatabase(DB_PATH, null, SQLiteDatabase.OPEN_READWRITE);
//        db =  SQLiteDatabase.openDatabase(dbPath, null, SQLiteDatabase.OPEN_READONLY);
    }
    public void closeDatabase(){
        if(db != null){
            db.close();
        }
    }
    //////////////////////////////////////////////////////////////////////////////////////////
    public Cursor getAllData(){
        openDatabase();
        Cursor res = db.rawQuery("SELECT * FROM SPAM_FILTER", null);
        if(res!=null){
            res.moveToFirst();
        }
        closeDatabase();
        return res;
    }
    public Cursor getStopwordByTerm(String term){
        openDatabase();
        Cursor res = db.rawQuery("SELECT * FROM STOPWORD WHERE STOPWORD=\""+ term +"\"", null);
        if(res!=null){
            res.moveToFirst();
        }
        closeDatabase();
        return res;
    }
    public Cursor getSynonymByTerm(String term){
        openDatabase();
        Cursor res = db.rawQuery("SELECT * FROM SYNONYM WHERE KATA_INPUT=\""+ term +"\"", null);
        if(res!=null){
            res.moveToFirst();
        }
        closeDatabase();
        return res;
    }
    //testing
    public Cursor getProbModel(String term, String data_model){
        openDatabase();
        String query = "SELECT * FROM "+ data_model +" WHERE TERM=\""+ term +"\"";
        Cursor res = db.rawQuery(query, null);
        if(res!=null){
            res.moveToFirst();
        }
        closeDatabase();
        return res;
    }
    ////////////filtering
    public boolean insertSpam(int _id, int spam){
        openDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("id_inbox", _id);
        contentValues.put("spam", spam);
        long result = db.insert("spam_filter", null, contentValues);
        if(result == -1){
            closeDatabase();
            return false;
        }else{
            closeDatabase();
            return true;
        }
    }
    public Cursor getSpam(int spam){
        openDatabase();
        Cursor res = db.rawQuery("SELECT * FROM SPAM_FILTER WHERE SPAM="+spam, null);
        if(res!=null){
            res.moveToFirst();
        }
        closeDatabase();
        return res;
    }
}
