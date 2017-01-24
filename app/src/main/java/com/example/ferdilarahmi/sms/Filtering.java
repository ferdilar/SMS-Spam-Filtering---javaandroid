package com.example.ferdilarahmi.sms;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.StringBuilderPrinter;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Created by Ferdila Rahmi on 9/14/2016.
 */
public class Filtering extends AppCompatActivity {
    Context context;
    DBHelper db_helper;
    SharedPreferences sharedPreferences;
    Filtering(Context context){
        super();
        this.context = context;
        db_helper = new DBHelper(context);
        sharedPreferences = context.getSharedPreferences("sms_spam",MODE_PRIVATE);
    }
    public void filterAll() throws Exception {
        String last_id="0";
        Cursor c_inbox = context.getContentResolver().query(Uri.parse("content://sms/inbox"), new String[]{"_id","body"}, null, null, "_id asc");

        if(c_inbox.moveToFirst()){
            testing(c_inbox.getString(0), c_inbox.getString(1));
    //        Toast.makeText(context, c_inbox.getString(1), Toast.LENGTH_SHORT).show();
            last_id = c_inbox.getString(0);
//            saveLastID(last_id);//antisipasi close watu launching pertama
            while(c_inbox.moveToNext()) {
                testing(c_inbox.getString(0), c_inbox.getString(1));
                last_id = c_inbox.getString(0);
//                saveLastID(last_id);
            }
        }
        c_inbox.close();
        saveLastID(last_id);//setiap filter selanjutnya jg
    }
    public void testing(String _id, String body) throws Exception {
        String data_after = "";
        boolean nbc;
        ArrayList<String> arl_term_testing = new ArrayList<String>();

        ProCleansing pro_cleansing = new ProCleansing();
        ProTokenizing pro_tokenizing = new ProTokenizing(context);

        data_after = pro_cleansing.caseFolding(body);
        data_after = pro_cleansing.removeChar(data_after);

        //proses tokenzing terdapat filtering, stemming return nilai array list
        arl_term_testing = pro_tokenizing.tokenizing(data_after);
        data_after="";
        for(int j=0;j<arl_term_testing.size();j++){
            data_after = data_after+" "+arl_term_testing.get(j);
        }
        NaiveBayes naive_bayes = new NaiveBayes();

        nbc = naive_bayes.naiveBayesClass(arl_term_testing,"model1",context);

        int spam_filter;
        if(nbc==false){
            //skip do nothing
//            Toast.makeText(context, data_after+"\nnot spam", Toast.LENGTH_SHORT).show();
        }else{
            nbc = naive_bayes.naiveBayesClass(arl_term_testing,"model2",context);
            if(nbc==true){
                //spam_filter 1
                spam_filter = 1;
//                Toast.makeText(context, data_after+"\nfraud", Toast.LENGTH_SHORT).show();
            }else{
                //spam_filter 0
                spam_filter = 0;
//                Toast.makeText(context, data_after+"\npromo", Toast.LENGTH_SHORT).show();
            }
            //update spam_report content://sms/
            try{
                //ga error ga ngapdet wat u wan, SOMETHING WRONG WITH VERSION :3
                ContentValues contentValues = new ContentValues();
                contentValues.put("spam_report", true);
                context.getContentResolver().update(Uri.parse("content://sms/inbox"), contentValues, "_id = "+_id, null);
//                Log.e("_id__spam_report", _id);
            }catch(Exception e){
                Log.e("bugy",e.toString());
            }

            //insert table spam_filter db
            try{
                db_helper.insertSpam(Integer.parseInt(_id),spam_filter);
            }catch(Exception e){
                Log.e("buggy",e.toString());
            }
        }
    }

    public void filter() throws Exception {
        String last_id=getLastID();
        Log.e("Last ID Filter",last_id+"");
        Cursor c_inbox = context.getContentResolver().query(Uri.parse("content://sms/inbox"), new String[]{"_id","body"}, "_id>"+last_id, null, "_id asc");

        if(c_inbox.moveToFirst()) {
            testing(c_inbox.getString(0), c_inbox.getString(1));
            last_id = c_inbox.getString(0);
//            saveLastID(last_id);//antisipasi close watu launching pertama
            while (c_inbox.moveToNext()) {
                testing(c_inbox.getString(0), c_inbox.getString(1));
                last_id = c_inbox.getString(0);
                saveLastID(last_id);
            }
        }
        c_inbox.close();
    }
    public void tes(){//hanya untuk cek value spam_report
        //coba coba pun tak bisa karena akses versi android
//        ContentValues contentValues = new ContentValues();
//        contentValues.put("spam_report",true);
//        context.getContentResolver().update(Uri.parse("content://sms/inbox"), contentValues, "_id = ?", new String[]{"1880"});

        Cursor c_inbox = context.getContentResolver().query(Uri.parse("content://sms/inbox"), new String[]{"_id","body","spam_report"}, null, null, "_id asc");
        int i=0;
        i++;
        if(c_inbox.moveToFirst()) {
            Log.e("spam_report", c_inbox.getString(0) + " " + c_inbox.getString(1) + " " + c_inbox.getString(2));
            while (c_inbox.moveToNext()) {
                i++;
                Log.e("spam_report", c_inbox.getString(0) + " " + c_inbox.getString(1) + " " + c_inbox.getString(2));
            }
        }
        c_inbox.close();
    }
    public void saveLastID(String _id){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("_id",_id);
        editor.commit();

//        int last_id = sharedPreferences.getInt("_id",0);
    }
    public String getLastID(){
        return sharedPreferences.getString("_id","0");
    }
}
