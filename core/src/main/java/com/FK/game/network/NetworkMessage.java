package com.FK.game.network;
public enum NetworkMessage {
    // Cliente → Servidor
    CONNECT,           // Solicita conexión al servidor
    DISCONNECT,        // Cliente se desconecta
    INPUT_LEFT,        // Movimiento del jugador a la izquierda
    INPUT_RIGHT,       // Movimiento del jugador a la derecha
    INPUT_JUMP,        // Salto
    INPUT_ATTACK,      // Ataque normal
    INPUT_FIRE_ATTACK, // Ataque especial
    PING,              // Para test de latencia

    // Servidor → Cliente
    CONNECTED,         // Confirmación de conexión
    ENTITY_UPDATE,     // Posiciones/estados de jugadores y enemigos
    COIN_UPDATE,       // Actualización de monedas recogidas
    PORTAL_SPAWNED,    // Portal creado en la sala
    GAME_OVER,         // Cuando el jugador muere o termina la partida
    PONG;              // Respuesta a PING
}
