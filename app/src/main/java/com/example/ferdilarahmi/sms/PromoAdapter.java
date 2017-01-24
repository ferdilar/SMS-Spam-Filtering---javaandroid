package com.example.ferdilarahmi.sms;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

/**
 * Created by Ferdila Rahmi on 9/14/2016.
 */
public class PromoAdapter extends ArrayAdapter<Promo> {

    Context context;
    int layoutResourceId;
    Promo data[] = null;

    public PromoAdapter(Context context, int layoutResourceId, Promo[] data) {
        super(context, layoutResourceId, data);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.data = data;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        PromoHolder holder = null;

        if(row == null)
        {
            LayoutInflater inflater = ((Activity)context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);

            holder = new PromoHolder();
            holder.tv_address_name = (TextView)row.findViewById(R.id.tv_address_name);
            holder.tv_body = (TextView)row.findViewById(R.id.tv_body);
            holder.tv_datetime = (TextView)row.findViewById(R.id.tv_datetime);
            holder.tv_read = (TextView)row.findViewById(R.id.tv_read);
            holder.tv_id = (TextView)row.findViewById(R.id.tv_id);

            row.setTag(holder);
        }
        else
        {
            holder = (PromoHolder) row.getTag();
        }

        Promo promo = data[position];
        holder.tv_address_name.setText(promo.address_name);
        holder.tv_body.setText(promo.body);
        holder.tv_datetime.setText(promo.datetime);
        /*if(promo.read!=1){//satu sudah dibaca
            holder.tv_read.setText("Baru");
            holder.tv_read.setBackgroundResource( R.drawable.circle );
        }*/
        holder.tv_id.setText(String.valueOf(promo._id));

        return row;
    }

    static class PromoHolder
    {
        TextView tv_address_name;
        TextView tv_body;
        TextView tv_datetime;
        TextView tv_read;
        TextView tv_id;
    }
}
