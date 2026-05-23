# SimonSays 🎮

A **Paper 1.21** Minecraft server plugin that brings the classic "Simon Says" party game to your server as a multiplayer minigame.

Online players compete against each other by following (or ignoring) "Simon's" commands. Perform the wrong action at the wrong time and you're eliminated — last player standing wins!

## Features

- **Automated rounds** — Simon randomly picks an action each round and decides whether to say "Simon says..."
- **5 unique actions**: Jump, Sneak, Break a block, Drop an item, Right click
- **Elimination mechanics** — perform an action when Simon didn't say so (or *don't* when he did) and you're out
- **Spam protection** — 500ms cooldown per action per player prevents input abuse
- **Visual & audio feedback** — titles, sounds, and colored chat messages for every event
- **Mid-game quit** — leave anytime with `/simonsays quit`

## Requirements

- **Server**: [Paper](https://papermc.io/) 1.21 or later (uses Paper-specific events like `PlayerJumpEvent`)
- **Java**: 25

## Installation

1. Download the latest `SimonSays-1.0.0.jar` from [Releases](https://github.com/stormizn/SimonSays/releases).
2. Place the JAR in your Paper server's `plugins/` folder.
3. Restart or reload the server.

## Usage

| Command | Aliases | Description |
|---|---|---|
| `/simonsays start` | `/ss start` | Starts a new game with all online players (min. 2) |
| `/simonsays quit` | `/ss quit` | Leaves the current game |
| `/simonsays` | `/ss` | Shows game status / help |

### How to Play

1. A game round begins and Simon announces an action (e.g. **Jump**).
2. If Simon **said** "Simon says" — you **must** perform the action. If you don't, you're eliminated.
3. If Simon did **not** say "Simon says" — you **must** refrain. If you perform the action, you're eliminated.
4. The last player standing wins!

## Building

```bash
# Windows
gradlew.bat build

# macOS / Linux
./gradlew build
```

The compiled JAR will be in `build/libs/`.

## Project Structure

```
src/main/java/me/stormizn/simonsays/
├── SimonSays.java           # Main plugin entry point
├── commands/
│   └── SimonCommand.java    # /simonsays command handler
├── game/
│   ├── GameManager.java     # Manages active game lifecycle
│   ├── SimonGame.java       # Core game logic & state machine
│   └── SimonTask.java       # Enum of possible actions
└── listener/
    └── GameListener.java    # Bukkit event listeners
```

## License

All rights reserved.
