package com.example.ferdilarahmi.sms;

import android.content.Context;
import android.database.Cursor;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Created by Ferdila Rahmi on 8/23/2016.
 */
public class NaiveBayes {
    DBHelper db_helper;// ragu apakah harus di instance?
    private String error;
    double c_map_true;
    double c_map_false;
    Context context;

    NaiveBayes() throws Exception {
        super();
        c_map_true = 0;
        c_map_false = 0;
    }

    public double getC_map_true() {
        return c_map_true;
    }

    public void setC_map_true(double c_map_true) {
        this.c_map_true = c_map_true;
    }

    public double getC_map_false() {
        return c_map_false;
    }

    public void setC_map_false(double c_map_false) {
        this.c_map_false = c_map_false;
    }

    public boolean naiveBayesClass(ArrayList<String> arl_term_testing, String tabel_model, Context context){
        this.context = context;
        db_helper =  new DBHelper(context);
        boolean nbc = false;

        setC_map_true(cMAP(arl_term_testing,true,tabel_model));
        setC_map_false(cMAP(arl_term_testing,false,tabel_model));
//        Toast.makeText(context, getC_map_true() +" ? "+ getC_map_false(), Toast.LENGTH_SHORT).show();

        if(getC_map_true()>getC_map_false()){
            nbc = true;
        }
        return nbc;
    }

    public double cMAP(ArrayList<String> arl_term_testing, boolean c, String tabel_model){
        String row_model_class="1111";
        String row_model_null="0000";
        double prob=0;
        double p_class=0;
        double p_term_all=1;
        try{
            p_class=getProbModel(row_model_class, c, tabel_model);
            if(arl_term_testing.size()==0){
                p_term_all=0;
            }else {
                for (int j = 0; j < arl_term_testing.size(); j++) {
                    if(cekExistTerm(arl_term_testing.get(j),tabel_model)){
//                p_term_all=p_term_all+getProbModel(arl_term_testing.get(j),c,data_model);//log 10
                        p_term_all=p_term_all*getProbModel(arl_term_testing.get(j),c,tabel_model);//salah
//                        Toast.makeText(context, arl_term_testing.get(j)+" : "+p_class +" x1 "+ p_term_all, Toast.LENGTH_SHORT).show();
                    }else{
//                p_term_all=p_term_all+getProbModel(row_model_null,c,data_model);//log 10
                        p_term_all=p_term_all*getProbModel(row_model_null,c,tabel_model);//salah
//                        Toast.makeText(context, arl_term_testing.get(j)+" : "+p_class +" x0 "+ p_term_all, Toast.LENGTH_SHORT).show();
                    }
                }
            }
            prob = p_class * p_term_all;
//            System.out.println(" "+p_term_all+" "+p_class);
//            Toast.makeText(context, p_class +" x "+ p_term_all, Toast.LENGTH_SHORT).show();
        }catch(Exception e){
            error = e.toString();
        }
        return prob;
    }
    private boolean cekExistTerm(String term, String tabel_model) {
        //cekexist term in row tabel
        boolean existed=false;
        Cursor cursor=null;
        try {
            cursor = db_helper.getProbModel(term,tabel_model);
            if (cursor.getCount()>0) {
                existed=true;
            }
        }catch(Exception e){
            error = e.toString();
        }finally {// biar ga warning db leaked
            if (cursor != null && !cursor.isClosed())
                cursor.close();
        }
        return existed;
    }

    public double getProbModel(String term, boolean c, String data_model){
        double p_term=0;
        Cursor cursor=null;
        try{
            cursor = db_helper.getProbModel(term,data_model);
//            if(cursor.moveToFirst()){// return sudah move to first
                if(c==true){//kelas spam/fraud tergantung data_model
                    p_term=Double.valueOf(cursor.getDouble(2));
                }else{
                    p_term=Double.valueOf(cursor.getDouble(3));
                }
//            }
        }catch(Exception e){
            error = e.toString();
        }finally {// biar ga warning db leaked
            if (cursor != null && !cursor.isClosed())
                cursor.close();
        }
//        Toast.makeText(context, term+" : "+p_term +" p return", Toast.LENGTH_SHORT).show();
        return p_term;
    }
    public String getError(){
        return this.error;
    }
}
