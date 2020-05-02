package net.kappabyte.spigot.eventapi.game;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;

import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.potion.PotionEffect;
import org.bukkit.scoreboard.ScoreboardManager;

import net.kappabyte.spigot.eventapi.API;
import net.kappabyte.spigot.eventapi.PlayerInv;
import net.kappabyte.spigot.eventapi.game.states.GameState;
import net.md_5.bungee.api.ChatColor;
import net.minecraft.server.v1_15_R1.EntityFox.i;

public abstract class Game implements Runnable, Listener {
    public static Game currentGame;

    public LinkedHashMap<String,GamePlayer> players = new LinkedHashMap<String,GamePlayer>();
    public LinkedHashMap<String,Player> spectators = new LinkedHashMap<String,Player>();

    /**
     * A list of player rankings. The player who winds the event should be in key 0, second place at key 1, etc
     */
    public ArrayList<GamePlayer> rankedPlayerList = new ArrayList<GamePlayer>();
    
    public int initialPlayerCount = 0;

    public GameState currentState;

    public boolean gameStart;
    
    public String name;

    private int taskId;

    private HashMap<Player, PlayerInv> playerInventories = new HashMap<Player, PlayerInv>();
    private HashMap<Player, GameMode> playerGamemodes = new HashMap<Player, GameMode>();
    private HashMap<Player, Collection<PotionEffect>> playerEffects = new HashMap<Player, Collection<PotionEffect>>();
    private HashMap<Player, Double> playerHealth = new HashMap<Player, Double>();
    private HashMap<Player, Integer> playerHunger = new HashMap<Player, Integer>();
    
    /**
     * Location player should be teleported to when joining the game.
     * Should be null if you plan on handling this yourself.
     * 
     * Not used in bungee mode
     */
    public Location gameLocation;

    /**
     * Location player should be teleported to when leaving the game.
     * Should be null if you plan on handling this yourself.
     * 
     * Not used in bungee mode
     */
    public Location outOfGameLocation;

    /**
     * Gets called when a game is created
     */
    public Game() {
        taskId = Bukkit.getScheduler().scheduleSyncRepeatingTask(API.getPlugin(), this, 0, 1);

        Bukkit.getPluginManager().registerEvents(this, API.getPlugin());
    }

    /**
     * Gets called when a game should start
     */
    public void startGame() {
    }

