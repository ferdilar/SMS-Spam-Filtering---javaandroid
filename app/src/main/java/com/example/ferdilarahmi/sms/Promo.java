package com.example.ferdilarahmi.sms;

/**
 * Created by Ferdila Rahmi on 9/14/2016.
 */
public class Promo {
    String address_name;
    String body;
    String datetime;
    int read;
    int _id;

    public Promo (){
        super();
    }

    public Promo(String address_name, String body, String datetime, int read, int _id) {
        super();
        this.address_name = address_name;
        this.body = body;
        this.datetime = datetime;
        this.read = read;
        this._id = _id;
    }
}
