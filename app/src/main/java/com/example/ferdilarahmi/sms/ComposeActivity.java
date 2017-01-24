package com.example.ferdilarahmi.sms;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Dialog;
import android.app.PendingIntent;
import android.app.SearchManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class ComposeActivity extends AppCompatActivity {
    AutoCompleteTextView ac_contacts;

    ImageButton btn_send;
    EditText et_message;

    IntentFilter intentFilter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.compose_activity);

        intentFilter = new IntentFilter();
        intentFilter.addAction("SMS_RECEIVED_ACTION");

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        ac_contacts = (AutoCompleteTextView)findViewById(R.id.ac_contacts);
        btn_send = (ImageButton) findViewById(R.id.btn_send);
        et_message = (EditText) findViewById(R.id.et_message);

        ac_contacts.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                String key = ac_contacts.getText().toString();
                searchContactAuto(key);
                return false;
            }
        });
        btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!et_message.getText().toString().equals("") && !ac_contacts.getText().toString().equals("")){
                    //proses
                    String noSeluler = ac_contacts.getText().toString();
                    String message = et_message.getText().toString();
                    sendMsg(noSeluler, message);

                    Intent intent_to = new Intent(ComposeActivity.this, SmsActivity.class);
                    startActivity(intent_to);
                    finish();
                    //masuk ke conversation kelas yang addres tsbt
                }else{
                    if(et_message.getText().toString().equals("")){
                        Toast.makeText(getApplicationContext(), "Masukan Pesan", Toast.LENGTH_SHORT).show();
                    }else{
                        Toast.makeText(getApplicationContext(), "Masukan Kontak", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    protected void sendMsg(String noSeluler, String message) {
        String SENT = "Message Sent";
        String DELIVERED = "Message Delivered";

        PendingIntent sentPI = PendingIntent.getBroadcast(this, 0, new Intent(SENT), 0);
        PendingIntent deliveredPI = PendingIntent.getBroadcast(this, 0, new Intent(DELIVERED), 0);

        registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                switch(getResultCode())
                {
                    case Activity.RESULT_OK:
                        Toast.makeText(getApplicationContext(), "SMS SENT", Toast.LENGTH_SHORT).show();
                        break;
                    case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
                        Toast.makeText(getApplicationContext(), "GENERIC FAILURE", Toast.LENGTH_SHORT).show();
                        break;
                    case SmsManager.RESULT_ERROR_NO_SERVICE:
                        Toast.makeText(getApplicationContext(), "NO SERVICE", Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        }, new IntentFilter(SENT));

        registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                switch(getResultCode())
                {
                    case Activity.RESULT_OK:
                        Toast.makeText(getApplicationContext(), "SMS DELIVERED", Toast.LENGTH_SHORT).show();
                        break;
                    case Activity.RESULT_CANCELED:
                        Toast.makeText(getApplicationContext(), "SMS NOT DELIVERED", Toast.LENGTH_SHORT).show();
                        break;
                }

            }
        }, new IntentFilter(DELIVERED));

        SmsManager sms = SmsManager.getDefault();
        sms.sendTextMessage(noSeluler, null, message, sentPI, deliveredPI);
    }

    private void searchContactAuto(String key) {
        Uri uriContact= ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
        Cursor c_contact= getContentResolver().query(uriContact, null, ContactsContract.CommonDataKinds.Phone.NUMBER+ " like '%" +key+ "%'", null, null);
        String[] listContacts= new String[c_contact.getCount()];
        int i = 0;
        c_contact.moveToFirst();
        while(c_contact.isAfterLast() == false){//problem using move to Next :"{
//            listContacts[i] = c_contact.getString(c_contact.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
            String numb = c_contact.getString(c_contact.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
//            listContacts[i] = listContacts[i]+"\n"+numb;
            numb=numb.replace("-","");
            numb=numb.replace("*","");
            numb=numb.replace("#","");
            numb=numb.replace(" ","");
            listContacts[i] = numb;
//            Log.e("cek",listContacts[i]);
            i++;
            c_contact.moveToNext();//works like that
        }
        c_contact.close();

        ArrayAdapter<String> adapterContacts = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, listContacts);
        ac_contacts.setAdapter(adapterContacts);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.compose_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        switch (id){
            case R.id.action_settings:
                Dialog dialog0 = new Dialog(this);
                dialog0.setTitle("Action Setting");
                dialog0.show();
                return true;
            case R.id.action_search:
                Dialog dialog1 = new Dialog(this);
                dialog1.setTitle("Pencarian");
                dialog1.show();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
