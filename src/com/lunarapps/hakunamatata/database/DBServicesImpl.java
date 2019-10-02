package com.lunarapps.hakunamatata.database;

import com.lunarapps.hakunamatata.models.*;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;

import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.SecureRandom;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class DBServicesImpl implements DBService {

    private ResultSet resultSet;
    private Statement statement;

    private static final int ITERATIONS = 2 * 1000;
    private static final int DESIRED_KEY_LEN = 128;
    private static final int SALT_LEN = 20;

    public DBServicesImpl() {
        try {
            statement = DBConnectionSingleton.getDBConnectionSingleton().connection.createStatement();

        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    @Override
    public User getUser(String un, String pa) throws SQLException {
        return new UserBuilder(un, pa).getUser();
    }

    @Override
    public boolean shareDoc(String... data) {
        String query = "insert into STORED values(null, " + data[0] + "," + data[1] + "," + data[2] + "," + data[3] + "," + data[4] + ")";
        try {
            statement.executeQuery(query);
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean storeDoc(Doc doc) throws SQLException {
        String namie = doc.getName();
        User uploader = doc.getUploader();
        Type type = doc.getType();


        if (type == Type.IMAGE) {
            //image code

            Image image = (Image) doc;//my image class
            byte[] imagesBytes = image.getImagesBytes();

            //create directory, write the byte, store the path in database, handle result

            String serverPath = "Z:\\ServerFiles\\" + uploader.getUsername() + "\\";
//            String identifier = serverPath + java.io.File.separator + namie;
            java.io.File file = new java.io.File(serverPath);
            boolean x = file.mkdir();
            boolean y = writeBytesToFile(imagesBytes, serverPath, namie);
            if (x && y) {

                String query = "insert into STORED values(null,'" + namie + "','" + uploader.getUsername() + "','" +
                        doc.getAccess() + "','" + 0 + "','" + doc.getType() + "','" + serverPath +
                        java.io.File.separator + namie + "')";

                statement.executeQuery(query);

                return true;


            }
            return false;

        } else {
            //pdf code
        }


        return false;
    }

    private boolean writeBytesToFile(byte[] imageBytes, String destination, String namie) {
        try {
            FileOutputStream fos = new FileOutputStream(destination + java.io.File.separator + namie);
            fos.write(imageBytes);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean checkUser(String username, String pass) throws Exception {

        boolean validityTest = false;
        resultSet = statement.executeQuery("select * from USER where USERNAME='" + username + "'");

        while (resultSet.next()) {
            String encodedPass = resultSet.getString("PASSWORD");

            if (decoder(pass, encodedPass)) {
                validityTest = true;
            }
            break;
        }
        return validityTest;
    }


    @Override
    public ArrayList userHistory(User user) throws Exception {
        ArrayList<History> rec = new ArrayList<History>();
        History his;
        String query = ("select * from SEARCH_HISTORY where SEARCHER='" + user.getUsername() + "'");
        resultSet = statement.executeQuery(query);

        while (resultSet.next()) {
            String searcher = resultSet.getString("SEARCHER");
            String search = resultSet.getString("SEARCH");
            Date search_date = resultSet.getDate("SEARCH_DATE");

            his = new History(user, search, search_date);
            rec.add(his);
        }

        return rec;
    }

    @Override
    public boolean submitFeedback(Feedback feed) throws Exception {
        try {
            String query = "insert into FEEDBACK values(null,'" + feed.getCommenter().getUsername() + "','" + feed.getFeed_back()
                    + "','" + feed.getFeed_date() + "')";
            statement.executeQuery(query);
            return true;
        } catch (Exception e) {
            return false;
        }

    }

    @Override
    public boolean addUser(User u) {

        if (!usernameExists(u.getUsername())) {
            try {
                String query = "insert into USER values(null,'" + u.getFirstname() + "','" + u.getLastname() + "'," +
                        "'" + u.getSex() + "','" + u.getEmail() + "','" + u.getUsername() + "','" + getSaltedHash(u.getPassword()) + "');";

                statement.execute(query);
                return true;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    private boolean usernameExists(String un) {
        try {
            resultSet = statement.executeQuery("select 1 from USER where USERNAME='" + un + "'");
            return resultSet.first();
        } catch (SQLException ignored) {
        }
        return true;
    }

    @Override
    public ArrayList search(String param) throws Exception {

        ArrayList<ArrayList> result = new ArrayList<ArrayList>();
        ArrayList<Image> images = new ArrayList<Image>();
        ArrayList<File> files = new ArrayList<>();

        //my classes
        Image image;
        File file;

        //every file or image name that holds the param key word will be retrieved by the query given below
        String query = "SELECT * FROM STORED WHERE DOC_NAME LIKE '" + param + "%' OR DOC_NAME LIKE '% " + param + "%' "
                + "AND ACCESSIBILITY=PUBLIC";
        resultSet = statement.executeQuery(query);

        while (resultSet.next()) {


            String name = resultSet.getString("DOC_NAME");
            String uploaderName = resultSet.getString("UPLOADER");
            int downloadCount = resultSet.getInt("DOWNLOAD_COUNT");
            String path = resultSet.getString("PATH");
            Type type = (Type) resultSet.getObject("TYPE");

            FileInputStream fis = new FileInputStream(new java.io.File(path));
            byte[] imageBytes = IOUtils.toByteArray(fis);


            if (type == Type.FILE) {
                file = new File(name, new UserBuilder(uploaderName).getUser(), Accessibility.PUBLIC, Type.FILE, path,
                        downloadCount);
                files.add(file);
            } else {
                image = new Image(name, new UserBuilder(uploaderName).getUser(), Accessibility.PUBLIC, Type.IMAGE, path,
                        downloadCount, imageBytes);
                images.add(image);
            }

        }
        result.add(files);
        result.add(images);

        return result;
    }


    //these hash and decoder methods are straightforward
    private boolean decoder(String password, String stored) throws Exception {
        String[] saltAndHash = stored.split("\\$");
        if (saltAndHash.length != 2) {
            throw new IllegalStateException("The stored password must have the form 'salt$hash'");
        }

        String hashOfInput = hash(password, Base64.decodeBase64(saltAndHash[0]));//the decodeBase64 method decodes a base64 string to byte[]
        return hashOfInput.equals(saltAndHash[1]);//compare the user's hashed input with the actual hash
    }

    private String hash(String password, byte[] salt) throws Exception {
        if (password == null || password.length() == 0) {
            throw new IllegalArgumentException("Empty passwords are not acceptable");
        }
        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
        SecretKey key = factory.generateSecret(new PBEKeySpec(password.toCharArray(), salt, ITERATIONS, DESIRED_KEY_LEN));
        return Base64.encodeBase64String(key.getEncoded());
    }

    private String getSaltedHash(String password) throws Exception {
        byte[] salt = SecureRandom.getInstance("SHA1PRNG").generateSeed(SALT_LEN);
        // store the salt with the password
        return Base64.encodeBase64String(salt) + "$" + hasher(password, salt);
    }

    private String hasher(String password, byte[] salt) throws Exception {
        if (password == null || password.length() == 0) {
            throw new IllegalArgumentException("Empty passwords are not supported.");
        }
        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
        SecretKey key = factory.generateSecret(new PBEKeySpec(password.toCharArray(), salt, ITERATIONS, DESIRED_KEY_LEN));
        return Base64.encodeBase64String(key.getEncoded());
    }
}
