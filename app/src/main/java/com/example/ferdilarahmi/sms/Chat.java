package com.example.ferdilarahmi.sms;

/**
 * Created by Ferdila Rahmi on 8/9/2016.
 */
public class Chat {
    String body;
    String datetime_chat;
//    String sim;
    int _id;
    int type;

    public Chat (){
        super();
    }

    public Chat(String body, String datetime_chat, int _id, int type) {
        super();
        this.body = body;
        this.datetime_chat = datetime_chat;
//        this.sim = sim;
        this._id = _id;
        this.type = type;
    }
}
