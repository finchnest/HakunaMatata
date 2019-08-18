package com.lunarapps.hakunamatata.network;

import com.lunarapps.hakunamatata.database.DBServicesImpl;
import com.lunarapps.hakunamatata.database.ServiceNames;
import com.lunarapps.hakunamatata.models.Feedback;
import com.lunarapps.hakunamatata.models.Image;
import com.lunarapps.hakunamatata.models.User;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

public class ClientHandler implements Runnable {

    private Socket sock;
    private DBServicesImpl dbServicesImpl;

    public ClientHandler(Socket clientSocket) {
        try {
            sock = clientSocket;
            dbServicesImpl = new DBServicesImpl();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void run() {

        while (true) {
            try {
                InputStreamReader servStream = new InputStreamReader(sock.getInputStream());
                String reader = new BufferedReader(servStream).readLine();

                String[] splitted = reader.split("&%");

                ServiceNames dbServiceNames = ServiceNames.valueOf(splitted[0]);

                switch (dbServiceNames) {
                    case CHECK:
                        ObjectInputStream objStream = new ObjectInputStream(sock.getInputStream());
                        String[] logVals = ((String) objStream.readObject()).split("!@#");
                        boolean validity = dbServicesImpl.checkUser(logVals[0], logVals[1]);
                        String booleanValue = ((Boolean) validity).toString();

                        PrintWriter writer = new PrintWriter(sock.getOutputStream());
                        writer.print(booleanValue);

                        if (validity) {
                            User user = dbServicesImpl.getUser(logVals[0], logVals[1]);
                            ObjectOutputStream userObj = new ObjectOutputStream(sock.getOutputStream());
                            userObj.writeObject(user);
                        }
                        break;

                    case ADD_USER:

                        ObjectInputStream objStreamU = new ObjectInputStream(sock.getInputStream());
                        User u = (User) objStreamU.readObject();
                        boolean added = dbServicesImpl.addUser(u);
                        String regValue = ((Boolean) added).toString();

                        PrintWriter addWriter = new PrintWriter(sock.getOutputStream());
                        addWriter.print(regValue);

                        break;
                    case MY_IMAGES:

                        //for now i dnt know how i am gonna store file path in the database
                        //and the real file in the disk. when i understand that imma use that
                        //understanding to create array objects using the retrieved file
                        //attach it to its name and send to the client iterator

                        //call to the DBServicesImpl method to retrieve the images
                        //each image is sent as byte arrays object with its respective name

                        break;

                    case MY_DOCS:

                        //call to the DBServicesImpl method to retrieve the pdf docs
                        //each pdf is sent as byte arrays object with its respective names

                        break;
                    case SHARED_IMAGES:

                        //call to the DBServicesImpl method to retrieve the images
                        //each image is sent as byte arrays object with its respective name

                        break;

                    case SHARED_DOCS:

                        //call to the DBServicesImpl method to retrieve the pdf docs
                        //each pdf is sent as byte arrays object with its respective name

                        break;

                    case HISTORY:
                        ObjectInputStream objHis = new ObjectInputStream(sock.getInputStream());
                        User us = (User) objHis.readObject();
                        ArrayList userHistory = dbServicesImpl.userHistory(us);

                        ObjectOutputStream userRec = new ObjectOutputStream(sock.getOutputStream());
                        userRec.writeObject(userHistory);

                        break;
                    case FEEDBACK:
                        ObjectInputStream objFeed = new ObjectInputStream(sock.getInputStream());
                        Feedback feed = (Feedback) objFeed.readObject();
                        boolean feedReceived = dbServicesImpl.submitFeedback(feed);
                        String boolVal = ((Boolean) feedReceived).toString();

                        PrintWriter feedVal = new PrintWriter(sock.getOutputStream());
                        feedVal.write(boolVal);

                        break;
                    case SEARCH:

                        ArrayList searchResult = dbServicesImpl.search(splitted[1]);
                        ObjectOutputStream searchStream = new ObjectOutputStream(sock.getOutputStream());
                        searchStream.writeObject(searchResult);

                        break;
                    case UPLOAD:
                        ObjectInputStream objIma = new ObjectInputStream(sock.getInputStream());
                        com.lunarapps.hakunamatata.models.Image ima = (com.lunarapps.hakunamatata.models.Image) objIma.readObject();

                        PrintWriter uploadWriter = new PrintWriter(sock.getOutputStream());
                        uploadWriter.print(dbServicesImpl.storeDoc(ima));

                    case SHARE:
                        ObjectInputStream storeD=new ObjectInputStream(sock.getInputStream());
                        String[] mes=((String)storeD.readObject()).split("!@#$");

                        PrintWriter sharedRes = new PrintWriter(sock.getOutputStream());
                        sharedRes.print(dbServicesImpl.shareDoc(mes));

                        break;
                    default:
                        break;


                }

            } catch (Exception ex) {
                ex.printStackTrace();
            }
            finally {
                //close streams
            }
        }
    }


}