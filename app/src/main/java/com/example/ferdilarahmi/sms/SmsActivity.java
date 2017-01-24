package com.example.ferdilarahmi.sms;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.SearchView;
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

public class SmsActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    ListView list_sms;

    Uri uri_contact;
    SharedPreferences sharedPreferences;

    static boolean active = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sms_activity);
        /**************/
        Intent intent = getIntent();
        boolean br = intent.getBooleanExtra("Update",false);
        if(br==true){
            Log.e("BroadcastReceiver","Update");
            Intent intent_to = new Intent(this, SmsActivity.class);
            intent_to.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent_to);
            finish();
        }
        /**************/
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();

                Intent intent = new Intent(SmsActivity.this, ComposeActivity.class);
                intent.putExtra("tag", "Compose");
                startActivity(intent);
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        /*Ambil Data SMS*/
        ArrayList<Sms> arl_conversation = new ArrayList<Sms>();
        arl_conversation = fetchConversation();
        Sms[] sms_conversation = arl_conversation.toArray(new Sms[arl_conversation.size()]);
        list_sms = (ListView) findViewById(R.id.lv_sms_list);
        if (fetchConversation() != null) {
            SmsAdapter adapter = new SmsAdapter(this, R.layout.sms_row_listview, sms_conversation);
//            list_sms.addHeaderView(header);
            list_sms.setAdapter(adapter);
        }

        list_sms.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1,int position, long arg3)
            {
                String _idthread = ((TextView) arg1.findViewById(R.id.tv_idthread)).getText().toString();
                String address = ((TextView) arg1.findViewById(R.id.tv_address_name)).getText().toString();

                Intent intent = new Intent(SmsActivity.this, ChatActivity.class);
                intent.putExtra("_idthread", Integer.parseInt(_idthread));
                intent.putExtra("address", address);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.sms_menu, menu);
        // Menghubungkan konfigurasi searchable dengan SearchView

        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView SearchView = (SearchView) MenuItemCompat.getActionView(searchItem);

        /*biar ada warna wk*/
        final SearchView.SearchAutoComplete searchAutoComplete = (SearchView.SearchAutoComplete) SearchView.findViewById(android.support.v7.appcompat.R.id.search_src_text);
        searchAutoComplete.setTextColor(Color.BLACK);
        searchAutoComplete.setBackgroundColor(Color.WHITE);

//        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,R.layout.search_autocomplete_textview, arl_search);
//        searchAutoComplete.setAdapter(adapter);

//        SearchManager searchManager = (SearchManager) getSystemService(this.SEARCH_SERVICE);
//        SearchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));

//        searchAutoComplete.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                String searchString=(String)parent.getItemAtPosition(position);
//                searchAutoComplete.setText(""+searchString);
//                Toast.makeText(MainActivity.this, "you clicked "+searchString, Toast.LENGTH_LONG).show();
//            }
//        });
//        return true;
//        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
//        SearchView searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
//        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        switch (id) {
            case R.id.action_settings:
                //reset data
                openDialogReset(this);
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
            //intent to activity fraud
            Intent intent = new Intent(this, FraudActivity.class);
            intent.putExtra("tag", "Fraud");
            startActivity(intent);
        } else if (id == R.id.nav_promotion) {
            Intent intent = new Intent(this, PromoActivity.class);
            intent.putExtra("tag", "Promotion");
            startActivity(intent);
        } else if (id == R.id.nav_about) {
            Dialog dialog2 = new Dialog(this);
            dialog2.setTitle("ferdila.rahmi@student.upi.edu");
            dialog2.show();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
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

    public void openDialogReset(Context c) {
        final Context context = c;
        final Dialog dialog = new Dialog(this); // Context, this, etc.
        dialog.setContentView(R.layout.dialog_reset);
        dialog.setTitle("Reset Database ?");
        dialog.show();
        Button btnDialogY = (Button) dialog.findViewById(R.id.dialog_yes);
        Button btnDialogN = (Button) dialog.findViewById(R.id.dialog_no);
        TextView tvDialogInfo = (TextView) dialog.findViewById(R.id.dialog_info);
        btnDialogN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
            }
        });
        btnDialogY.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences sharedPreferences = getSharedPreferences("sms_spam",MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean("status",false);
                editor.commit();

                dialog.dismiss();

                //intent to main
                Intent intent = new Intent(context, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    public ArrayList<Sms> fetchConversation(){
        ArrayList<Sms> arl_sms_conv = new ArrayList<Sms>();

        final String[] projection = new String[]{"*"};
        Cursor c_sms_conversation = getContentResolver().query(Uri.parse("content://mms-sms/conversations?simple=true"), projection, null, null, "date desc");

        Sms sms;

        if(c_sms_conversation.moveToFirst()){
            sms = getSms(c_sms_conversation);
            arl_sms_conv.add(sms);
            while(c_sms_conversation.moveToNext()){
                sms = getSms(c_sms_conversation);
                arl_sms_conv.add(sms);
            }
        }

        c_sms_conversation.close();
        return arl_sms_conv;
    }
    public Sms getSms(Cursor c_sms_conversation){
        int _idthread=c_sms_conversation.getInt(0);//buat masuk conversation dikirim intent = id_thread
        String recipientId=c_sms_conversation.getString(3);
        String snippet=c_sms_conversation.getString(4);
        int count_unread=c_sms_conversation.getInt(11);

        Date date_long = new Date(c_sms_conversation.getLong(1));
        String datetime = new SimpleDateFormat("hh:mm a\ndd-MMM-yy").format(date_long);

            /*mendapatkan address dari thread_id dr uri conversation dalam uri canonical-addresses */
        Cursor c_sms_canonical=  getContentResolver().query(Uri.parse("content://mms-sms/canonical-addresses"), null, "_id = " + recipientId, null, null);
        String address="";
        if (c_sms_canonical.moveToFirst()){
            address = c_sms_canonical.getString(1);
        }

            /*mendapatkan display_name dari address dr uri canonical dalam uriContact */
        uri_contact = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI,Uri.encode(address));

        Cursor c_contact_name = getContentResolver().query(uri_contact, new String[]{ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME}, null, null, null);
        if (c_contact_name.moveToFirst()){
            address = c_contact_name.getString(c_contact_name.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
        }

        c_contact_name.close();
        c_sms_canonical.close();

        Sms sms = new Sms(address, snippet, datetime, count_unread, _idthread);

        return sms;
    }
}
