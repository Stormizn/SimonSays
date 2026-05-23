package me.stormizn.simonsays.game;

import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class GameManager {

    private final JavaPlugin plugin;
    private SimonGame currentGame;

    public GameManager(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public SimonGame startGame() {
        if (currentGame != null && currentGame.isRunning()) return null;
        currentGame = new SimonGame(this);
        currentGame.start();
        return currentGame;
    }

    public void endGame() {
        currentGame =  null;
    }

    public SimonGame getGame(Player player) {
        if (currentGame != null && currentGame.isRunning() && currentGame.isParticipant(player)) {
            return currentGame;
        }
        return null;
    }

    public boolean isPlaying(Player player) {
        return getGame(player) != null;
    }

    public boolean hasGame() {
        return currentGame != null && currentGame.isRunning();
    }

    public SimonGame getCurrentGame() {
        return currentGame;
    }

    public JavaPlugin getPlugin() { return plugin; }

}