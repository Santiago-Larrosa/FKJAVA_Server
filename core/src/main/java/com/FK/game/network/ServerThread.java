package com.FK.game.network;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap; 
import com.FK.game.core.GameContext;
import com.FK.game.entities.Player;

import com.FK.game.core.NetworkInputHandler;
import com.FK.game.server.RemotePlayer; 

public class ServerThread extends Thread {
    private static final int PORT = 54555;
    private volatile boolean running = true; 
    private DatagramSocket socket;
    private int nextId = 1;

    private Map<Integer, ConnectedClient> clientsById = new ConcurrentHashMap<>();
    
    private Map<InetSocketAddress, ConnectedClient> clientsByAddress = new ConcurrentHashMap<>();

    private Thread gameLoopThread;

    @Override
    public void run() {
        try {
            socket = new DatagramSocket(PORT);
            System.out.println("Servidor iniciado en el puerto " + PORT);

            this.gameLoopThread = new Thread(this::gameLogicLoop);
            this.gameLoopThread.start();

            byte[] buffer = new byte[1024];

            while (running) {
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                socket.receive(packet); 
                handlePacket(packet);
            }
        } catch (IOException e) {
            if (running) {
                System.err.println("Error de Socket: " + e.getMessage());
                e.printStackTrace();
            }
        } finally {
            stopServer();
        }
    }

    private void handlePacket(DatagramPacket packet) throws IOException {
        String msg = new String(packet.getData(), 0, packet.getLength());
        InetSocketAddress address = new InetSocketAddress(packet.getAddress(), packet.getPort());

        ConnectedClient client = clientsByAddress.get(address);

        if (client == null) {
            if (msg.equals("CONNECT")) {
                int assignedId = nextId++;
                System.out.println("Nuevo jugador conectando desde " + address + ", asignando ID: " + assignedId);

                NetworkInputHandler inputHandler = new NetworkInputHandler();
                RemotePlayer player = new RemotePlayer(); 

                ConnectedClient newClient = new ConnectedClient(assignedId, address, player, inputHandler);
                clientsById.put(assignedId, newClient);
                clientsByAddress.put(address, newClient);

                String response = "ASSIGN_ID:" + assignedId;
                sendPacket(response, address);

                if (GameContext.getScreen() != null) {
                    for (ConnectedClient existingClient : clientsById.values()) {
                        Player existingPlayer = GameContext.getScreen().getPlayerById(existingClient.id - 1);
                        String snapshot = buildPlayerUpdateMessage(existingClient.id, existingPlayer);
                        if (snapshot != null) {
                            sendPacket(snapshot, address);
                        }
                    }

                    Player joinedPlayer = GameContext.getScreen().getPlayerById(assignedId - 1);
                    String joinedSnapshot = buildPlayerUpdateMessage(assignedId, joinedPlayer);
                    if (joinedSnapshot != null) {
                        for (ConnectedClient existingClient : clientsById.values()) {
                            if (existingClient.id == assignedId) {
                                continue;
                            }
                            sendPacket(joinedSnapshot, existingClient.address);
                        }
                    }
                }

            }
            
        } else {
            if (msg.startsWith("INPUT:")) {
                String[] parts = msg.split(":");
                String input = parts[2];
                Player player = GameContext.getScreen().getPlayerById(client.id-1);
                player.getInputHandler().handleNetworkInput(input);
                //System.out.println("Recibido input del jugador " + client.id + ": " + input);
            }
        }
    }
    private void broadcastMapChange(String mapName) {
    System.out.println(" Enviando cambio de mapa a: " + mapName);
    for (ConnectedClient c : clientsById.values()) {
        sendPacket("CHANGE_MAP:" + mapName, c.address);
    }
}
public void sendPacketToAll(String msg) {
    for (ConnectedClient c : clientsById.values()) {
        sendPacket(msg, c.address);
    }
}
    private void gameLogicLoop() {
        try {
            while (running) {
                for (ConnectedClient c : clientsById.values()) {
                    c.player.update(1 / 30f); 
                }

                for (ConnectedClient targetClient : clientsById.values()) {
                    for (ConnectedClient playerState : clientsById.values()) {

                        Player player = GameContext.getScreen().getPlayerById(playerState.id-1);
                        int id = playerState.id;

                        String updateMessage = buildPlayerUpdateMessage(id, player);
                        if (updateMessage != null) {
                            sendPacket(updateMessage, targetClient.address);
                        }
                    }
                }

                Thread.sleep(33);
            }
        } catch (InterruptedException e) {
            System.out.println("Hilo del juego interrumpido.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void sendPacket(String msg, InetSocketAddress targetAddress) {
        if (socket == null || socket.isClosed()) return;
        try {
            byte[] data = msg.getBytes();
            DatagramPacket packet = new DatagramPacket(data, data.length, targetAddress);
            socket.send(packet);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void stopServer() {
        if (!running) return; 
        
        running = false;

        if (socket != null && !socket.isClosed()) {
            socket.close();
        }

        if (gameLoopThread != null) {
            gameLoopThread.interrupt();
        }
        
        System.out.println("Servidor detenido.");
    }

    private String buildPlayerUpdateMessage(int id, Player player) {
        if (player == null) {
            return null;
        }

        float x = player.getX();
        float y = player.getY();
        StateMessage state = player.getStateMessage();
        String stateName = state != null ? state.name() : StateMessage.PLAYER_IDLE.name();
        String facingName = player.isMovingRight()
            ? FacingDirection.RIGHT.name()
            : FacingDirection.LEFT.name();

        return "UPDATE_PLAYER:" + id + ":" + x + ":" + y + ":" + stateName + ":" + facingName;
    }

    private class ConnectedClient {
        int id;
        InetSocketAddress address;
        RemotePlayer player;
        NetworkInputHandler inputHandler;

        public ConnectedClient(int id, InetSocketAddress address, RemotePlayer player, NetworkInputHandler inputHandler) {
            this.id = id;
            this.address = address;
            this.player = player;
            this.inputHandler = inputHandler;
        }
    }
}
