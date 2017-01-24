package com.example.ferdilarahmi.sms;

import android.app.Dialog;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.design.widget.FloatingActionButton;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class FraudActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    ListView list_fraud;

    Uri uri_contact;

    DBHelper db_helper;

    static boolean active = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fraud_activity);
        /**************/
        Intent intent = getIntent();
        boolean br = intent.getBooleanExtra("Update",false);
        if(br==true){
            Log.e("BroadcastReceiver","Update");
            Intent intent_to = new Intent(this, FraudActivity.class);
            intent_to.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent_to);
            finish();
        }
        /**************/
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab_home);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(FraudActivity.this, SmsActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                intent.putExtra("tag", "Sms");
                startActivity(intent);
                finish();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout_fraud);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        /*Ambil Data SMS*/
        ArrayList<Fraud> arl_inbox = new ArrayList<Fraud>();
        arl_inbox = fetchInbox();
        Fraud[] fraud_inbox = arl_inbox.toArray(new Fraud[arl_inbox.size()]);
        list_fraud = (ListView) findViewById(R.id.lv_fraud_list);
        if (fetchInbox() != null) {
            FraudAdapter adapter = new FraudAdapter(this, R.layout.fraud_row_listview, fraud_inbox);
//            list_sms.addHeaderView(header);
            list_fraud.setAdapter(adapter);
        }

        list_fraud.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1,int position, long arg3)
            {
//                String _id = ((TextView) arg1.findViewById(R.id.tv_id)).getText().toString();
                String address = ((TextView) arg1.findViewById(R.id.tv_address_name)).getText().toString();
                String body = ((TextView) arg1.findViewById(R.id.tv_body)).getText().toString();
                openDialogView(address,body);
            }
        });
        Filtering filtering = new Filtering(this);
        try {
            filtering.filter();
        } catch (Exception e) {
//            e.printStackTrace();
            Log.e("Error filtering",e.toString());
        }
    }
    public void openDialogView(String address, String body) {

        final Dialog dialog = new Dialog(this); // Context, this, etc.
        dialog.setContentView(R.layout.dialog_view);
        dialog.setTitle("Dari: "+address);
        dialog.show();
        Button btnDialog = (Button) dialog.findViewById(R.id.dialog_close);
        TextView tvDialogInfo = (TextView) dialog.findViewById(R.id.dialog_info);
        tvDialogInfo.setText(body);
        btnDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
            }
        });
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout_fraud);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.spam_menu, menu);
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
                dialog0.setTitle("Setting");
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

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_fraud) {
            Intent intent = new Intent(this, FraudActivity.class);
            intent.putExtra("tag", "Fraud");
            startActivity(intent);
            finish();
        } else if (id == R.id.nav_promotion) {
            Intent intent = new Intent(this, PromoActivity.class);
            intent.putExtra("tag", "Promotion");
            startActivity(intent);
            finish();
        } else if (id == R.id.nav_about) {
            Dialog dialog2 = new Dialog(this);
            dialog2.setTitle("ferdila.rahmi@student.upi.edu");
            dialog2.show();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout_fraud);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onStart() {
        super.onStart();
        active = true;
    }

    @Override
    public void onStop() {
        super.onStop();
        active = false;
    }

    public ArrayList<Fraud> fetchInbox(){
        ArrayList<Fraud> arl_sms_inbox = new ArrayList<Fraud>();
        db_helper = new DBHelper(this);

        //load db_helper cek apakah _id terdapat dalam tabel spam_filter id_inbox?
        //atau pakai beetwen di query?
        final String[] projection = new String[]{"*"};
        Cursor cursor = db_helper.getSpam(1);
        String id_inbox="";
        if(cursor.moveToFirst()){
            //
            id_inbox = cursor.getString(1);//id_inbox
            while(cursor.moveToNext()){
                //
                id_inbox = id_inbox+","+cursor.getString(1);
            }
        }
        String selection = "_id in ("+id_inbox+")";

        Cursor c_sms_inbox = getContentResolver().query(Uri.parse("content://sms/inbox"), projection, selection, null, "date desc");

        Fraud fraud;

        if(c_sms_inbox.moveToFirst()){
            fraud = getFraud(c_sms_inbox);
            arl_sms_inbox.add(fraud);

            while(c_sms_inbox.moveToNext()){
                fraud = getFraud(c_sms_inbox);
                arl_sms_inbox.add(fraud);
            }
        }


        c_sms_inbox.close();
        return arl_sms_inbox;
    }
    public Fraud getFraud(Cursor c_fraud_inbox){
        int _id=c_fraud_inbox.getInt(0);//buat masuk conversation dikirim intent = id_thread
        String address=c_fraud_inbox.getString(2);
        String body=c_fraud_inbox.getString(12);
        int read=c_fraud_inbox.getInt(7);

        Date date_long = new Date(c_fraud_inbox.getLong(4));
        String datetime = new SimpleDateFormat("hh:mm a\ndd-MMM-yy").format(date_long);

            /*mendapatkan display_name dari address dr address dalam uriContact */
        uri_contact = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI,Uri.encode(address));

        Cursor c_contact_name = getContentResolver().query(uri_contact, new String[]{ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME}, null, null, null);
        if (c_contact_name.moveToFirst()){
            address = c_contact_name.getString(c_contact_name.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
        }

        c_contact_name.close();

        Fraud fraud = new Fraud(address, body, datetime, read, _id);

        return fraud;
    }
}
