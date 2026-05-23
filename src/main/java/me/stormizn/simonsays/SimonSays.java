package me.stormizn.simonsays;

import me.stormizn.simonsays.commands.SimonCommand;
import me.stormizn.simonsays.game.GameManager;
import me.stormizn.simonsays.listener.GameListener;
import org.bukkit.plugin.java.JavaPlugin;

public class SimonSays extends JavaPlugin {

    private GameManager gameManager;

    @Override
    public void onEnable() {
        gameManager = new GameManager(this);
        getCommand("simonsays").setExecutor(new SimonCommand(gameManager));
        getServer().getPluginManager().registerEvents(new GameListener(gameManager), this);
        System.out.println("Enabling SimonSays.....");
    }

    @Override
    public void onDisable() {
        if (gameManager != null) {
            gameManager.endGame();
        }
        System.out.println("Disabling SimonSays.....");
    }

}
