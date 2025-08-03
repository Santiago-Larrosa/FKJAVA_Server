# FurnaceKnightJAVA

A [libGDX](https://libgdx.com/) project generated with [gdx-liftoff](https://github.com/libgdx/gdx-liftoff).

This project was generated with a template including simple application launchers and an `ApplicationAdapter` extension that draws libGDX logo.

## Integrants
Santiago Larrosa

## Platforms

- `core`: Main module with the application logic shared by all platforms.
- `lwjgl3`: Primary desktop platform using LWJGL3; was called 'desktop' in older docs.

## Gradle

This project uses [Gradle](https://gradle.org/) to manage dependencies.
The Gradle wrapper was included, so you can run Gradle tasks using `gradlew.bat` or `./gradlew` commands.
Useful Gradle tasks and flags:

- `--continue`: when using this flag, errors will not stop the tasks from running.
- `--daemon`: thanks to this flag, Gradle daemon will be used to run chosen tasks.
- `--offline`: when using this flag, cached dependency archives will be used.
- `--refresh-dependencies`: this flag forces validation of all dependencies. Useful for snapshot versions.
- `build`: builds sources and archives of every project.
- `cleanEclipse`: removes Eclipse project data.
- `cleanIdea`: removes IntelliJ project data.
- `clean`: removes `build` folders, which store compiled classes and built archives.
- `eclipse`: generates Eclipse project data.
- `idea`: generates IntelliJ project data.
- `lwjgl3:jar`: builds application's runnable jar, which can be found at `lwjgl3/build/libs`.
- `lwjgl3:run`: starts the application.
- `test`: runs unit tests (if any).

## Current project status

## Current State of the Game

The project is currently in the stage of a playable demo. This demo includes the following features:

### Player Functionality

The player character supports all core movement and attack states, with directional variations:

- Walking (left and right)
- Jumping (left and right)
- Falling (left and right)
- Ground attack (left and right)
- Saw attack (left and right)
- Fire attack (left and right)
- Receiving damage (reuses the jump animation; no separate damage animation)

### Enemy Implementation

The demo introduces the first enemy type, **Bolb**, which uses its own state machine tailored to its entity type. Bolb supports the following states:

- Walking (uses the same animation for both directions)
- Attacking (currently uses a single default Bolb animation)
- Receiving damage (reuses the same default Bolb animation)

### Environment and Map

- Map designed in **Tiled**, with custom tileset integration
- Functional collision system
- Entity interaction and response system

### Audio

- Specific sound effects for distinct in-game actions

### Heads-Up Display (HUD)

- A HUD element that visually represents the fire attack's charge state

### Screen and Display Management

- Uses a **StretchViewport** to accommodate different screen resolutions
- Multi-screen architecture, including:
  - **GameScreen**: Main gameplay screen containing the game loop
  - **LoadingScreen**: Transitional screen with a side progress bar to indicate resource loading and prevent black screen delays
  - **MenuScreen**: A basic pause menu accessible via the Escape key

## Trailer Video:
 - https://youtu.be/MHtqUcFfqkk
