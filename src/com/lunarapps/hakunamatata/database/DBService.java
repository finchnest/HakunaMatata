package com.lunarapps.hakunamatata.database;

import com.lunarapps.hakunamatata.models.Doc;
import com.lunarapps.hakunamatata.models.Feedback;
import com.lunarapps.hakunamatata.models.User;

import java.sql.SQLException;
import java.util.ArrayList;

/**
 * I gotta copy the model classes because during de-serialization the server JVM needs
 * to have the class of the objects being de-serialized. 'Could have used primitives or
 * String, or classes that are found in the J2SE library but that does't feel like a
 * good OO concept application
 */

//I need to have serialVersionUID explicitly stated in my class in case I unknowingly
// change the class in the server package


public interface DBService {

    boolean addUser(User u) throws SQLException;

    User getUser(String un, String pa) throws SQLException;

    boolean storeDoc(Doc d) throws SQLException;

    boolean checkUser(String un, String pa) throws Exception;

    ArrayList userHistory(User u) throws Exception;

    boolean submitFeedback(Feedback feed) throws Exception;

    ArrayList search(String param) throws Exception;

    boolean shareDoc(String...data);


    //and more operations

}

