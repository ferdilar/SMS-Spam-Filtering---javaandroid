package com.example.ferdilarahmi.sms;

/**
 * Created by Ferdila Rahmi on 8/8/2016.
 */

import android.app.Activity;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v7.app.NotificationCompat;
import android.telephony.SmsMessage;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Random;

public class SmsReceiver extends BroadcastReceiver{

    private NotificationManager mNotificationManager;
    private int notificationID = 100;
    private int notificationIDFraud = 101;
    private int notificationIDPromo = 102;
    private int numMessages = 0;//ngupdate perbaiki pake count u read aj
    Context context;

    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle bundle = intent.getExtras();
        SmsMessage[] messages = null ;
        this.context = context;
        String str = "";
        String address = "";
        if(bundle != null){
            Object[] pdus = (Object[]) bundle.get("pdus");
            messages = new SmsMessage[pdus.length];
            for(int i=0; i<messages.length; i++)
            {
                messages[i]=SmsMessage.createFromPdu((byte[])pdus[i]);
                address = messages[i].getOriginatingAddress();
                str += messages[i].getMessageBody().toString();
            }

            Uri uri_contact = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI,Uri.encode(address));

            Cursor c_contact_name = context.getContentResolver().query(uri_contact, new String[]{ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME}, null, null, null);
            if (c_contact_name.moveToFirst()){
                address = c_contact_name.getString(c_contact_name.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
            }

            c_contact_name.close();

            //sent a broadcast intent to update the SMS received in a textview
            Intent broadcastIntent = new Intent();
            broadcastIntent.setAction("SMS_RECEIVED_ACTION");
            broadcastIntent.putExtra("sms", str);
            context.sendBroadcast(broadcastIntent);
            try {
                this.testing(str,address);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if(PromoActivity.active==true){
            Log.e("BroadcastReceiver","Update Promo Active");
            Intent intent_to = new Intent(context, PromoActivity.class);
            intent_to.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_NEW_TASK);
            intent_to.putExtra("Update",true);
            context.startActivity(intent_to);
        }else if(FraudActivity.active==true){
            Log.e("BroadcastReceiver","Update Fraud Active");
            Intent intent_to = new Intent(context, FraudActivity.class);
            intent_to.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_NEW_TASK);
            intent_to.putExtra("Update",true);
            context.startActivity(intent_to);
        }else if(SmsActivity.active==true){
            Log.e("BroadcastReceiver","Update SMS Active");
            Intent intent_to = new Intent(context, SmsActivity.class);
            intent_to.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_NEW_TASK);
            intent_to.putExtra("Update",true);
            context.startActivity(intent_to);
        }
    }
    public void testing(String sms_testing, String address) throws Exception {
        String data_after = "";
        boolean nbc;
        ArrayList<String> arl_term_testing = new ArrayList<String>();

        ProCleansing pro_cleansing = new ProCleansing();
        ProTokenizing pro_tokenizing = new ProTokenizing(context);

        data_after = pro_cleansing.caseFolding(sms_testing);
        data_after = pro_cleansing.removeChar(data_after);

        //proses tokenzing terdapat filtering, stemming return nilai array list
        arl_term_testing = pro_tokenizing.tokenizing(data_after);
        data_after="";
        for(int j=0;j<arl_term_testing.size();j++){
            data_after = data_after+" "+arl_term_testing.get(j);
        }
        NaiveBayes naive_bayes = new NaiveBayes();

        nbc = naive_bayes.naiveBayesClass(arl_term_testing,"model1",context);

        if(nbc==false){
            //tidak terjadi apa, tampilkan notif pesan biasa
            displayNotification(address, sms_testing);
//            Toast.makeText(context, data_after+"\nNOT SPAM", Toast.LENGTH_LONG).show();
        }else{
            nbc = naive_bayes.naiveBayesClass(arl_term_testing,"model2",context);
            if(nbc==true){
                displayNotificationSpamFraud(address, sms_testing);
//                Toast.makeText(context, data_after+"\nSPAM FRAUD", Toast.LENGTH_LONG).show();
                //get _id inbox sms insert into table spam_filter set spam=1
                //tampilkan notif pesan spam detected
            }else{
                displayNotificationSpamPromo(address, sms_testing);
//                Toast.makeText(context, data_after+"\nSPAM PROMO", Toast.LENGTH_LONG).show();
                //get _id inbox sms insert into table spam_filter set spam=0
                //tampilkan notif pesan spam detected
            }
        }
    }

    private void displayNotification(String notificationTitle, String notificationMessage){
        Uri alertSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
        builder.setSmallIcon(R.mipmap.ic_mail_outline_white);
        builder.setContentTitle(notificationTitle);//"Notifications Example")
        builder.setContentText(notificationMessage);//"This is a test notification");
        builder.setTicker(notificationTitle+ ": " +notificationMessage);
        builder.setNumber(++numMessages);
        builder.setSound(alertSound);
        builder.setAutoCancel(true);

        Intent notificationIntent = new Intent(context, SmsActivity.class);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_NEW_TASK);
        notificationIntent.putExtra("Update",true);

        PendingIntent contentIntent = PendingIntent.getActivity(context, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(contentIntent);
        // Add as notification
        mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

	    /* notificationID untuk membuat notifikasi lain berikutnya. */
        mNotificationManager.notify(notificationID, builder.build());
    }

    // Remove notification
   /* private void removeNotification() {
        mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.cancel(notificationID);
    }*/
    private void displayNotificationSpamFraud(String notificationTitle, String notificationMessage){

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
        builder.setSmallIcon(R.mipmap.ic_warning_black);
        builder.setContentTitle(notificationTitle);//"Notifications Example")
        builder.setContentText(notificationMessage);//"This is a test notification");
        builder.setTicker(notificationTitle+ ": " +notificationMessage);
        builder.setNumber(++numMessages);

        Uri sound = Uri.parse("android.resource://" + context.getPackageName() + "/" + R.raw.notifysound);
        builder.setSound(sound);

        builder.setAutoCancel(true);

        Intent notificationIntent = new Intent(context, FraudActivity.class);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_NEW_TASK);
        notificationIntent.putExtra("Update",true);

        PendingIntent contentIntent = PendingIntent.getActivity(context, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(contentIntent);
        // Add as notification
        mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

	    /* notificationID untuk membuat notifikasi lain berikutnya. */
        mNotificationManager.notify(notificationIDFraud, builder.build());
    }
    private void displayNotificationSpamPromo(String notificationTitle, String notificationMessage){

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
        builder.setSmallIcon(R.mipmap.ic_shopping_basket_black);
        builder.setContentTitle(notificationTitle);//"Notifications Example")
        builder.setContentText(notificationMessage);//"This is a test notification");
        builder.setTicker(notificationTitle+ ": " +notificationMessage);
        builder.setNumber(++numMessages);

        Uri sound = Uri.parse("android.resource://" + context.getPackageName() + "/" + R.raw.notifysound);
        builder.setSound(sound);

        builder.setAutoCancel(true);

        Intent notificationIntent = new Intent(context, PromoActivity.class);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_NEW_TASK);
        notificationIntent.putExtra("Update",true);

        PendingIntent contentIntent = PendingIntent.getActivity(context, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(contentIntent);
        // Add as notification
        mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

	    /* notificationID untuk membuat notifikasi lain berikutnya. */
        mNotificationManager.notify(notificationIDPromo, builder.build());
    }
}
