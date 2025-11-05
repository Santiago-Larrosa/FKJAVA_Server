package com.FK.game.server;

import java.net.InetAddress;

public class ConnectedClient {
    public InetAddress address;
    public int port;
    public int id;
    public RemotePlayer player;

    public ConnectedClient(InetAddress address, int port, int id) {
        this.address = address;
        this.port = port;
        this.id = id;
        this.player = new RemotePlayer();
    }
}
