package com.lunarapps.hakunamatata.models;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.Serializable;

public enum Sex implements Serializable {

    MALE,
    FEMALE
}


// class DBConnectionSingleton {
//
//    static DBConnectionSingleton mSingleton=new DBConnectionSingleton();
//
//    private DBConnectionSingleton(){
//        //private constructor so that no other class can create instance of this class
//
//    }
//
//    public static DBConnectionSingleton getmSingleton(){
//        return mSingleton;
//        //we don't wanna return a new instance every
//        // time this method is called, hence the static variable above
//    }
//
//}
