package me.stormizn.simonsays.game;

import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import java.util.*;

public class SimonGame {

    private final GameManager gameManager;
    private final Random random = new Random();
    private final List<Player> participants = new ArrayList<>();
    private final Set<Player> eliminated = new HashSet<>();
    private final Map<UUID, Boolean> didAction = new HashMap<>();
    private final Map<UUID, Long> lastActionTime = new HashMap<>();

    private SimonTask currentTask;
    private boolean simonSaid;
    private boolean running = false;
    private int round = 0;
    private BukkitTask roundTask;

    public SimonGame(GameManager gameManager) {
        this.gameManager = gameManager;
    }

    public void start() {
        participants.addAll(Bukkit.getOnlinePlayers());
        eliminated.clear();

        if (participants.size() < 2) {
            broadcast("§cNeed at least 2 players to start Simon Says!");
            gameManager.endGame();
            return;
        }

        running = true;
        broadcast("§6§l§m⏤⏤⏤⏤⏤⏤⏤⏤⏤⏤⏤⏤⏤⏤⏤⏤⏤⏤⏤⏤");
        broadcast("§6§l         SIMON SAYS");
        broadcast("§7    Do what Simon says, or get eliminated!");
        broadcast("§e    " + participants.size() + " players");
        broadcast("§6§l§m⏤⏤⏤⏤⏤⏤⏤⏤⏤⏤⏤⏤⏤⏤⏤⏤⏤⏤⏤⏤");
        playSoundAll(Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 1);

        Bukkit.getScheduler().runTaskLater(gameManager.getPlugin(), this::nextRound, 60L);
    }

    private void nextRound() {
        if (!running) return;

        round++;
        currentTask = SimonTask.values()[random.nextInt(SimonTask.values().length)];
        simonSaid = random.nextBoolean();
        didAction.clear();
        lastActionTime.clear();

        for (Player p : participants) {
            if (!eliminated.contains(p)) {
                didAction.put(p.getUniqueId(), false);
            }
        }

        int alive = getAliveCount();
        for (Player p : getAlive()) {
            if (simonSaid) {
                p.sendTitle("§a§lSimon says", "§f" + currentTask.getDisplay(), 10, 50, 10);
                p.sendMessage("§a§lSimon says: §f" + currentTask.getDisplay());
            } else {
                p.sendTitle("§f" + currentTask.getDisplay(), "§7(Simon didn't say)", 10, 50, 10);
                p.sendMessage("§f" + currentTask.getDisplay() + " §7(Simon didn't say)");
            }
            p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1, 1.5f);
        }
        broadcast("§7Round §e" + round + " §7| §a" + alive + " alive");

        roundTask = Bukkit.getScheduler().runTaskLater(gameManager.getPlugin(), this::evaluate, 80L);
    }

    public void reportAction(Player player, SimonTask task) {
        if (!running) return;
        if (roundTask == null) return;
        if (task != currentTask) return;
        if (eliminated.contains(player)) return;
        if (!didAction.containsKey(player.getUniqueId())) return;

        long now = System.currentTimeMillis();
        long last = lastActionTime.getOrDefault(player.getUniqueId(), 0L);
        if (now - last < 500) return;
        lastActionTime.put(player.getUniqueId(), now);

        didAction.put(player.getUniqueId(), true);

        if (!simonSaid) {
            player.sendMessage("§c§lYou did the task when Simon didn't say! Eliminated!");
            eliminate(player);
        }
    }

    private void evaluate() {
        if (!running) return;

        List<Player> toEliminate = new ArrayList<>();
        for (Player p : participants) {
            if (eliminated.contains(p)) continue;
            boolean didIt = didAction.getOrDefault(p.getUniqueId(), false);

            if (simonSaid && !didIt) {
                toEliminate.add(p);
            }
        }

        if (simonSaid) {
            broadcast("§eSimon said: §f" + currentTask.getDisplay());
        } else {
            broadcast("§eSimon didn't say it!");
        }

        if (toEliminate.isEmpty()) {
            broadcast("§aNo one was eliminated!");
        } else {
            StringBuilder names = new StringBuilder();
            for (Player p : toEliminate) {
                names.append("§c").append(p.getName()).append("§7, ");
                eliminate(p);
            }
            names.setLength(names.length() - 2);
            broadcast("§7Eliminated: " + names.toString());
        }

        int alive = getAliveCount();
        if (alive <= 1) {
            if (alive == 1) {
                Player winner = getAlive().get(0);
                broadcast("");
                broadcast("§6§l§m⏤⏤⏤⏤⏤⏤⏤⏤⏤⏤⏤⏤⏤⏤⏤⏤⏤⏤⏤⏤");
                broadcast("§6§l         GAME OVER");
                broadcast("§a§l    " + winner.getName() + " wins!");
                broadcast("§7    " + round + " rounds played");
                broadcast("§6§l§m⏤⏤⏤⏤⏤⏤⏤⏤⏤⏤⏤⏤⏤⏤⏤⏤⏤⏤⏤⏤");
                playSoundAll(Sound.ENTITY_PLAYER_LEVELUP, 1, 1);
            } else {
                broadcast("§c§lNo one wins! Everyone was eliminated!");
                playSoundAll(Sound.ENTITY_ENDERMAN_DEATH, 1, 1);
            }
            endGame();
        } else {
            broadcast("§7" + alive + " players remain!");
            roundTask = Bukkit.getScheduler().runTaskLater(gameManager.getPlugin(), this::nextRound, 60L);
        }
    }

    private void eliminate(Player player) {
        eliminated.add(player);
        player.playSound(player.getLocation(), Sound.ENTITY_ENDERMAN_DEATH, 1, 1);
        player.sendTitle("§c§lEliminated!", "§7You're out!", 5, 30, 5);
    }

    private void endGame() {
        running = false;
        if (roundTask != null) {
            roundTask.cancel();
            roundTask = null;
        }
        gameManager.endGame();
    }

    public void removePlayer(Player player) {
        if (!participants.contains(player)) return;
        eliminated.add(player);
        player.sendMessage("§cYou left the game.");
        int alive = getAliveCount();

        if (alive <= 1 && running) {
            if (roundTask != null) roundTask.cancel();
            if (alive == 1) {
                Player winner = getAlive().get(0);
                broadcast("§a§l" + winner.getName() + " wins! (opponent left)");
            } else {
                broadcast("§c§lGame ended — everyone left!");
            }
            endGame();
        }
    }

    public boolean isParticipant(Player player) {
        return participants.contains(player);
    }

    public boolean isRunning() { return running; }

    private List<Player> getAlive() {
        List<Player> alive = new ArrayList<>();
        for (Player p : participants) {
            if (!eliminated.contains(p)) alive.add(p);
        }
        return alive;
    }

    private int getAliveCount() {
        int count = 0;
        for (Player p : participants) {
            if (!eliminated.contains(p)) count++;
        }
        return count;
    }

    private void playSoundAll(Sound sound, float volume, float pitch) {
        for (Player p : participants) {
            p.playSound(p.getLocation(), sound, volume, pitch);
        }
    }

    private void broadcast(String message) {
        for (Player p : participants) {
            p.sendMessage(message);
        }
    }

}
