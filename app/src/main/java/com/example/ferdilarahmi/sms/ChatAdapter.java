package com.example.ferdilarahmi.sms;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Created by Ferdila Rahmi on 8/10/2016.
 */
public class ChatAdapter extends ArrayAdapter<Chat> {

    Context context;
    int layoutResourceId;
    Chat data[] = null;

    public ChatAdapter(Context context, int layoutResourceId, Chat[] data) {
        super(context, layoutResourceId, data);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.data = data;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        ChatHolder holder = null;

        LinearLayout.LayoutParams params_left = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params_left.gravity = Gravity.LEFT;
        LinearLayout.LayoutParams params_right = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params_right.gravity = Gravity.RIGHT;

        if(row == null)
        {
            LayoutInflater inflater = ((Activity)context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);

            holder = new ChatHolder();
            holder.lin_chat_row_listview_1 = (LinearLayout) row.findViewById(R.id.lin_chat_row_listview_1);
            holder.lin_chat_row_listview_2 = (LinearLayout) row.findViewById(R.id.lin_chat_row_listview_2);
            holder.tv_body = (TextView)row.findViewById(R.id.tv_body);
            holder.tv_datetime_chat = (TextView)row.findViewById(R.id.tv_datetime_chat);
            holder.tv_sim = (TextView)row.findViewById(R.id.tv_sim);
            holder.tv_idchat = (TextView)row.findViewById(R.id.tv_idchat);

            row.setTag(holder);
        }
        else
        {
            holder = (ChatHolder)row.getTag();
        }

        Chat chat = data[position];
        if(chat.type==1){
            holder.lin_chat_row_listview_1.setLayoutParams(params_left);
            holder.lin_chat_row_listview_2.setLayoutParams(params_left);
            holder.tv_body.setBackgroundResource(R.drawable.callout_1_inbox);
        }else{
            holder.lin_chat_row_listview_1.setLayoutParams(params_right);
            holder.lin_chat_row_listview_2.setLayoutParams(params_right);
            if(chat.type==2){
                holder.tv_body.setBackgroundResource(R.drawable.callout_2_sent);
            }else{
                holder.tv_body.setBackgroundResource(R.drawable.callout_5_failed);
            }
        }
        holder.tv_body.setText(chat.body);
        holder.tv_datetime_chat.setText(chat.datetime_chat);
//        holder.tv_sim.setText(sms.sim);
        holder.tv_idchat.setText(String.valueOf(chat._id));

        return row;
    }

    static class ChatHolder
    {
        LinearLayout lin_chat_row_listview_1;
        LinearLayout lin_chat_row_listview_2;
        TextView tv_body;
        TextView tv_datetime_chat;
        TextView tv_sim;
        TextView tv_idchat;
    }
}
