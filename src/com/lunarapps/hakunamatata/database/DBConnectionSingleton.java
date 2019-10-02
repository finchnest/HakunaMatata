package com.lunarapps.hakunamatata.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnectionSingleton {

    public Connection connection;

    private static DBConnectionSingleton dbConnectionSingleton = null;

    private DBConnectionSingleton() {

        //code to create connection to the database

        try {

            Class.forName("com.mysql.jdbc.Driver");// returns the Class object for the class with the specified name
            connection = DriverManager
                    .getConnection("jdbc:mysql://127.0.0.1:3306/fileServer", "root", "");//returns  a connection to the URL

        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }


    }

    public static DBConnectionSingleton getDBConnectionSingleton() {
        if (dbConnectionSingleton == null) {
            dbConnectionSingleton = new DBConnectionSingleton();
        }
        return dbConnectionSingleton;
    }


}
