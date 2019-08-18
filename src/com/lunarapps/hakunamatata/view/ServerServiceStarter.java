package com.lunarapps.hakunamatata.view;

import com.lunarapps.hakunamatata.network.SetUpSockets;

class ServerServiceStarter implements Runnable {

    public ServerServiceStarter() {
    }

    @Override
    public void run() {
        new SetUpSockets().runClientHandler();
    }
}
