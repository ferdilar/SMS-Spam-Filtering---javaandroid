package com.example.ferdilarahmi.sms;


import android.app.Dialog;
import android.app.SearchManager;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class ChatActivity extends AppCompatActivity {

    ListView list_chat;

    int _idthread;

    ImageButton btn_send;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat_activity);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        btn_send = (ImageButton) findViewById(R.id.btn_send);

        handleIntent(getIntent());
        String address = getIntent().getExtras().getString("address", "");
        _idthread = getIntent().getExtras().getInt("_idthread");
        setTitle(address);

                /*Ambil Data Chat*/

        ArrayList<Chat> arl_chat = fetchChat();
        Chat[] chat = arl_chat.toArray(new Chat[arl_chat.size()]);
        list_chat = (ListView) findViewById(R.id.lv_chat_list);
        if (fetchChat() != null) {
            ChatAdapter adapter = new ChatAdapter(this, R.layout.chat_row_listview, chat);
            list_chat.setAdapter(adapter);

//            ArrayAdapter<String> adapter = new  ArrayAdapter<String>(this, R.layout.chat_row_listview, R.id.tv_body, fetchChat());
//            list_chat.setAdapter(adapter);
        }

        btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //get number and get getText
                Toast.makeText(getApplicationContext(), "hehehe", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onNewIntent(Intent intent) {
        // TODO Auto-generated method stub
        setIntent(intent);
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {
        // TODO Auto-generated method stub
        if(Intent.ACTION_SEARCH.equals(intent.getAction())){
            String query = intent.getStringExtra(SearchManager.QUERY);
        }
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

    public ArrayList<Chat> fetchChat(){
        ArrayList<Chat> arl_chat = new ArrayList<Chat>();

        Cursor c_sms = getContentResolver().query(Uri.parse("content://sms"), new String[]{"_id","address","date","body","type"}, "thread_id = "+_idthread, null, "date asc");
        Chat chat;

        if(c_sms.moveToFirst()){
            chat = getChat(c_sms);
            arl_chat.add(chat);
            while(c_sms.moveToNext()) {
                chat = getChat(c_sms);
                arl_chat.add(chat);
            }
        }

        c_sms.close();
        return arl_chat;
    }
    public Chat getChat(Cursor c_sms){
        String body="";
        String datetime_chat="";
        int _id;
        int type=0;

        Date date_long = new Date(c_sms.getLong(2));

        body = c_sms.getString(3);
        datetime_chat = new SimpleDateFormat("hh:mm a  dd-MMM-yy").format(date_long);
        _id = c_sms.getInt(0);
        type = c_sms.getInt(4);

        Chat chat = new Chat(body, datetime_chat, _id, type);
        return chat;
    }
}