    /**
     * Gets called when a game ends
     */
    public void onGameEnd() {
        currentState.OnStateEnd(players, spectators);
        currentState.OnGameEnd(players, spectators);
        Bukkit.getScheduler().cancelTask(taskId);
        int i = 0;
        for(GamePlayer player : players.values()) {
            if(rankedPlayerList.contains(player)) {
                continue;
            }
            while(rankedPlayerList.get(i) != null) {
                i++;
            }
            rankedPlayerList.set(i, player);
            API.getPlugin().getLogger().info("Adding player to rankings: " + player.name + " / " + i);
            i++;
        }
        GamePlayer[] rankings = new GamePlayer[rankedPlayerList.size()];
        rankings = rankedPlayerList.toArray(rankings);
        API.handler.endGame(rankings);

        for(GamePlayer player : players.values()) {
            player.bukkitPlayer.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
            for(AttributeModifier mod : player.bukkitPlayer.getAttribute(Attribute.GENERIC_MAX_HEALTH).getModifiers()) {
                player.bukkitPlayer.getAttribute(Attribute.GENERIC_MAX_HEALTH).removeModifier(mod);
            }

            PlayerInv.clearInventory(player.bukkitPlayer.getInventory());
            playerInventories.get(player.bukkitPlayer).setInventoryContents(player.bukkitPlayer.getInventory());
            player.bukkitPlayer.setGameMode(playerGamemodes.get(player.bukkitPlayer));
            for(PotionEffect effect : player.bukkitPlayer.getActivePotionEffects()) {
                player.bukkitPlayer.removePotionEffect(effect.getType());
            }
            for(PotionEffect effect : playerEffects.get(player.bukkitPlayer)) {
                player.bukkitPlayer.addPotionEffect(effect);
            }
            player.bukkitPlayer.setHealth(playerHealth.get(player.bukkitPlayer));
            player.bukkitPlayer.setFoodLevel(playerHunger.get(player.bukkitPlayer));
        }
        for(Player player : spectators.values()) {
            player.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
            for(AttributeModifier mod : player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getModifiers()) {
                player.getAttribute(Attribute.GENERIC_MAX_HEALTH).removeModifier(mod);
            }

            PlayerInv.clearInventory(player.getInventory());
            playerInventories.get(player).setInventoryContents(player.getInventory());
            player.setGameMode(playerGamemodes.get(player));
            for(PotionEffect effect : player.getActivePotionEffects()) {
                player.removePotionEffect(effect.getType());
            }
            for(PotionEffect effect : playerEffects.get(player)) {
                player.addPotionEffect(effect);
            }
            player.setHealth(playerHealth.get(player));
            player.setFoodLevel(playerHunger.get(player));
        }

        PlayerDeathEvent.getHandlerList().unregister(this);
        PlayerJoinEvent.getHandlerList().unregister(this);
        PlayerQuitEvent.getHandlerList().unregister(this);

        for(GamePlayer player : rankings) {
            player.bukkitPlayer.sendMessage(ChatColor.DARK_GRAY + "" + ChatColor.STRIKETHROUGH + "                    > " + ChatColor.RESET + "" + ChatColor.GOLD + name + ChatColor.DARK_GRAY + "" + ChatColor.STRIKETHROUGH + " <                    ");
            if(rankings.length > 0) {
                player.bukkitPlayer.sendMessage(ChatColor.GOLD + "First Place: " + ChatColor.RESET + rankings[0].name);
            }
            if(rankings.length > 1) {
                player.bukkitPlayer.sendMessage(ChatColor.GOLD + "Second Place: " + ChatColor.RESET + rankings[1].name);
            }
            if(rankings.length > 2) {
                player.bukkitPlayer.sendMessage(ChatColor.GOLD + "Third Place: " + ChatColor.RESET + rankings[3].name);
            }
            player.bukkitPlayer.sendMessage(ChatColor.DARK_GRAY + "" + ChatColor.STRIKETHROUGH + "                       " + StringUtils.repeat(" ", name.length()) + "                       ");

            player.bukkitPlayer.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
        }
        currentGame = null;
        EntityDamageEvent.getHandlerList().unregister(this);
        PlayerJoinEvent.getHandlerList().unregister(this);
        PlayerQuitEvent.getHandlerList().unregister(this);
    }

    /**
     * Call to change the game state
     */
    public void changeState(GameState state) {
        if(currentGame.currentState != null) {
            currentGame.currentState.OnStateEnd(players, spectators);
        }
        currentGame.currentState = state;
        if(currentGame.currentState != null) {
            currentGame.currentState.OnStateStart(players, spectators);
        }
    }

    public void addPlayer(Player player) {
        GamePlayer gamePlayer = new GamePlayer(player);
        addPlayer(gamePlayer);
    }

    public void addPlayer(GamePlayer player) {
        if(!currentGame.players.containsKey(player.name)) { //If the player is not already in the event
            playerInventories.put(player.bukkitPlayer, new PlayerInv(player.bukkitPlayer.getInventory()));
            playerGamemodes.put(player.bukkitPlayer, player.bukkitPlayer.getGameMode());
            playerEffects.put(player.bukkitPlayer, player.bukkitPlayer.getActivePotionEffects());
            for(PotionEffect effect : playerEffects.get(player.bukkitPlayer)) {
                player.bukkitPlayer.removePotionEffect(effect.getType());
            }
            playerHealth.put(player.bukkitPlayer, player.bukkitPlayer.getHealth());
            playerHunger.put(player.bukkitPlayer, player.bukkitPlayer.getFoodLevel());

            PlayerInv.clearInventory(player.bukkitPlayer.getInventory());
            currentGame.currentState.OnPlayerJoin(player);
            currentGame.players.put(player.name, player);
            
            while(currentGame.rankedPlayerList.size() < currentGame.players.size()) {
                currentGame.rankedPlayerList.add(null);
            }
        }
    }

