package net.kappabyte.spigot.eventapi.game.states;

import java.util.LinkedHashMap;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import net.kappabyte.spigot.eventapi.game.Game;
import net.kappabyte.spigot.eventapi.game.GamePlayer;
import net.kappabyte.spigot.eventapi.game.states.GameState;
import net.kappabyte.spigot.eventapi.scoreboard.Scoreboard;
import net.md_5.bungee.api.ChatColor;

public class StateWaiting extends GameState {

    private String serverName = "your server here";
    private String scoreboardTitle = ChatColor.GOLD + "" + ChatColor.BOLD + "Game";
    private GameState nextState = null;

    public StateWaiting(String serverName, String scoreboardTitle, GameState nextState) {
        this.serverName = serverName;
        this.scoreboardTitle = scoreboardTitle;
        this.nextState = nextState;
    }

    @Override
    public void OnStateStart(LinkedHashMap<String,GamePlayer> players, LinkedHashMap<String,Player> spectators) {
        initiateGame();

        scoreboard = new Scoreboard(scoreboardTitle);
        scoreboard.initBoard();
        scoreboard.setSlotText(4, "");
        scoreboard.setSlotText(3, ChatColor.WHITE + "Waiting...");
        scoreboard.setSlotText(1, "");
        scoreboard.setSlotText(0, ChatColor.translateAlternateColorCodes('&', serverName));

        for(GamePlayer player : players.values()) {
            scoreboard.addPlayer(player);
        }
        for(Player player : spectators.values()) {
            scoreboard.addPlayer(player);
        }
    }

    protected void initiateGame() {
    }

    private int seconds = 30;
    private int ticks = 20;
    
    @Override
    public void OnStateExecute(LinkedHashMap<String,GamePlayer> players, LinkedHashMap<String,Player> spectators) {
        if(Game.currentGame.gameStart) {
            if(ticks == 0) {
                ticks = 20;
                this.seconds--;

                String seconds = this.seconds + "";
                if(this.seconds < 10) {
                    seconds = "0" + this.seconds;
                }
                
                scoreboard.setSlotText(3, ChatColor.WHITE + "Starting: " + ChatColor.GOLD + "0:" + seconds);
                if(this.seconds == 3) {
                    for(GamePlayer player : players.values()) {
                        player.bukkitPlayer.sendTitle(ChatColor.GREEN + "Starting", 3 + "", 10, 70, 20);
                        player.bukkitPlayer.playSound(player.bukkitPlayer.getLocation(), Sound.BLOCK_NOTE_BLOCK_HARP, SoundCategory.MASTER, 10, 1);
                    }
                    
                    for(Player player : spectators.values()) {
                        player.sendTitle(ChatColor.GREEN + "Starting", 3 + "", 10, 70, 20);
                        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_HARP, SoundCategory.MASTER, 10, 1);
                    }
                }
                if(this.seconds == 2) {
                    for(GamePlayer player : players.values()) {
                        player.bukkitPlayer.sendTitle(ChatColor.GREEN + "Starting", 2 + "", 10, 70, 20);
                        player.bukkitPlayer.playSound(player.bukkitPlayer.getLocation(), Sound.BLOCK_NOTE_BLOCK_HARP, SoundCategory.MASTER, 10, 1);
                    }
                    
                    for(Player player : spectators.values()) {
                        player.sendTitle(ChatColor.GREEN + "Starting", 2 + "", 10, 70, 20);
                        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_HARP, SoundCategory.MASTER, 10, 1);
                    }
                }
                if(this.seconds == 1) {
                    for(GamePlayer player : players.values()) {
                        player.bukkitPlayer.sendTitle(ChatColor.GREEN + "Starting", 1 + "", 10, 70, 20);
                        player.bukkitPlayer.playSound(player.bukkitPlayer.getLocation(), Sound.BLOCK_NOTE_BLOCK_HARP, SoundCategory.MASTER, 10, 1);
                    }
                    
                    for(Player player : spectators.values()) {
                        player.sendTitle(ChatColor.GREEN + "Starting", 1 + "", 10, 70, 20);
                        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_HARP, SoundCategory.MASTER, 10, 1);
                    }
                }
                if(this.seconds == 0) {
                    for(GamePlayer player : players.values()) {
                        player.bukkitPlayer.sendTitle(ChatColor.GREEN + "Start!", "", 10, 70, 20);
                        player.bukkitPlayer.playSound(player.bukkitPlayer.getLocation(), Sound.BLOCK_NOTE_BLOCK_HARP, SoundCategory.MASTER, 10, 2);
                    }
                    for(Player player : spectators.values()) {
                        player.sendTitle(ChatColor.GREEN + "Start!", "", 10, 70, 20);
                        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_HARP, SoundCategory.MASTER, 10, 2);
                    }
                    Game.currentGame.changeState(nextState);
                }
            }
            ticks--;
        }
    }

    @Override
    public void OnStateEnd(LinkedHashMap<String, GamePlayer> players, LinkedHashMap<String,Player> spectators) {
        Game.currentGame.initialPlayerCount = players.size();
    }

    @Override
    public void OnPlayerJoin(GamePlayer player) {
        scoreboard.setSlotText(2, ChatColor.WHITE + "Players: " + ChatColor.GOLD + "" + (Game.currentGame.players.size() + 1));

        scoreboard.addPlayer(player);
    }

    @Override
    public void OnPlayerRevive(GamePlayer player) {
        scoreboard.setSlotText(2, ChatColor.WHITE + "Players: " + ChatColor.GOLD + "" + (Game.currentGame.players.size() + 1));

        scoreboard.addPlayer(player);
    }

    @Override
    public void OnSpectatorJoin(Player player) {
        player.setGameMode(GameMode.SPECTATOR);

        scoreboard.addPlayer(player);
    }

    @Override
    public void OnPlayerLeave(GamePlayer player) {
        scoreboard.setSlotText(2, ChatColor.WHITE + "Players: " + ChatColor.GOLD + "" + (Game.currentGame.players.size()));
    }

    @Override
    public PlayerRemoveType OnPlayerDeath(GamePlayer player, EntityDamageEvent e) {
        player.bukkitPlayer.teleport(player.startingLocation);
        return PlayerRemoveType.DO_NOTHING;
    }
}