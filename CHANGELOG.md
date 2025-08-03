# Changelog

Este archivo sigue el formato de [Keep a Changelog](https://keepachangelog.com/es-ES/1.0.0/) y documenta todos los cambios importantes realizados en el proyecto. Las versiones siguen el esquema [SemVer](https://semver.org/lang/es/).

## [3.0.0] - 2025-08-03
### Added
- Nuevos estados del jugador con animaciones propoas.
    - `FallingAttackState` : ataque que ocurre si presiono X estando en el aire lo que proboca que calcule una trayectoria de 45° en la direccion que mira.
    - `FireAttackState` : ataque lateral a distancia que ocurre estando en el suelo y presionando la Z y teniendo la carga completa del ataque.
    - `DamageState` : Estado que tambien utliza la entidad `Bolb` para manejar el daño y el knockback sin que se acumule de más.

- Refactorización de `Player` para crear `Entity` del que nace `Bolb`.

- Inclusion de dos nuevos `Rectangles` en `Entity` que son CollisionBox y `DamageBox`.

- Metodo en `Entity` que funje de debug para los colliders de las entidades y de sus areas de daño.

- Estados particulares de `Bolb`:
    - `BolbWalkState` : Utiliza la animacion unica de Bolb para moverse lateralmente sobre las plataformas evitando salirse.
    - `BolbAttackState` : Estado donde genera daño estando en el suelo, teniendo cerca al jugador y respetando el cooldown de su ataque.
    - `bolbDamageState` : Estado para manejar el daño y el knockback sin que se acumule de más.

- Manejo de sonidos en un nuevo directorio `sounds` que utiliza una logica similar a la carga de animaciones que incluye:
    - `SoundCache`.
    - `SoundType`.

- Refactorizacion de los enum de animaciones para separar los tipos de animaciones segun Player y Enemy.

- Refactorizacion de la maquina de estados como una interfaz que recibe el tipo de entidad como argumento:
    - `EntityState`
    - `EntityStateMachine`

- Mapa con colisiones importado de Tiled.
    - Directorio `maps` en `assets`

- Hud para el jugador que muestra la carga de su ataque `FireAttackHud`.

- Añadi el archivo `GameContext` para tener variables globales.


## [2.0.0] - 2025-07-05
### Added
- Refactorizacion del codigo:
    - Nuevos paquetes: `animations`, `core`, `entities`, `screens`, `states`.
    - Nueva interfaz para generalizar los estados del jugador fuera del `Player`.
    - Factory de animaciones.
- Metodo de carga unica de recursos.
- Pantalla de carga en `LoadingScreen`.
- Pantalla de menu en `MenuScreen`.
- Camara adaptable en `GameScreen` que se mueve cada que el personaje esta fuera de rango de pantalla.  

## [1.1.0] - 2025-05-17
### Added
- Sistema de animaciones para caminar, estar quieto, cargar salto, saltar, caer y atacar, tanto mirando a la derecha como a la izquierda.
- Estados de salto: `NONE`, `PASS`, `JUMPING`, `FALLING`.
- Lógica de ataque con duración limitada y control de interrupción de movimiento durante el ataque.
- Implementación de salto cargado manteniendo presionada la tecla `SPACE`.
- Carga y renderizado de `TextureRegion` desde spritesheets con `AnimationHandler`.

## [1.0.0] - 2025-05-14
### Added
- Configuración inicial del proyecto con LibGDX mediante el asistente **Liftoff**.
- Creación de archivos base: `README.md`, `CHANGELOG.md`.
- Configuración inicial de la [Wiki del repositorio](https://github.com/Santiago-Larrosa/FKJAVA/wiki) con la propuesta del proyecto.
