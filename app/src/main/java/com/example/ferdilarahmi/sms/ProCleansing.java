package com.example.ferdilarahmi.sms;

/**
 * Created by Ferdila Rahmi on 8/23/2016.
 */
public class ProCleansing {

    ProCleansing(){
        super();
    }

    public String caseFolding(String document){
        document=document.toLowerCase();
        return document;
    }

    public String removeChar(String document){
        document=document.replaceAll("[^a-z ]"," ");

//        document=document.replace(","," ");
//        document=document.replace("0"," ");
//        document=document.replace("1"," ");
//        document=document.replace("2"," ");
//        document=document.replace("3"," ");
//        document=document.replace("4"," ");
//        document=document.replace("5"," ");
//        document=document.replace("6"," ");
//        document=document.replace("7"," ");
//        document=document.replace("8"," ");
//        document=document.replace("9"," ");
//        document=document.replace("%"," ");
//        document=document.replace("$"," ");
//        document=document.replace("#"," ");
//        document=document.replace("*"," ");
//        document=document.replace("!"," ");
//        document=document.replace("?"," ");
//        document=document.replace("@"," ");
//        document=document.replace("-"," ");
//        document=document.replace("="," ");
//        document=document.replace(":"," ");
//        document=document.replace("\""," ");
//        document=document.replace("\\"," ");
//        document=document.replace("'"," ");
//        document=document.replace("/"," ");
//        document=document.replace("+"," ");
//        document=document.replace("&"," ");
//        document=document.replace(";"," ");
//        document=document.replace(">"," ");
//        document=document.replace("<"," ");
//        document=document.replace("]"," ");
//        document=document.replace("["," ");
//        document=document.replace("{"," ");
//        document=document.replace("}"," ");
//        document=document.replace("~"," ");
//        document=document.replace("`"," ");
//        document=document.replace("^"," ");
//        document=document.replace("("," ");
//        document=document.replace(")"," ");
//        document=document.replace("_"," ");
//        document=document.replace("•"," ");
//        document=document.replace("×"," ");
//        document=document.replace("’"," ");
//        document=document.replace("…"," ");
//        document=document.replace("“"," ");
//        document=document.replace("”"," ");
        return document;
    }
}
