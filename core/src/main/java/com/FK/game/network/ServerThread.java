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
import com.FK.game.core.MainGame;
import com.FK.game.core.PlayerData;
import com.FK.game.core.UpgradeManager;
import java.util.List;
import java.util.ArrayList;
import com.badlogic.gdx.Gdx;

public class ServerThread extends Thread {
    private static final int PORT = 54555;
    private volatile boolean running = true; 
    private DatagramSocket socket;
    private int nextId = 1;
    private int nextEntityId = 1000;
    private Thread broadcastThread;
    private final List<Integer> releasedIds = new ArrayList<>();

    private Map<Integer, ConnectedClient> clientsById = new ConcurrentHashMap<>();
    
    private Map<InetSocketAddress, ConnectedClient> clientsByAddress = new ConcurrentHashMap<>();

    private Thread gameLoopThread;

    @Override
    public void run() {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
        try {
            System.out.println("[SERVER] Shutdown Hook ejecutado — enviando aviso de desconexión a clientes...");
            broadcastServerShutdown();
        } catch (Exception e) {
            System.err.println("[SERVER] Error al enviar mensaje de desconexión en shutdown hook: " + e.getMessage());
        }
    }));
        try {
            try {
                socket = new DatagramSocket(null);
                socket.setReuseAddress(true);
                socket.bind(new InetSocketAddress(PORT));

                System.out.println("Servidor iniciado en el puerto " + PORT);
                startBroadcastListener();
            } catch (java.net.BindException e) {
                System.err.println("[ERROR] El puerto " + PORT + " ya está en uso. No se pudo iniciar el servidor.");
                e.printStackTrace(); // solo muestra el error, no lo resuelve
                return; // o System.exit(1); si querés cerrar el programa
            } catch (IOException e) {
                System.err.println("[ERROR] No se pudo crear el socket UDP: " + e.getMessage());
                e.printStackTrace();
                return;
            }

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

        private void startBroadcastListener() {
    broadcastThread = new Thread(() -> {
        DatagramSocket broadcastSocket = null;
        try {
            broadcastSocket = new DatagramSocket(null);
            broadcastSocket.setReuseAddress(true);
            broadcastSocket.bind(new InetSocketAddress(54556));
            broadcastSocket.setBroadcast(true);

            byte[] buffer = new byte[256];
            System.out.println("[SERVER] Escuchando broadcasts en puerto 54556...");

            while (running) {
                // Detener si ya hay el máximo de clientes
                if (clientsById.size() >= 2) {
                    System.out.println("[SERVER] Límite de jugadores alcanzado. Deteniendo broadcast...");
                    break;
                }

                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                broadcastSocket.receive(packet);

                String msg = new String(packet.getData(), 0, packet.getLength());
                if (msg.equals("DISCOVER_FK_SERVER")) {
                    String response = "FK_SERVER_RESPONSE:" + InetAddress.getLocalHost().getHostAddress();
                    byte[] data = response.getBytes();
                    DatagramPacket responsePacket = new DatagramPacket(
                        data, data.length, packet.getAddress(), packet.getPort());
                    broadcastSocket.send(responsePacket);
                    System.out.println("[SERVER] Respondió al broadcast de " + packet.getAddress());
                }
            }

        } catch (IOException e) {
            if (running) e.printStackTrace();
        } finally {
            if (broadcastSocket != null && !broadcastSocket.isClosed()) {
                broadcastSocket.close();
                System.out.println("[SERVER] Socket de broadcast cerrado correctamente.");
            }
        }
    });
    broadcastThread.start();
}




    private void handlePacket(DatagramPacket packet) throws IOException {
        String msg = new String(packet.getData(), 0, packet.getLength());
        InetSocketAddress address = new InetSocketAddress(packet.getAddress(), packet.getPort());
        if (GameContext.getScreen() == null) {
            return;
        }
        MainGame game = GameContext.getScreen().getGame();
        ConnectedClient client = clientsByAddress.get(address);

        if (client == null) {
            if (msg.equals("CONNECT")) {
                int assignedId;
                    if (!releasedIds.isEmpty()) {
                        assignedId = releasedIds.remove(0); // 🔁 reutiliza la primera ID disponible
                        System.out.println("Reutilizando ID liberada: " + assignedId);
                    } else {
                        assignedId = nextId++; // 🔸 asigna una nueva si no hay liberadas
                        System.out.println("Asignando nueva ID: " + assignedId);
                    }


                NetworkInputHandler inputHandler = new NetworkInputHandler();
                RemotePlayer player = new RemotePlayer(); 

                ConnectedClient newClient = new ConnectedClient(assignedId, address, player, inputHandler);
                clientsById.put(assignedId, newClient);
                clientsByAddress.put(address, newClient);

                String response = "ASSIGN_ID:" + assignedId;
                sendPacket(response, address);
                GameContext.getScreen().sendFullEntitySnapshotTo(address);
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
            
        } else if (msg.startsWith("INPUT:")) {
                String[] parts = msg.split(":");
                String input = parts[2];
                Player player = GameContext.getScreen().getPlayerById(client.id-1);
                player.getInputHandler().handleNetworkInput(input);
                //System.out.println("Recibido input del jugador " + client.id + ": " + input);
        } else if (msg.equals("UPGRADE_DAMAGE")) {
            Player p = GameContext.getScreen().getPlayerById(client.id - 1);
            if (p != null) {
                PlayerData pd = p.getPlayerData();
                GameContext.getScreen().getUpgradeManager().purchaseDamageUpgrade(pd);
                sendPacket("UPGRADE_CONFIRM:" + pd.coinCount, client.address);
            }
        } else if (msg.equals("UPGRADE_HEALTH")) {
            Player p = GameContext.getScreen().getPlayerById(client.id - 1);
            if (p != null) {
                PlayerData pd = p.getPlayerData();
                GameContext.getScreen().getUpgradeManager().purchaseHealthUpgrade(pd);
                sendPacket("UPGRADE_CONFIRM:" + pd.coinCount, client.address);
            }
        } else if (msg.equals("CLOSE_UPGRADE_WINDOW")) {
            Player p = GameContext.getScreen().getPlayerById(client.id-1);
            GameContext.getScreen().closeUpgradeMenu();
            if (p != null) {
                p.setUpgradeMenuOpen(false);
            }
        }else if (msg.equals("REQUEST_UPGRADE_DATA")) {
            Player p = GameContext.getScreen().getPlayerById(client.id - 1);
            if (client != null) {
                PlayerData data = p.getPlayerData(); // o tu estructura de progreso
                UpgradeManager manager = GameContext.getScreen().getUpgradeManager();

                int coins = data.coinCount;
                int dmgLevel = data.attackDamageLevel;
                int hpLevel = data.healthLevel;
                int dmgCost = manager.getDamageUpgradeCost(data);
                int hpCost = manager.getHealthUpgradeCost(data);
                
                String message = "UPGRADE_DATA:" + coins + ":" + dmgLevel + ":" + hpLevel + ":" + dmgCost + ":" + hpCost;
                System.out.println("Enviando datos de mejora a jugador " + client.id + ": " + message);
                sendPacket(
                    message,
                    address
                );
            }
        }else if (msg.startsWith("DISCONNECT:")) {
            int id = Integer.parseInt(msg.split(":")[1]);
            ConnectedClient removed = clientsById.remove(id);
            if (removed != null) {
                clientsByAddress.remove(removed.address);
                releasedIds.add(id);
                System.out.println("[SERVER] Jugador desconectado: " + id);
                if (clientsById.isEmpty()) {
                    nextId = 1;
                    releasedIds.clear();
                    System.out.println("[SERVER] Todos los jugadores desconectados. IDs reiniciadas.");
                    notifyServerEmpty();
            }
        }

    }
   /* private void broadcastMapChange(String mapName) {
    System.out.println(" Enviando cambio de mapa a: " + mapName);
    for (ConnectedClient c : clientsById.values()) {
        sendPacket("CHANGE_MAP:" + mapName, c.address);
    }*/
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
    public void sendPacketTo(int id, String msg) {
    ConnectedClient client = clientsById.get(id + 1);
    if (client != null && client.address != null) {
        sendPacket(msg, client.address);
    } 
}


    public void sendPacket(String msg, InetSocketAddress targetAddress) {
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
        if (broadcastThread != null && broadcastThread.isAlive()) {
            broadcastThread.interrupt();
        }

        running = false;

        if (socket != null && !socket.isClosed()) {
            socket.close();
        }

        if (gameLoopThread != null) {
            gameLoopThread.interrupt();
        }
        
        System.out.println("Servidor detenido.");
    }

    private void notifyServerEmpty() {
    Gdx.app.postRunnable(() -> {
        if (GameContext.getScreen().getGame() != null) {
            GameContext.getScreen().getGame().playerData.resetOnReload();
            GameContext.getScreen().getGame().playerData2.resetOnReload();
            GameContext.getScreen().getGame().returnToServerLauncher();
        }
    });
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
    public int generateEntityId() {
    return nextEntityId++;
}

public void broadcastServerShutdown() {
    try {
        for (ConnectedClient c : clientsById.values()) {
            sendPacket("SERVER_SHUTDOWN", c.address);
        }
        System.out.println("[SERVER] Aviso de cierre enviado a todos los clientes.");
    } catch (Exception e) {
        System.err.println("[SERVER] Error al enviar mensajes de cierre: " + e.getMessage());
    }
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
