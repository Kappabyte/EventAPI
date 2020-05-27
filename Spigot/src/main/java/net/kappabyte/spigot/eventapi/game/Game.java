package net.kappabyte.spigot.eventapi.game;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.attribute.Attribute;
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

import net.kappabyte.spigot.eventapi.API;
import net.kappabyte.spigot.eventapi.game.states.GameState;
import net.kappabyte.spigot.tournament.Tournament;
import net.kappabyte.spigot.tournament.TournamentTeam;
import net.md_5.bungee.api.ChatColor;

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

    private HashMap<Player, GamePlayerData> oldPlayerData = new HashMap<Player, GamePlayerData>();
    private HashMap<Player, GamePlayerData> deadPlayerData = new HashMap<Player, GamePlayerData>();

    public HashMap<Player, Location> oldPlayerLocations = new HashMap<Player, Location>();
    
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
        Bukkit.getWorld("main").getBlockAt(-444, 20, 45).setType(Material.STONE);
        if(currentGame != null) {
            currentGame.cleanup();
        }
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
        //Let the current state know the game ended.
        currentState.OnStateEnd(players, spectators);
        currentState.OnGameEnd(players, spectators);

        //Add untracked players to rankings list
        int i = 0;
        for(GamePlayer player : players.values()) {
            if(rankedPlayerList.contains(player)) {
                continue;
            }
            if(rankedPlayerList.size() - 1 <= i) {
                rankedPlayerList.add(null);
            }
            while(rankedPlayerList.size() > i && rankedPlayerList.get(i) != null) {
                i++;
            }
            rankedPlayerList.set(i, player);
            API.getPlugin().getLogger().info("Adding player to rankings: " + player.name + " / " + i);
            i++;
        }

        //Remove all null elements from the ranked player list
        rankedPlayerList.removeAll(Collections.singleton(null)); 

        //Transform player rankings to strings
        String[] rankings;
        ArrayList<String> _rankings = new ArrayList<String>();
        if(Tournament.getTournamentActive()) {
            //Use team names for the rankings
            ArrayList<Player> playersAddedToRankings = new ArrayList<Player>();

            int rankIndex = 0;
            rankings = new String[Tournament.getTournament().getTeams().size()];

            for(int index = 0; index < rankedPlayerList.size(); index++) {
                GamePlayer player = rankedPlayerList.get(index);
                if(!playersAddedToRankings.contains(player.bukkitPlayer)) {
                    TournamentTeam team = Tournament.getTournament().getTeamWithPlayer(player.bukkitPlayer);
                    if(team != null) {
                        if(team.player1.isOnline()) {
                            playersAddedToRankings.add(team.player1.getPlayer());
                        }
                        if(team.player2.isOnline()) {
                            playersAddedToRankings.add(team.player2.getPlayer());
                        }
                        _rankings.add(team.getTeamName());
                        rankings[rankIndex] = team.getTeamName();
                        rankIndex++;
                    } else {
                        _rankings.add(player.name);
                    }
                }
            }
        } else {
            //Use player names for ranking
            rankings = new String[rankedPlayerList.size()];

            for(int index = 0; index < rankedPlayerList.size(); index++) {
                rankings[index] = rankedPlayerList.get(index).name;
                _rankings.add(rankedPlayerList.get(index).name);
            }
        }

        //Reset player data back to how it was before the game started & send rankings to the players chat
        for(GamePlayer player : players.values()) {
            resetPlayerData(player.bukkitPlayer);

            sendRankingsToPlayer(player.bukkitPlayer, _rankings);
        }
        for(Player player : spectators.values()) {
            resetPlayerData(player);

            sendRankingsToPlayer(player, _rankings);
        }

        //Teleport players who where previosly connected to the server back to
        if(!Tournament.getTournamentActive()) {
            for(Player player : oldPlayerLocations.keySet()) {
                player.teleport(oldPlayerLocations.get(player));
            }

            //Send the ranking data to the back end.
            API.handler.endGame(rankings);
        } else {
            Tournament.getTournament().sendAllPlayersToHub();

            //Send the ranking data to the back end.
            API.handler.endGameNoTP(rankings);
        }

        cleanup();
    }

    public void cleanup() {

        //Unregister events
        PlayerDeathEvent.getHandlerList().unregister(this);
        PlayerJoinEvent.getHandlerList().unregister(this);
        PlayerQuitEvent.getHandlerList().unregister(this);
        EntityDamageEvent.getHandlerList().unregister(this);
        PlayerJoinEvent.getHandlerList().unregister(this);
        PlayerQuitEvent.getHandlerList().unregister(this);

        //Final cleanup
        currentGame = null;
        Bukkit.getScheduler().cancelTask(taskId);
    }

    private void sendRankingsToPlayer(Player player, ArrayList<String> rankings) {
        player.sendMessage(ChatColor.DARK_GRAY + "                    > " + ChatColor.GOLD + name + ChatColor.DARK_GRAY + " <                    ");
        if(rankings.size() > 0) {
            player.sendMessage(ChatColor.GOLD + "First Place: " + ChatColor.RESET + rankings.get(0));
        }
        if(rankings.size() > 1) {
            player.sendMessage(ChatColor.GOLD + "Second Place: " + ChatColor.RESET + rankings.get(1));
        }
        if(rankings.size() > 2) {
            player.sendMessage(ChatColor.GOLD + "Third Place: " + ChatColor.RESET + rankings.get(2));
        }
        player.sendMessage("");
    }

    private void resetPlayerData(Player player) {
        if(oldPlayerData.containsKey(player)) {
            oldPlayerData.get(player).resetPlayerData(player);
        }
    }

    public void setPlayerRanking(GamePlayer player, int ranking) {
        API.getPlugin().getLogger().info("Setting player ranking: " + player.name + " to " + ranking + "\n\nCurrentRankedList:");
        rankedPlayerList.add(ranking, player);
        for(GamePlayer p : rankedPlayerList) {
            if(p != null) {
                API.getPlugin().getLogger().info(rankedPlayerList.indexOf(p) + ": " + p.name);
            }
        }
    }

    /**
     * Call to change the game state
     */
    public void changeState(GameState state) {
        API.getPlugin().getLogger().info("Change State: Stage 1");
        if(currentGame.currentState != null) {
            API.getPlugin().getLogger().info("Change State: Stage 2");
            currentGame.currentState.OnStateEnd(players, spectators);
            API.getPlugin().getLogger().info("Change State: Stage 3");
        }
        API.getPlugin().getLogger().info("Change State: Stage 4");
        currentGame.currentState = state;
        API.getPlugin().getLogger().info("Change State: Stage 5");
        if(currentGame.currentState != null) {
            API.getPlugin().getLogger().info("Change State: Stage 6");
            currentGame.currentState.OnStateStart(players, spectators);
            API.getPlugin().getLogger().info("Change State: Stage 7");
        }
    }

    public void addPlayer(Player player) {
        GamePlayer gamePlayer = new GamePlayer(player);
        addPlayer(gamePlayer);
    }

    private void savePlayerData(Player player) {
        if(!oldPlayerData.containsKey(player)) {
            oldPlayerData.put(player, new GamePlayerData(player, false));
        }
    }

    public void addPlayer(GamePlayer player) {
        if(!currentGame.players.containsKey(player.name)) { //If the player is not already in the event
            savePlayerData(player.bukkitPlayer);
            GamePlayerData.clearPlayerData(player.bukkitPlayer);
            
            //Tell current state a player has joined
            currentGame.currentState.OnPlayerJoin(player);

            //Add the player
            currentGame.players.put(player.name, player);
            
            //Create room in the ranked player list.
            while(currentGame.rankedPlayerList.size() < currentGame.players.size()) {
                currentGame.rankedPlayerList.add(null);
            }
        }
    }

    public void addSpectator(Player player) {
        if(!spectators.containsKey(player.getName()) && !players.containsKey(player.getName())) {
            savePlayerData(player);

            spectators.put(player.getName(), player);

            currentGame.currentState.OnSpectatorJoin(player);
        }
    }

    public void removeSpectator(Player player) {
        if(spectators.containsKey(player.getName())) {
            spectators.remove(player.getName());

            currentGame.currentState.OnSpectatorLeave(player);

            resetPlayerData(player);

            if(oldPlayerLocations.containsKey(player)) {
                player.teleport(oldPlayerLocations.get(player));
            }
        }
    }

    private ArrayList<GamePlayer> markedForSpectator = new ArrayList<GamePlayer>();

    /**
     * Call to remove player from the game, but keep them as a spectator.
     */
    public int makePlayerSpectator(GamePlayer player, boolean addToRankings) {
        markedForSpectator.add(player);
        deadPlayerData.put(player.bukkitPlayer, new GamePlayerData(player.bukkitPlayer, true));
        if(addToRankings) {
            currentGame.rankedPlayerList.add(currentGame.players.size() - 1, player);

            for(GamePlayer _player : players.values()) {
                _player.bukkitPlayer.sendMessage(ChatColor.DARK_RED + "" + ChatColor.BOLD + "Elimination " + ChatColor.RESET + "" + ChatColor.DARK_GRAY + "> " + ChatColor.AQUA + player.name + ChatColor.GRAY + " was eliminated!");
            }
            for(Player _player : spectators.values()) {
                _player.sendMessage(ChatColor.DARK_RED + "" + ChatColor.BOLD + "Elimination " + ChatColor.RESET + "" + ChatColor.DARK_GRAY + "> " + ChatColor.AQUA + player.name + ChatColor.GRAY + " was eliminated!");
            }
        }
        
        //Return number of players left
        if(players.containsKey(player.name)) {
            return players.size() - 1;
        }
        return players.size();
    }

    private ArrayList<GamePlayer> markedForRemoval = new ArrayList<GamePlayer>();

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
    public void removePlayer(Player player, RemoveReason reason) {
        GamePlayer gamePlayer = new GamePlayer(player);
        removePlayer(gamePlayer, reason);
    }

    public enum RemoveReason {
        LEFT_GAME, ELIMINATION
    }

    /**
     * Call to remove player from the game - makes them go pack to their old location, and all references to the player are destroyed.
     */
    public void removePlayer(GamePlayer player) {
        removePlayer(player, RemoveReason.ELIMINATION);
    }

    /**
     * Call to remove player from the game - makes them go pack to their old location, and all references to the player are destroyed.
     */
    public void removePlayer(GamePlayer player, RemoveReason reason) {
        if(currentGame.players.containsKey(player.name)) {
            markedForRemoval.add(player);
            resetPlayerData(player.bukkitPlayer);

            if(oldPlayerLocations.containsKey(player.bukkitPlayer)) {
                player.bukkitPlayer.teleport(oldPlayerLocations.get(player.bukkitPlayer));
            }

            if(reason == RemoveReason.ELIMINATION) {
                for(GamePlayer _player : players.values()) {
                    _player.bukkitPlayer.sendMessage(ChatColor.DARK_RED + "" + ChatColor.BOLD + "Elimination " + ChatColor.RESET + "" + ChatColor.DARK_GRAY + "> " + ChatColor.AQUA + player.name + ChatColor.GRAY + " was eliminated!");
                }
                for(Player _player : spectators.values()) {
                    _player.sendMessage(ChatColor.DARK_RED + "" + ChatColor.BOLD + "Elimination " + ChatColor.RESET + "" + ChatColor.DARK_GRAY + "> " + ChatColor.AQUA + player.name + ChatColor.GRAY + " was eliminated!");
                }
            }

            //Let current state know a player has left
            currentGame.currentState.OnPlayerLeave(player);
            currentGame.rankedPlayerList.set(currentGame.players.size() - 1, player);
        }
    }

    public static boolean revivePlayer(Player player) {
        if(!currentGame.spectators.containsKey(player.getName())) {
            return false;
        }

        GamePlayer gamePlayer = null;

        for(GamePlayer p : currentGame.rankedPlayerList) {
            if(p == null) continue;
            if(p.name.equals(player.getName())) {
                gamePlayer = p;
                currentGame.rankedPlayerList.remove(p);
                break;
            }
        }
        if(gamePlayer == null) {
            gamePlayer = new GamePlayer(player);
        }

        if(currentGame.deadPlayerData.containsKey(player)) {
            currentGame.deadPlayerData.get(player).resetPlayerData(player);
        }

        currentGame.currentState.OnPlayerRevive(gamePlayer);
        currentGame.players.put(player.getName(), gamePlayer);
        currentGame.spectators.remove(player.getName());

        return true;
    }

    @Override
    public void run() {
        //Remove players who are marked for removal, and make players marked for spectator a spectator
        if(currentGame != null) {
            for(GamePlayer player : markedForSpectator) {
                players.remove(player.name);
                spectators.put(player.name, player.bukkitPlayer);
            }
            for(GamePlayer player : markedForRemoval) {
                players.remove(player.name);
            }

            markedForRemoval.clear();
            markedForSpectator.clear();
        }

        //Let states know a tick has passed
        if(currentGame.currentState != null) {
            currentGame.currentState.OnStateExecute(currentGame.players, currentGame.spectators);
        }
    }

    @EventHandler
    public void onDeathEvent(EntityDamageEvent e) {
        //Make sure it's a player that was hurt
        if(!(e.getEntity() instanceof Player)) return;

        //Get person who's (maybe) dead
        Player dead = (Player) e.getEntity();
        if(!currentGame.players.containsKey(dead.getName())) {
            //The player who was hurt is not in the game.
            return;
        }

        //Try getting a killer
        Player killer = null;
        if(e.getCause() == DamageCause.ENTITY_ATTACK) {
            Entity k = null;

            try {
                k = ((EntityDamageByEntityEvent)e).getDamager();
            } catch(Exception ex) {}

            if(k != null && k instanceof Player) {
                killer = (Player) k;
            }
        }

        //If the hit doesn't kill the player
        if(!(dead.getHealth() - e.getDamage() <= 0)) return;

        //Prevent the player from dying, and give them full health
        e.setCancelled(true);
        dead.setHealth(dead.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue());

        switch(currentGame.currentState.OnPlayerDeath(players.get(dead.getName()), e)) {
            default:
            case DO_NOTHING:
                break;
            case MAKE_SPECTATOR:
                currentGame.makePlayerSpectator(players.get(dead.getName()), false);
                for(GamePlayer player : players.values()) {
                    player.bukkitPlayer.playSound(dead.getLocation(), Sound.ENTITY_PLAYER_DEATH, SoundCategory.PLAYERS, 1, 1);
                }
                for(Player spectator : spectators.values()) {
                    spectator.playSound(dead.getLocation(), Sound.ENTITY_PLAYER_DEATH, SoundCategory.PLAYERS, 1, 1);
                }
                break;
            case MAKE_SPECTATOR_RANK:
                currentGame.makePlayerSpectator(players.get(dead.getName()), true);
                for(GamePlayer player : players.values()) {
                    player.bukkitPlayer.playSound(dead.getLocation(), Sound.ENTITY_PLAYER_DEATH, SoundCategory.PLAYERS, 1, 1);
                }
                for(Player spectator : spectators.values()) {
                    spectator.playSound(dead.getLocation(), Sound.ENTITY_PLAYER_DEATH, SoundCategory.PLAYERS, 1, 1);
                }
                break;
            case REMOVE_PLAYER:
                currentGame.removePlayer(players.get(dead.getName()));
                for(GamePlayer player : players.values()) {
                    player.bukkitPlayer.playSound(dead.getLocation(), Sound.ENTITY_PLAYER_DEATH, SoundCategory.PLAYERS, 1, 1);
                }
                for(Player spectator : spectators.values()) {
                    spectator.playSound(dead.getLocation(), Sound.ENTITY_PLAYER_DEATH, SoundCategory.PLAYERS, 1, 1);
                }
                break;
        }

        //Let current state know a player got a kill
        if(killer != null) {
            if(currentGame.players.containsKey(killer.getName())) {
                currentGame.currentState.OnPlayerKill(players.get(killer.getName()));
            }
            //Send death message
            for(GamePlayer player : players.values()) {
                player.bukkitPlayer.sendMessage(ChatColor.AQUA + dead.getName() + ChatColor.GRAY + " was killed by " + ChatColor.AQUA + killer.getName());
            }
            for(Player player : spectators.values()) {
                player.sendMessage(ChatColor.AQUA + dead.getName() + ChatColor.GRAY + " was killed by " + ChatColor.AQUA + killer.getName());
            }
        } else {
            for(GamePlayer player : players.values()) {
                player.bukkitPlayer.sendMessage(ChatColor.AQUA + dead.getName() + ChatColor.GRAY + " died!");
            }
            for(Player player : spectators.values()) {
                player.sendMessage(ChatColor.AQUA + dead.getName() + ChatColor.GRAY + " died!");
            }
        }
    }

    //If bungee mode is used, this adds the player when the player is marked to be added, but wasnt connected already.
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        if(GamePlayer.toAdd.contains(e.getPlayer().getName())) {
            GamePlayer player = new GamePlayer(e.getPlayer());
            currentGame.addPlayer(player);
            GamePlayer.toAdd.remove(e.getPlayer().getName());
        }
        if(GamePlayer.SpectatorsToAdd.contains((e.getPlayer().getName()))) {
            currentGame.addSpectator(e.getPlayer());
            GamePlayer.SpectatorsToAdd.remove(e.getPlayer().getName());
        }
    }

    //Remove the player from the event if they leave the server
    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent e) {
        currentGame.currentState.OnPlayerLeave(currentGame.players.get(e.getPlayer().getName()));

        if(currentGame.players.containsKey(e.getPlayer().getName())) {
            currentGame.removePlayer(currentGame.players.get(e.getPlayer().getName()), RemoveReason.LEFT_GAME);
        }
        if(currentGame.spectators.containsKey(e.getPlayer().getName())) {
            currentGame.removeSpectator(e.getPlayer());
        }
    }
}