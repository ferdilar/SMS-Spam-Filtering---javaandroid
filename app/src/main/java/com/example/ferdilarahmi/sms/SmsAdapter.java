package com.example.ferdilarahmi.sms;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

/**
 * Created by Ferdila Rahmi on 8/10/2016.
 */
public class SmsAdapter extends ArrayAdapter<Sms> {

    Context context;
    int layoutResourceId;
    Sms data[] = null;

    public SmsAdapter(Context context, int layoutResourceId, Sms[] data) {
        super(context, layoutResourceId, data);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.data = data;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        SmsHolder holder = null;

        if(row == null)
        {
            LayoutInflater inflater = ((Activity)context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);

            holder = new SmsHolder();
            holder.tv_address_name = (TextView)row.findViewById(R.id.tv_address_name);
            holder.tv_snippet = (TextView)row.findViewById(R.id.tv_snippet);
            holder.tv_datetime = (TextView)row.findViewById(R.id.tv_datetime);
            holder.tv_count_unread = (TextView)row.findViewById(R.id.tv_count_unread);
            holder.tv_idthread = (TextView)row.findViewById(R.id.tv_idthread);

            row.setTag(holder);
        }
        else
        {
            holder = (SmsHolder)row.getTag();
        }

        Sms sms = data[position];
        holder.tv_address_name.setText(sms.address_name);
        holder.tv_snippet.setText(sms.snippet);
        holder.tv_datetime.setText(sms.datetime);
        /*if(sms.count_unread!=0){
            holder.tv_count_unread.setText(String.valueOf(sms.count_unread));
            holder.tv_count_unread.setBackgroundResource( R.drawable.circle );
        }*/
        holder.tv_idthread.setText(String.valueOf(sms._idthread));

        return row;
    }

    static class SmsHolder
    {
        TextView tv_address_name;
        TextView tv_snippet;
        TextView tv_datetime;
        TextView tv_count_unread;
        TextView tv_idthread;
    }
}
