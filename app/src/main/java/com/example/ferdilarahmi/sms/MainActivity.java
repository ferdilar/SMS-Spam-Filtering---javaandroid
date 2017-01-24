package com.example.ferdilarahmi.sms;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.media.MediaActionSound;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;

public class MainActivity extends AppCompatActivity{
    TextView tvRecord;

    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        sharedPreferences = getSharedPreferences("sms_spam", MODE_PRIVATE);
        tvRecord = (TextView) findViewById(R.id.tv_record);
        /**********/
        Log.i("Info",getSuccessInfo()+"");

        db_helper = new DBHelper(this);
        filtering = new Filtering(this);
        new splash().execute();
    }
    public void saveSuccessInfo(){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("status",true);
        editor.commit();
    }
    public boolean getSuccessInfo(){
        return sharedPreferences.getBoolean("status",false);
    }

    DBHelper db_helper;
    Filtering filtering;
    class splash extends AsyncTask<Void, Void, Void>{
        @Override
        protected Void doInBackground(Void... params) {
//            MainActivity.this.runOnUiThread(new Runnable() {//ga bisa jd hilang progress barnya
//                @Override
//                public void run() {
                    try {
                        boolean existed=MainActivity.this.db_helper.prepareDatabase();//setiap load main :/
                        if(existed==false){
                            Log.i("Info","Proses Filter ...");
                            MainActivity.this.filtering.filterAll();
                            Log.i("Info","Filter Selesai");
                            MainActivity.this.saveSuccessInfo();
                        }else{
                            if(MainActivity.this.getSuccessInfo()==false){
                                Log.i("Info","Reset Unfinished");
                                MainActivity.this.db_helper.resetDB();
                                Log.i("Info","Proses Filter ...");
                                MainActivity.this.filtering.filterAll();
                                Log.i("Info","Filter Selesai");
                                MainActivity.this.saveSuccessInfo();
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
//                }
//            });
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            try {
                Intent intent = new Intent(MainActivity.this, SmsActivity.class);
                startActivity(intent);
                finish();
            }catch (Exception e) {

            }
        }
    }
}
