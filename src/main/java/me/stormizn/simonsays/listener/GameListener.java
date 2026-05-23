package me.stormizn.simonsays.listener;

import com.destroystokyo.paper.event.player.PlayerJumpEvent;
import me.stormizn.simonsays.game.GameManager;
import me.stormizn.simonsays.game.SimonGame;
import me.stormizn.simonsays.game.SimonTask;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;

public class GameListener implements Listener {

    private final GameManager gameManager;

    public GameListener(GameManager gameManager) {
        this.gameManager = gameManager;
    }

    @EventHandler
    public void onJump(PlayerJumpEvent event) {
        routeAction(event.getPlayer(), SimonTask.JUMP);
    }

    @EventHandler
    public void onSneak(PlayerToggleSneakEvent event) {
        if (!event.isSneaking()) return;
        routeAction(event.getPlayer(), SimonTask.SNEAK);
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        routeAction(event.getPlayer(), SimonTask.BREAK_BLOCK);
    }

    @EventHandler
    public void onDropItem(PlayerDropItemEvent event) {
        routeAction(event.getPlayer(), SimonTask.DROP_ITEM);
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        if (event.getAction().name().startsWith("LEFT_CLICK")) return;
        routeAction(event.getPlayer(), SimonTask.INTERACT);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        SimonGame game = gameManager.getCurrentGame();
        if (game != null && game.isParticipant(event.getPlayer())) {
            game.removePlayer(event.getPlayer());
        }
    }

    private void routeAction(Player player, SimonTask task) {
        SimonGame game = gameManager.getGame(player);
        if (game != null) {
            game.reportAction(player, task);
        }
    }

}