    /**
     * Call to remove player from the game, but keep them as a spectator.
     */
    public void makePlayerSpectator(GamePlayer player) {
        players.remove(player.name);
        spectators.put(player.name, player.bukkitPlayer);
    }

    /**
     * Call to remove player from the game - makes them go pack to their old location, and all references to the player are destroyed.
     */
    public void removePlayer(Player player) {
        GamePlayer gamePlayer = new GamePlayer(player);
        removePlayer(gamePlayer);
    }

    /**
     * Call to remove player from the game - makes them go pack to their old location, and all references to the player are destroyed.
     */
    public void removePlayer(GamePlayer player) {
        if(currentGame.players.containsKey(player.name)) {

            currentGame.currentState.OnPlayerLeave(player);
            currentGame.players.remove(player.name);
            currentGame.rankedPlayerList.set(currentGame.players.size(), player);
        }
    }

    @Override
    public void run() {
        if(currentGame.currentState != null) {
            currentGame.currentState.OnStateExecute(players, spectators);
        }
    }

    @EventHandler
    public void onDeathEvent(EntityDamageEvent e) {
        if(!(e.getEntity() instanceof Player)) return;

        Player dead = (Player) e.getEntity();
        if(!currentGame.players.containsKey(dead.getName())) {
            return;
        }
        Player killer = null;

        //Try getting a killer
        if(e.getCause() == DamageCause.ENTITY_ATTACK) {
            Entity k = null;
            try {
            k = ((EntityDamageByEntityEvent)e).getDamager();
            } catch(Exception ex) {
                ex.printStackTrace();
            }
            if(k != null && k instanceof Player) {
                killer = (Player) k;
            }
        }

        if(!(dead.getHealth() - e.getDamage() <= 0)) return;

        e.setCancelled(true);
        dead.setHealth(dead.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue());

        if(currentGame.players.containsKey(e.getEntity().getName())) {
            switch(currentGame.currentState.OnPlayerDeath(players.get(dead.getName()), e)) {
                default:
                case DO_NOTHING:
                    break;
                case MAKE_SPECTATOR:
                    currentGame.makePlayerSpectator(players.get(dead.getName()));
                    break;
                case REMOVE_PLAYER:
                    currentGame.removePlayer(players.get(dead.getName()));
                    break;
            }
        }
        if(killer != null && killer instanceof Player) {
            if(currentGame.players.containsKey(killer.getName())) {
                currentGame.currentState.OnPlayerKill(players.get(killer.getName()));
            }
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        if(GamePlayer.toAdd.contains(e.getPlayer().getName())) {
            GamePlayer player = new GamePlayer(e.getPlayer());
            currentGame.addPlayer(player);
            GamePlayer.toAdd.remove(e.getPlayer().getName());
        }
    }

    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent e) {
        currentGame.currentState.OnPlayerLeave(currentGame.players.get(e.getPlayer().getName()));

        if(currentGame.players.containsKey(e.getPlayer().getName())) {
            GamePlayer player = currentGame.players.get(e.getPlayer().getName());

            PlayerInv.clearInventory(player.bukkitPlayer.getInventory());
            playerInventories.get(player.bukkitPlayer).setInventoryContents(player.bukkitPlayer.getInventory());
            player.bukkitPlayer.setGameMode(playerGamemodes.get(player.bukkitPlayer));
            for(PotionEffect effect : player.bukkitPlayer.getActivePotionEffects()) {
                player.bukkitPlayer.removePotionEffect(effect.getType());
            }
            for(PotionEffect effect : playerEffects.get(player.bukkitPlayer)) {
                player.bukkitPlayer.addPotionEffect(effect);
            }
            player.bukkitPlayer.setHealth(playerHealth.get(player.bukkitPlayer));
            player.bukkitPlayer.setFoodLevel(playerHunger.get(player.bukkitPlayer));

            currentGame.removePlayer(currentGame.players.get(e.getPlayer().getName()));
        }
    }
}