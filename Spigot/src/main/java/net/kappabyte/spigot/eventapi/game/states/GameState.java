package net.kappabyte.spigot.eventapi.game.states;

import java.util.LinkedHashMap;

import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;

import net.kappabyte.spigot.eventapi.scoreboard.Scoreboard;
import net.kappabyte.spigot.eventapi.game.GamePlayer;

public abstract class GameState {

    public Scoreboard scoreboard;

    public void OnStateStart(LinkedHashMap<String,GamePlayer> players, LinkedHashMap<String,Player> spectators) {
        
    }

    public void OnStateExecute(LinkedHashMap<String,GamePlayer> players, LinkedHashMap<String,Player> spectators) {
        
    }

    public void OnStateEnd(LinkedHashMap<String,GamePlayer> players, LinkedHashMap<String,Player> spectators) {
        
    }

    public void OnGameEnd(LinkedHashMap<String,GamePlayer> players, LinkedHashMap<String,Player> spectators) {
        
    }

    public void OnPlayerJoin(GamePlayer player) {
        
    }

    public void OnSpectatorJoin(Player player) {
        
    }
    
    public void OnPlayerLeave(GamePlayer player) {
        
    }

    public void OnSpectatorLeave(Player player) {

    }

    public void OnPlayerKill(GamePlayer player) {
        
    }

    public void OnPlayerRevive(GamePlayer player) {
        
    }

    public PlayerRemoveType OnPlayerDeath(GamePlayer player, EntityDamageEvent e) {
        return PlayerRemoveType.DO_NOTHING;
    }

    public static enum PlayerRemoveType {
        DO_NOTHING, MAKE_SPECTATOR, MAKE_SPECTATOR_RANK, REMOVE_PLAYER
    }
}