package com.example.ferdilarahmi.sms;

/**
 * Created by Ferdila Rahmi on 8/9/2016.
 * conversation
 */
public class Sms {
    String address_name;
    String snippet;
    String datetime;
    int count_unread;
    int _idthread;

    public Sms (){
        super();
    }

    public Sms(String address_name, String snippet, String datetime, int count_unread, int _idthread) {
        super();
        this.address_name = address_name;
        this.snippet = snippet;
        this.datetime = datetime;
        this.count_unread = count_unread;
        this._idthread = _idthread;
    }
}
