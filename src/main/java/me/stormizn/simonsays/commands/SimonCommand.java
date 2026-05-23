package me.stormizn.simonsays.commands;

import me.stormizn.simonsays.game.GameManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SimonCommand implements CommandExecutor {

    private final GameManager gameManager;

    public SimonCommand(GameManager gameManager) {
        this.gameManager = gameManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("§cOnly players can use this command!");
            return true;
        }

        if (args.length > 0 && args[0].equalsIgnoreCase("quit")) {
            if (gameManager.isPlaying(player)) {
                gameManager.getGame(player).removePlayer(player);
            } else {
                player.sendMessage("§cYou're not in a game!");
            }
            return true;
        }

        if (args.length > 0 && args[0].equalsIgnoreCase("start")) {
            if (gameManager.hasGame()) {
                player.sendMessage("§cA game is already running! Use §f/simonsays §cto join.");
            } else {
                gameManager.startGame();
            }
            return true;
        }

        if (gameManager.isPlaying(player)) {
            player.sendMessage("§eYou're in the game! Use §f/simonsays quit §eto leave.");
        } else if (gameManager.hasGame()) {
            player.sendMessage("§eA game is running but you're not a participant.");
            player.sendMessage("§7You can't join mid-game. Wait for the next round!");
        } else {
            player.sendMessage("§6Simon Says");
            player.sendMessage("§7Use §f/simonsays start §7to begin with all online players!");
        }
        return true;
    }

}
