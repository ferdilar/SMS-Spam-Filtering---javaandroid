package com.example.ferdilarahmi.sms;

import android.content.Context;
import android.database.Cursor;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.StringTokenizer;

/**
 * Created by Ferdila Rahmi on 8/23/2016.
 */
public class ProTokenizing {
    Context context;
    DBHelper db_helper;
    private String error;
    private String kata_output;

    ProTokenizing(Context context) throws Exception {
        super();
        this.context=context;
        db_helper = new DBHelper(context);
    }

    public ArrayList<String> tokenizing(String document) throws Exception {
        StringTokenizer st = new StringTokenizer(document," ");
        ArrayList<String> token1 = new ArrayList<String>();
        String term;
//      System.out.println(st.countTokens());
        while(st.hasMoreElements()){
            term = st.nextToken();
                /*
                    Filtering
                 */
            if(cekSynonym(term)){
                term = getKata_output();
            }

                /*
                    Stemming
                 */
    //        term=pro_stemming.getTermStemming(term);

                /*
                    Stopword
                 */
            if(cekStopword(term)){
                token1.add(term);
//                Toast.makeText(context, term+" isnot stopword", Toast.LENGTH_SHORT).show();
            }
//            else{
//                Toast.makeText(context, term+" is stopword", Toast.LENGTH_SHORT).show();
//            }
        }
        return token1;
    }
    public boolean cekStopword(String term) throws Exception {
        boolean stopwordNotFound=false;
        Cursor cursor=null;
        try{
            cursor = db_helper.getStopwordByTerm(term);
            if(cursor.getCount()==0){//jika tdk ditemukan maka tambahkan
                stopwordNotFound=true;
            }
        }catch(Exception e){
            error = e.toString();
        }finally {// biar ga warning db leaked
            if (cursor != null && !cursor.isClosed())
                cursor.close();
        }
        return stopwordNotFound;
    }
    //////////////////////////////////////////////////////
    public boolean cekSynonym(String term) {
        boolean filterwordFound = true;
        Cursor cursor=null;
        try {
            cursor = db_helper.getSynonymByTerm(term);
            //ditemukan berarti belum baku baku
            if (cursor.getCount()==0) {
                filterwordFound = false;
            }else{
                setKata_output(cursor.getString(2).toString());
            }
        } catch (Exception e) {
            error = e.toString();
        }finally {// biar ga warning db leaked
            if (cursor != null && !cursor.isClosed())
                cursor.close();
        }
        return filterwordFound;
    }

    public String getKata_output() {
        return kata_output;
    }

    public void setKata_output(String kata_output) {
        this.kata_output = kata_output;
    }

    public String getError(){
        return this.error;
    }
}
