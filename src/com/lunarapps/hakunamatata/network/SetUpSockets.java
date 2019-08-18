package com.lunarapps.hakunamatata.network;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class SetUpSockets {

    private ArrayList<String> connectedClients;
    private int clientsNum = 0;

    public SetUpSockets() {
        Thread.currentThread().setName("My Runnable Server");
    }

    public void runClientHandler() {

        connectedClients = new ArrayList<String>();

        try {

            ServerSocket serverSock = new ServerSocket(44444);

            while (true) {
                Socket clientSocket = serverSock.accept();
                connectedClients.add(clientSocket.getInetAddress().toString() + "#@!" + clientSocket.getPort());
                clientsNum++;

                Thread t = new Thread(new ClientHandler(clientSocket));
                t.start();
                System.out.println("MULTI-THREADED SERVER STARTED!");

            }

        } catch (Exception ex) {
            ex.printStackTrace();
            ex.getMessage();
        }
    }


    public ArrayList<String> getConnectedClients() {
        return connectedClients;
    }

    public int getClientsNum() {
        return clientsNum;
    }
}
