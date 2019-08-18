package com.lunarapps.hakunamatata.database;

import com.lunarapps.hakunamatata.models.Sex;
import com.lunarapps.hakunamatata.models.User;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;


public class UserBuilder {

    private ResultSet resultSet;
    private Statement statement;
    private User user;

    public UserBuilder(String un, String pa) {

        try {
            statement = DBConnectionSingleton.getDBConnectionSingleton().connection.createStatement();
            user = builderOne(un, pa);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public User getUser(){
        return user;
    }

    public UserBuilder(String un) {
        try {
            statement = DBConnectionSingleton.getDBConnectionSingleton().connection.createStatement();
            user = builderTwo(un);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private User builderTwo(String un) {
        User usery = null;
        try {

            String qq = String.format("select 1 from USER where USERNAME='%s'", un);
            resultSet = statement.executeQuery(qq);

            while (resultSet.next()) {

                String firstname = resultSet.getString("FIRST_NAME");
                String lastname = resultSet.getString("LAST_NAME");
                Sex sex = (Sex) resultSet.getObject("SEX");
                String email = resultSet.getString("EMAIL");
                String username = resultSet.getString("USERNAME");
                String password = resultSet.getString("PASSWORD");

                if (email == null) {
                    usery = new User(firstname, lastname, sex, username, password);

                } else {
                    usery = new User(firstname, lastname, sex, email, username, password);

                }

            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return usery;
    }

    private User builderOne(String un, String pa) {
        User userx = null;
        try {

            String qq = String.format("select 1 from USER where USERNAME='%s' and PASSWORD='%s'", un, pa);
            resultSet = statement.executeQuery(qq);

            while (resultSet.next()) {

                String firstname = resultSet.getString("FIRST_NAME");
                String lastname = resultSet.getString("LAST_NAME");
                Sex sex = (Sex) resultSet.getObject("SEX");
                String email = resultSet.getString("EMAIL");
                String username = resultSet.getString("USERNAME");
                String password = resultSet.getString("PASSWORD");

                if (email == null) {
                    userx = new User(firstname, lastname, sex, username, password);

                } else {
                    userx = new User(firstname, lastname, sex, email, username, password);

                }

            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return userx;

    }
}
